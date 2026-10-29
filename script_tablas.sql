CREATE DATABASE db_persona;
GO

USE db_persona;
GO

CREATE TABLE tipo_documento (
    id_tipo_documento BIGINT IDENTITY(1,1) PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    descripcion VARCHAR(100) NOT NULL,
    estado BIT NOT NULL DEFAULT 1,
    fecha_registro DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);

CREATE TABLE departamento (
    id_departamento CHAR(2) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    fecha_registro DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);

CREATE TABLE provincia (
    id_provincia CHAR(4) PRIMARY KEY,
    id_departamento  CHAR(2)  NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    fecha_registro DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

    CONSTRAINT FK_PROVINCIA_DEPARTAMENTO
        FOREIGN KEY (id_departamento) REFERENCES departamento(id_departamento)
);

CREATE TABLE distrito (
    id_distrito CHAR(6) PRIMARY KEY,
    id_provincia CHAR(4) NOT NULL,
    id_departamento CHAR(2) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    fecha_registro DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

    CONSTRAINT FK_DISTRITO_PROVINCIA
        FOREIGN KEY (id_provincia) REFERENCES provincia(id_provincia),
    CONSTRAINT FK_DISTRITO_DEPARTAMENTO
        FOREIGN KEY (id_departamento) REFERENCES departamento(id_departamento)
);

CREATE TABLE estado_civil (
    id_estado_civil BIGINT PRIMARY KEY,
    descripcion VARCHAR(50) NOT NULL,
    fecha_registro DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);

CREATE TABLE persona (
    id_persona BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_tipo_documento BIGINT NOT NULL,
    id_estado_civil BIGINT NOT NULL, 
    id_distrito CHAR(6) NOT NULL, 
    numero_documento VARCHAR(20) NOT NULL,
    nombres VARCHAR(150) NOT NULL,
    apellido_paterno VARCHAR(100) NOT NULL,
    apellido_materno VARCHAR(100) NULL,
    fecha_nacimiento DATE NULL,
    genero CHAR(1) NOT NULL CONSTRAINT CK_PERSONA_GENERO CHECK (genero IN ('M', 'F')),
    correo VARCHAR(150) NULL,
    telefono VARCHAR(30) NULL,
    direccion VARCHAR(300) NULL,

    estado BIT NOT NULL DEFAULT 1,

    fecha_registro DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    fecha_actualizacion DATETIME2 NULL,

    CONSTRAINT UQ_PERSONA_NUMERO_DOCUMENTO UNIQUE (numero_documento),

    CONSTRAINT FK_PERSONA_TIPO_DOCUMENTO FOREIGN KEY (id_tipo_documento)
        REFERENCES tipo_documento(id_tipo_documento),

    CONSTRAINT FK_PERSONA_DISTRITO FOREIGN KEY (id_distrito)
        REFERENCES distrito(id_distrito),

    CONSTRAINT FK_PERSONA_ESTADO_CIVIL FOREIGN KEY (id_estado_civil)
		REFERENCES estado_civil(id_estado_civil),
);