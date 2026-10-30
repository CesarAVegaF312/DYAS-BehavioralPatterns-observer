package edu.unisabana.dyas.pos.configuracion.observadores;

import static org.assertj.core.api.Assertions.assertThatCode;

import edu.unisabana.dyas.pos.configuracion.CambioConfiguracion;
import java.time.Instant;
import org.junit.jupiter.api.Test;

/**
 * Prueba del observador de ejemplo. Úsela como modelo para las de los demás observadores.
 */
class ObservadorLogTest {

    @Test
    void registraElCambioSinFallar() {
        CambioConfiguracion cambio = new CambioConfiguracion("formato-fecha", "yyyy/MM/dd", "dd/MM/yyyy", Instant.now());

        assertThatCode(() -> new ObservadorLog().configuracionCambiada(cambio)).doesNotThrowAnyException();
    }
}
