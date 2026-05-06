create table titulares_financieros
(
    titular_id       uuid                        default gen_random_uuid() not null,
    token            varchar(255)                                          not null,
    fecha_registro   timestamp(0) with time zone default now()             not null,
    moneda_preferida varchar(255)                                          not null,
    zona_horaria     varchar(255)                                          not null,
    nombre           varchar(255)                                          not null,
    primer_apellido  varchar(255),
    segundo_apellido varchar(255),
    telefono         varchar(255),
    primary key (titular_id)
);

create table categorias
(
    categoria_id uuid default gen_random_uuid() not null,
    titular_id   uuid                           not null,
    nombre       varchar(255)                   not null,
    primary key (categoria_id),
    foreign key (titular_id) references titulares_financieros
        on update cascade on delete cascade
);

create table metas_ahorro
(
    meta_id        uuid                        default gen_random_uuid()                not null,
    titular_id     uuid                                                                 not null,
    nombre         varchar(255)                                                         not null,
    monto_objetivo double precision                                                     not null,
    fecha_limite   date                                                                 not null,
    estado         varchar(255)                default 'en progreso'::character varying not null,
    fecha_creacion timestamp(0) with time zone default now()                            not null,
    avance         integer                     default 0                                not null,
    primary key (meta_id),
    unique (nombre),
    constraint metas_ahorro_titular_id_foreign
        foreign key (titular_id) references titulares_financieros,
    constraint metas_ahorro_monto_objetivo_check
        check (monto_objetivo > ((0)::numeric)::double precision)
);

create table reportes
(
    reporte_id            uuid                        default gen_random_uuid() not null,
    titular_id            uuid                                                  not null,
    mes                   integer                                               not null,
    anho                  integer                                               not null,
    ingresos_acmds        numeric(15, 2)                                        not null,
    gastos_acmds          numeric(15, 2)                                        not null,
    aportes_ahorros_acmds numeric(15, 2)                                        not null,
    balance_neto          numeric(15, 2)                                        not null,
    fecha_generado        timestamp(0) with time zone default now()             not null,
    primary key (reporte_id),
    constraint reportes_titular_id_foreign
        foreign key (titular_id) references titulares_financieros
);

create table presupuestos
(
    presupuesto_id uuid                        default gen_random_uuid() not null,
    titular_id     uuid                                                  not null,
    monto_limite   numeric(8, 2)                                         not null,
    fecha_creacion timestamp(0) with time zone default now()             not null,
    fecha_inicio   date                                                  not null,
    fecha_final    date                                                  not null,
    primary key (presupuesto_id),
    foreign key (titular_id) references titulares_financieros
        on update cascade on delete cascade,
    constraint presupuestos_monto_limite_check
        check (monto_limite > (0)::numeric)
);

create table transacciones
(
    transaccion_id uuid         default gen_random_uuid() not null,
    titular_id     uuid                                   not null,
    categoria_id   uuid,
    nombre         varchar(150)                           not null,
    monto          numeric(38, 2)                         not null,
    descripcion    varchar(255) default ''::character varying,
    fecha_pago     date                                   not null,
    tipo           varchar(255)                           not null,
    primary key (transaccion_id),
    constraint transacciones_titular_id_foreign
        foreign key (titular_id) references titulares_financieros,
    foreign key (categoria_id) references categorias
        on update cascade on delete cascade,
    constraint transacciones_monto_check
        check (monto > (0)::numeric),
    constraint transacciones_tipo_check
        check ((tipo)::text = ANY
               ((ARRAY ['INGRESO'::character varying, 'GASTO'::character varying, 'APORTE_META'::character varying, 'RETIRO_META'::character varying])::text[]))
);

create table transacciones_metas
(
    meta_id        uuid not null,
    transaccion_id uuid not null,
    primary key (transaccion_id, meta_id),
    constraint transacciones_metas_transaccion_id_foreign
        foreign key (transaccion_id) references transacciones,
    foreign key (meta_id) references metas_ahorro
        on update cascade on delete cascade
);


