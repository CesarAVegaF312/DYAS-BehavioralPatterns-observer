package edu.unisabana.dyas.pos.precios;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Da formato a los precios y fechas que muestra el POS.
 *
 * TODO (Paso 5): hoy usa siempre los formatos con los que arrancó la aplicación, así que un cambio
 * con PUT /api/configuracion no se refleja en GET /api/precios. Conviértalo en observador para que
 * actualice sus formatos cuando cambien formato-fecha o formato-moneda, sin consultar
 * ServicioConfiguracion en cada petición.
 */
@Component
public class FormateadorPrecios {

    private static final Locale COLOMBIA = Locale.forLanguageTag("es-CO");

    private String formatoMoneda;
    private DateTimeFormatter formatoFecha;

    public FormateadorPrecios(@Value("${pos.configuracion.formato-fecha}") String formatoFecha,
                              @Value("${pos.configuracion.formato-moneda}") String formatoMoneda) {
        this.formatoFecha = DateTimeFormatter.ofPattern(formatoFecha);
        this.formatoMoneda = formatoMoneda;
    }

    public String formatearValor(BigDecimal valor) {
        // DecimalFormat no es seguro entre hilos: se crea uno por llamada.
        return new DecimalFormat(formatoMoneda, DecimalFormatSymbols.getInstance(COLOMBIA)).format(valor);
    }

    public String formatearFecha(LocalDate fecha) {
        return formatoFecha.format(fecha);
    }
}
