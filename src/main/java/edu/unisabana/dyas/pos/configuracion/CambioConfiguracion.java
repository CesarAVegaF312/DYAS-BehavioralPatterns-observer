package edu.unisabana.dyas.pos.configuracion;

import java.time.Instant;

/**
 * Notificación que el sujeto envía a sus observadores cuando cambia un valor de la configuración.
 */
public record CambioConfiguracion(String clave, String valorAnterior, String valorNuevo, Instant fecha) {
}
