package edu.unisabana.dyas.pos.configuracion.observadores;

import edu.unisabana.dyas.pos.configuracion.CambioConfiguracion;
import edu.unisabana.dyas.pos.configuracion.ObservadorConfiguracion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Observador concreto: escribe cada cambio de la configuración en el log de la aplicación.
 *
 * Ejemplo guía: los demás observadores siguen esta misma forma.
 */
@Component
public class ObservadorLog implements ObservadorConfiguracion {

    private static final Logger LOG = LoggerFactory.getLogger(ObservadorLog.class);

    @Override
    public void configuracionCambiada(CambioConfiguracion cambio) {
        LOG.info("Configuración cambiada: {} de '{}' a '{}'",
                cambio.clave(), cambio.valorAnterior(), cambio.valorNuevo());
    }
}
