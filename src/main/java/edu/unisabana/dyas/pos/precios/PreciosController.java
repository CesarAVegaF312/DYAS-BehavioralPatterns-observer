package edu.unisabana.dyas.pos.precios;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Muestra un valor con el formato de moneda y la fecha de hoy con el formato de fecha vigentes.
 *
 * NO MODIFICAR ESTA CLASE.
 */
@RestController
@RequestMapping("/api/precios")
public class PreciosController {

    public record PrecioFormateado(String valor, String fecha) {
    }

    private final FormateadorPrecios formateador;

    public PreciosController(FormateadorPrecios formateador) {
        this.formateador = formateador;
    }

    @GetMapping
    public PrecioFormateado formatear(@RequestParam BigDecimal valor) {
        return new PrecioFormateado(formateador.formatearValor(valor), formateador.formatearFecha(LocalDate.now()));
    }
}
