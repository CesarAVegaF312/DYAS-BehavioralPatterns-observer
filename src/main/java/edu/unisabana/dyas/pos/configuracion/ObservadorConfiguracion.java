package edu.unisabana.dyas.pos.configuracion;

/**
 * Observador del patrón Observer: recibe los cambios de la configuración sin tener que consultarla.
 *
 * NO MODIFICAR ESTA INTERFAZ.
 */
public interface ObservadorConfiguracion {

    /**
     * Se invoca después de que un valor de la configuración cambió.
     */
    void configuracionCambiada(CambioConfiguracion cambio);
}
