package edu.unisabana.dyas.pos.configuracion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/**
 * Pruebas del sujeto sin observadores. Agregue las de la notificación en el Paso 6.
 */
class ServicioConfiguracionTest {

    private final ServicioConfiguracion servicio = crearServicio();

    // TODO (Paso 3): cuando el constructor reciba los observadores, ajuste este método.
    private static ServicioConfiguracion crearServicio() {
        return new ServicioConfiguracion("yyyy/MM/dd", "#,##0.00");
    }

    @Test
    void cambiaUnValorValido() {
        CambioConfiguracion cambio = servicio.cambiar("formato-fecha", "dd/MM/yyyy");

        assertThat(cambio.valorAnterior()).isEqualTo("yyyy/MM/dd");
        assertThat(cambio.valorNuevo()).isEqualTo("dd/MM/yyyy");
        assertThat(servicio.obtenerTodo()).containsEntry("formato-fecha", "dd/MM/yyyy");
    }

    @Test
    void rechazaUnaClaveDesconocida() {
        assertThatThrownBy(() -> servicio.cambiar("idioma", "es"))
                .isInstanceOf(ClaveDesconocidaException.class);
    }

    @Test
    void rechazaUnFormatoInvalido() {
        assertThatThrownBy(() -> servicio.cambiar("formato-fecha", "yyyy-MM-dd {"))
                .isInstanceOf(ValorInvalidoException.class);
        assertThatThrownBy(() -> servicio.cambiar("formato-moneda", "#,##0.00.00"))
                .isInstanceOf(ValorInvalidoException.class);
        assertThat(servicio.obtenerTodo()).containsEntry("formato-moneda", "#,##0.00");
    }
}
