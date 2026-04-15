-- ====================
-- HELPERS
-- ====================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE OR REPLACE FUNCTION set_update_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ====================
-- TABLAS DE USUARIO Y PERFIL
-- ====================

CREATE TABLE IF NOT EXISTS usuario  (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email               VARCHAR(255) NOT NULL UNIQUE,
    contrasenha_hash    TEXT NOT NULL,
    fecha_registro      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS perfil (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL UNIQUE,
    nombre VARCHAR(120) NOT NULL,
    telefono VARCHAR(30),
    CONSTRAINT fk_perfil_usuario
                                  FOREIGN KEY (usuario_id)
                                  REFERENCES usuario(id)
                                  ON DELETE CASCADE
);

-- =========================
-- Categoria
-- =========================
CREATE TABLE IF NOT EXISTS categoria (
  categoria_id     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  usuario_id       UUID NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
  nombre           VARCHAR(100) NOT NULL,
  tipo             VARCHAR(20) NOT NULL CHECK (tipo IN ('ingreso', 'gasto')),
  created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE (usuario_id, nombre, tipo)
);

-- =========================
-- Transaccion
-- =========================
CREATE TABLE IF NOT EXISTS transaccion (
  id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  usuario_id       UUID NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
  categoria_id     UUID REFERENCES categoria(id) ON DELETE SET NULL,
  nombre           VARCHAR(150) NOT NULL,
  monto            NUMERIC(14,2) NOT NULL CHECK (monto > 0),
  fecha            DATE NOT NULL DEFAULT CURRENT_DATE,
  descripcion      TEXT,
  tipo             VARCHAR(20) NOT NULL CHECK (tipo IN ('ingreso', 'gasto', 'transferencia', 'ajuste')),
  fecha_creacion   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- =========================
-- Meta de ahorro
-- =========================
CREATE TABLE IF NOT EXISTS meta_ahorro (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  usuario_id           UUID NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
  nombre               VARCHAR(120) NOT NULL,
  monto_objetivo       NUMERIC(14,2) NOT NULL CHECK (monto_objetivo > 0),
  monto_actual         NUMERIC(14,2) NOT NULL DEFAULT 0 CHECK (monto_actual >= 0),
  fecha_limite         DATE,
  estado               VARCHAR(20) NOT NULL DEFAULT 'en progreso'
                        CHECK (estado IN ('en progreso', 'completada', 'vencida', 'eliminada')),
  notificacion_activas BOOLEAN NOT NULL DEFAULT TRUE,
  created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  transaccion_id        UUID REFERENCES transaccion(id) ON DELETE CASCADE
);

-- Relación N:M transaccion-meta
CREATE TABLE IF NOT EXISTS transaccion_meta (
  transaccion_meta_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  transaccion_id      UUID NOT NULL REFERENCES transaccion(id) ON DELETE CASCADE,
  meta_id             UUID NOT NULL REFERENCES meta_ahorro(id) ON DELETE CASCADE,
  UNIQUE (transaccion_id, meta_id)
);

-- =========================
-- Presupuesto
-- =========================
CREATE TABLE IF NOT EXISTS presupuesto (
  id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  usuario_id       UUID NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
  monto_total      NUMERIC(14,2) NOT NULL CHECK (monto_total > 0),
  periodo          VARCHAR(20) NOT NULL CHECK (periodo IN ('semanal', 'quincenal', 'mensual', 'anual')),
  fecha_creacion     DATE NOT NULL
);

-- =========================
-- Reporte
-- =========================
CREATE TABLE IF NOT EXISTS reporte (
  id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  usuario_id         UUID NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
  mes                SMALLINT NOT NULL CHECK (mes BETWEEN 1 AND 12),
  anho               INT NOT NULL CHECK (anho >= 2000),
  total_ingresos     NUMERIC(14,2) NOT NULL DEFAULT 0,
  total_gastos       NUMERIC(14,2) NOT NULL DEFAULT 0,
  balance_neto       NUMERIC(14,2) NOT NULL DEFAULT 0,
  ahorro             NUMERIC(14,2) NOT NULL DEFAULT 0,
  fecha_generacion   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE (usuario_id, mes, anho)
);

-- =========================
-- Notificaciones
-- =========================
CREATE TABLE IF NOT EXISTS notificacion (
  id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  usuario_id        UUID NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
  meta_id           UUID REFERENCES meta_ahorro(id) ON DELETE SET NULL,
  presupuesto_id    UUID REFERENCES presupuesto(id) ON DELETE SET NULL,
  reporte_id        UUID REFERENCES reporte(id) ON DELETE SET NULL,
  tipo              VARCHAR(30) NOT NULL CHECK (tipo IN ('Alerta de presupuesto', 'Vencimiento de Meta', 'Meta completada', 'Meta sin aportes', 'Meta vencida', 'Reporte generado')),
  mensaje           TEXT,
  leida             BOOLEAN NOT NULL DEFAULT FALSE
);
