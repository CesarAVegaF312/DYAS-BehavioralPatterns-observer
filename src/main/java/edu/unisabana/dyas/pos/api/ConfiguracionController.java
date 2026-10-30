package edu.unisabana.dyas.pos.api;

import edu.unisabana.dyas.pos.configuracion.CambioConfiguracion;
import edu.unisabana.dyas.pos.configuracion.ClaveDesconocidaException;
import edu.unisabana.dyas.pos.configuracion.ServicioConfiguracion;
import edu.unisabana.dyas.pos.configuracion.ValorInvalidoException;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Consulta y cambia la configuración del POS.
 *
 * NO MODIFICAR ESTA CLASE.
 */
@RestController
@RequestMapping("/api/configuracion")
public class ConfiguracionController {

    private final ServicioConfiguracion servicio;

    public ConfiguracionController(ServicioConfiguracion servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public Map<String, String> consultar() {
        return servicio.obtenerTodo();
    }

    /**
     * 200 con el cambio aplicado; 404 si la clave no existe; 400 si el valor falta o no es un formato válido.
     */
    @PutMapping("/{clave}")
    public CambioConfiguracion cambiar(@PathVariable String clave, @Valid @RequestBody SolicitudCambio solicitud) {
        return servicio.cambiar(clave, solicitud.valor());
    }

    @ExceptionHandler(ClaveDesconocidaException.class)
    public ResponseEntity<Map<String, String>> claveDesconocida(ClaveDesconocidaException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(ValorInvalidoException.class)
    public ResponseEntity<Map<String, String>> valorInvalido(ValorInvalidoException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
