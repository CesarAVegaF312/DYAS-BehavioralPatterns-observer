package edu.unisabana.dyas.pos.api;

import jakarta.validation.constraints.NotBlank;

/**
 * Cuerpo de la petición PUT /api/configuracion/{clave}.
 */
public record SolicitudCambio(@NotBlank String valor) {
}
