package edu.unisabana.dyas.pos.api;

import java.time.Instant;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Consulta los últimos 50 cambios registrados por el observador de auditoría.
 *
 * NO MODIFICAR ESTA CLASE.
 */
@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    public record RegistroAuditoria(String clave, String valorAnterior, String valorNuevo, Instant fecha) {
    }

    private static final String CONSULTA = """
            SELECT clave, valor_anterior, valor_nuevo, fecha
            FROM auditoria_configuracion
            ORDER BY id DESC
            LIMIT 50""";

    private final JdbcTemplate jdbc;

    public AuditoriaController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping
    public List<RegistroAuditoria> consultar() {
        return jdbc.query(CONSULTA, (rs, i) -> new RegistroAuditoria(
                rs.getString("clave"), rs.getString("valor_anterior"), rs.getString("valor_nuevo"),
                rs.getTimestamp("fecha").toInstant()));
    }
}
