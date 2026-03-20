CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL
);

CREATE TABLE amistades (
    id SERIAL PRIMARY KEY,
    usuario1 VARCHAR(50) NOT NULL,
    usuario2 VARCHAR(50) NOT NULL
);

CREATE TABLE solicitudes_amistad (
    id SERIAL PRIMARY KEY,
    solicitante VARCHAR(50) NOT NULL,
    solicitado VARCHAR(50) NOT NULL,
    estado VARCHAR(20) DEFAULT 'pendiente'
);
