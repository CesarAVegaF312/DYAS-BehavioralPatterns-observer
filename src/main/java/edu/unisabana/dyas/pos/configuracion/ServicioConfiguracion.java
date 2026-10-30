package edu.unisabana.dyas.pos.configuracion;

import edu.unisabana.dyas.pos.configuracion.observadores.ObservadorLog;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Sujeto del patrón Observer: guarda la configuración del POS y avisa cuando cambia.
 *
 * TODO (Paso 3): hoy el servicio crea y avisa directamente a ObservadorLog, y nadie más se entera
 * de los cambios. Cámbielo para que:
 *   - notifique a todos los observadores registrados en Spring (todas las clases que implementan
 *     ObservadorConfiguracion), sin conocer ni importar ninguna clase concreta;
 *   - agregar un observador nuevo no requiera modificar esta clase;
 *   - si un observador lanza una excepción, los demás igual reciban la notificación.
 */
@Service
public class ServicioConfiguracion {

    public static final String FORMATO_FECHA = "formato-fecha";
    public static final String FORMATO_MONEDA = "formato-moneda";

    private final Map<String, String> valores = new ConcurrentHashMap<>();
    private final ObservadorLog observadorLog = new ObservadorLog();

    public ServicioConfiguracion(@Value("${pos.configuracion.formato-fecha}") String formatoFecha,
                                 @Value("${pos.configuracion.formato-moneda}") String formatoMoneda) {
        valores.put(FORMATO_FECHA, formatoFecha);
        valores.put(FORMATO_MONEDA, formatoMoneda);
    }

    public Map<String, String> obtenerTodo() {
        return new TreeMap<>(valores);
    }

    /**
     * Cambia un valor y notifica a los observadores, solo si el valor es distinto del actual.
     */
    public CambioConfiguracion cambiar(String clave, String valorNuevo) {
        validar(clave, valorNuevo);
        String valorAnterior = valores.put(clave, valorNuevo);
        CambioConfiguracion cambio = new CambioConfiguracion(clave, valorAnterior, valorNuevo, Instant.now());
        if (!valorNuevo.equals(valorAnterior)) {
            notificar(cambio);
        }
        return cambio;
    }

    private void notificar(CambioConfiguracion cambio) {
        observadorLog.configuracionCambiada(cambio);
    }

    private void validar(String clave, String valor) {
        try {
            switch (clave) {
                case FORMATO_FECHA -> DateTimeFormatter.ofPattern(valor);
                case FORMATO_MONEDA -> new DecimalFormat(valor);
                default -> throw new ClaveDesconocidaException(clave);
            }
        } catch (IllegalArgumentException e) {
            throw new ValorInvalidoException(clave, valor, e);
        }
    }
}
