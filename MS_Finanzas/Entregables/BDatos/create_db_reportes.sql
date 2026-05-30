-- =============================================================================
-- MS_Reportes - Base de datos del microservicio de reportes
-- PostgreSQL 14+
-- Independiente de MS_Finanzas: titular_id es referencia lógica (sin FK externa)
-- =============================================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ---------------------------------------------------------------------------
-- Tipos / dominios
-- ---------------------------------------------------------------------------
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'estado_reporte') THEN
        CREATE TYPE estado_reporte AS ENUM ('GENERADO', 'ARCHIVADO', 'ERROR');
    END IF;
END$$;

-- ---------------------------------------------------------------------------
-- Tabla principal: reporte mensual del titular
-- ---------------------------------------------------------------------------
DROP TABLE IF EXISTS reporte_movimiento CASCADE;
DROP TABLE IF EXISTS reporte_gasto_categoria CASCADE;
DROP TABLE IF EXISTS auditoria_reporte CASCADE;
DROP TABLE IF EXISTS reportes CASCADE;

CREATE TABLE reportes (
    reporte_id              UUID            NOT NULL DEFAULT gen_random_uuid(),
    titular_id              UUID            NOT NULL,
    mes                     INTEGER         NOT NULL
        CONSTRAINT reportes_mes_check CHECK (mes BETWEEN 1 AND 12),
    anho                    INTEGER         NOT NULL
        CONSTRAINT reportes_anho_check CHECK (anho BETWEEN 1900 AND 2100),

    ingresos_total          NUMERIC(15, 2)  NOT NULL DEFAULT 0,
    gastos_total            NUMERIC(15, 2)  NOT NULL DEFAULT 0,
    aportes_meta_total      NUMERIC(15, 2)  NOT NULL DEFAULT 0,
    retiros_meta_total      NUMERIC(15, 2)  NOT NULL DEFAULT 0,
    balance_neto            NUMERIC(15, 2)  NOT NULL DEFAULT 0,

    moneda                  VARCHAR(10)     NOT NULL DEFAULT 'COP',
    estado                  estado_reporte  NOT NULL DEFAULT 'GENERADO',
    fecha_generado          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    CONSTRAINT reportes_pkey PRIMARY KEY (reporte_id),
    CONSTRAINT reportes_titular_periodo_uk UNIQUE (titular_id, anho, mes),
    CONSTRAINT reportes_gastos_nonneg CHECK (gastos_total >= 0),
    CONSTRAINT reportes_ingresos_nonneg CHECK (ingresos_total >= 0)
);

COMMENT ON TABLE reportes IS 'Snapshot mensual financiero por titular (cuenta Finanzas)';
COMMENT ON COLUMN reportes.titular_id IS 'UUID del titular en MS_Finanzas; sin FK cross-DB';
COMMENT ON COLUMN reportes.gastos_total IS 'Suma de transacciones tipo GASTO del mes';

CREATE INDEX idx_reportes_titular_id ON reportes (titular_id);
CREATE INDEX idx_reportes_periodo ON reportes (anho DESC, mes DESC);
CREATE INDEX idx_reportes_fecha_generado ON reportes (fecha_generado DESC);

-- ---------------------------------------------------------------------------
-- Desglose de GASTOS por categoría
-- ---------------------------------------------------------------------------
CREATE TABLE reporte_gasto_categoria (
    id                      UUID            NOT NULL DEFAULT gen_random_uuid(),
    reporte_id              UUID            NOT NULL,
    categoria_id            UUID,
    categoria_nombre        VARCHAR(255)    NOT NULL,
    monto_total             NUMERIC(15, 2)  NOT NULL DEFAULT 0,
    porcentaje_del_total    NUMERIC(5, 2),
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    CONSTRAINT reporte_gasto_categoria_pkey PRIMARY KEY (id),
    CONSTRAINT reporte_gasto_categoria_reporte_fk
        FOREIGN KEY (reporte_id) REFERENCES reportes (reporte_id)
        ON DELETE CASCADE,
    CONSTRAINT reporte_gasto_categoria_monto_check CHECK (monto_total >= 0),
    CONSTRAINT reporte_gasto_categoria_uk UNIQUE (reporte_id, categoria_nombre)
);

CREATE INDEX idx_reporte_gasto_categoria_reporte ON reporte_gasto_categoria (reporte_id);

-- ---------------------------------------------------------------------------
-- Detalle de movimientos incluidos en el reporte
-- ---------------------------------------------------------------------------
CREATE TABLE reporte_movimiento (
    id                      UUID            NOT NULL DEFAULT gen_random_uuid(),
    reporte_id              UUID            NOT NULL,
    transaccion_id          UUID            NOT NULL,
    tipo                    VARCHAR(30)     NOT NULL,
    nombre                  VARCHAR(150)    NOT NULL,
    descripcion             VARCHAR(255),
    monto                   NUMERIC(15, 2)  NOT NULL,
    fecha_pago              DATE            NOT NULL,
    categoria_nombre        VARCHAR(255),
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    CONSTRAINT reporte_movimiento_pkey PRIMARY KEY (id),
    CONSTRAINT reporte_movimiento_reporte_fk
        FOREIGN KEY (reporte_id) REFERENCES reportes (reporte_id)
        ON DELETE CASCADE,
    CONSTRAINT reporte_movimiento_monto_check CHECK (monto > 0),
    CONSTRAINT reporte_movimiento_tipo_check CHECK (
        tipo IN ('INGRESO', 'GASTO', 'APORTE_META', 'RETIRO_META')
    ),
    CONSTRAINT reporte_movimiento_uk UNIQUE (reporte_id, transaccion_id)
);

CREATE INDEX idx_reporte_movimiento_reporte ON reporte_movimiento (reporte_id);
CREATE INDEX idx_reporte_movimiento_tipo ON reporte_movimiento (reporte_id, tipo);
CREATE INDEX idx_reporte_movimiento_fecha ON reporte_movimiento (reporte_id, fecha_pago);

-- ---------------------------------------------------------------------------
-- Auditoría
-- ---------------------------------------------------------------------------
CREATE TABLE auditoria_reporte (
    auditoria_id            UUID            NOT NULL DEFAULT gen_random_uuid(),
    reporte_id              UUID,
    titular_id              UUID,
    accion                  VARCHAR(50)     NOT NULL,
    detalle                 TEXT,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    CONSTRAINT auditoria_reporte_pkey PRIMARY KEY (auditoria_id),
    CONSTRAINT auditoria_reporte_reporte_fk
        FOREIGN KEY (reporte_id) REFERENCES reportes (reporte_id)
        ON DELETE SET NULL
);

CREATE INDEX idx_auditoria_reporte_reporte ON auditoria_reporte (reporte_id);
CREATE INDEX idx_auditoria_reporte_titular ON auditoria_reporte (titular_id);
CREATE INDEX idx_auditoria_reporte_created ON auditoria_reporte (created_at DESC);

-- ---------------------------------------------------------------------------
-- Trigger updated_at
-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_reportes_updated_at
    BEFORE UPDATE ON reportes
    FOR EACH ROW
    EXECUTE FUNCTION fn_set_updated_at();

-- ---------------------------------------------------------------------------
-- Procedimiento almacenado: upsert del resumen mensual
-- ---------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_upsert_reporte_mensual(
    p_titular_id         UUID,
    p_mes                INTEGER,
    p_anho               INTEGER,
    p_ingresos           NUMERIC(15, 2),
    p_gastos             NUMERIC(15, 2),
    p_aportes_meta       NUMERIC(15, 2),
    p_retiros_meta       NUMERIC(15, 2),
    p_moneda             VARCHAR(10) DEFAULT 'COP',
    OUT p_reporte_id     UUID
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_balance NUMERIC(15, 2);
BEGIN
    v_balance := COALESCE(p_ingresos, 0)
               - COALESCE(p_gastos, 0)
               + COALESCE(p_retiros_meta, 0)
               - COALESCE(p_aportes_meta, 0);

    INSERT INTO reportes (
        titular_id, mes, anho,
        ingresos_total, gastos_total,
        aportes_meta_total, retiros_meta_total,
        balance_neto, moneda, estado, fecha_generado
    )
    VALUES (
        p_titular_id, p_mes, p_anho,
        COALESCE(p_ingresos, 0), COALESCE(p_gastos, 0),
        COALESCE(p_aportes_meta, 0), COALESCE(p_retiros_meta, 0),
        v_balance, COALESCE(p_moneda, 'COP'), 'GENERADO', NOW()
    )
    ON CONFLICT (titular_id, anho, mes)
    DO UPDATE SET
        ingresos_total     = EXCLUDED.ingresos_total,
        gastos_total       = EXCLUDED.gastos_total,
        aportes_meta_total = EXCLUDED.aportes_meta_total,
        retiros_meta_total = EXCLUDED.retiros_meta_total,
        balance_neto       = EXCLUDED.balance_neto,
        moneda             = EXCLUDED.moneda,
        estado             = 'GENERADO',
        fecha_generado     = NOW(),
        updated_at         = NOW()
    RETURNING reporte_id INTO p_reporte_id;

    INSERT INTO auditoria_reporte (reporte_id, titular_id, accion, detalle)
    VALUES (
        p_reporte_id,
        p_titular_id,
        'GENERAR',
        format('Reporte %s/%s titular %s', p_mes, p_anho, p_titular_id)
    );
END;
$$;

-- ---------------------------------------------------------------------------
-- Vista: gastos del mes por titular
-- ---------------------------------------------------------------------------
CREATE OR REPLACE VIEW vw_gastos_mensuales_por_titular AS
SELECT
    r.reporte_id,
    r.titular_id,
    r.anho,
    r.mes,
    r.gastos_total,
    r.fecha_generado,
    gc.categoria_nombre,
    gc.monto_total,
    gc.porcentaje_del_total
FROM reportes r
LEFT JOIN reporte_gasto_categoria gc ON gc.reporte_id = r.reporte_id
ORDER BY r.anho DESC, r.mes DESC, gc.monto_total DESC NULLS LAST;
