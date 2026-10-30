package edu.unisabana.dyas.pos.configuracion.observadores;

/**
 * Observador concreto: guarda cada cambio de la configuración en la tabla auditoria_configuracion de MySQL.
 *
 * TODO (Paso 4):
 *   - Implemente ObservadorConfiguracion y registre la clase como bean de Spring.
 *   - Inserte clave, valor anterior, valor nuevo y fecha con JdbcTemplate y una sentencia parametrizada.
 *   - El historial se consulta con GET /api/auditoria (ya implementado).
 */
public class ObservadorAuditoria {
}
