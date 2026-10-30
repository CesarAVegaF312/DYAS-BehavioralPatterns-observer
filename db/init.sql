-- Historial de cambios de la configuración del POS.
-- Lo llena el observador de auditoría (Paso 4) y lo consulta GET /api/auditoria.
CREATE TABLE auditoria_configuracion (
    id             BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    clave          VARCHAR(50)  NOT NULL,
    valor_anterior VARCHAR(100) NOT NULL,
    valor_nuevo    VARCHAR(100) NOT NULL,
    fecha          TIMESTAMP(3) NOT NULL
);
