package edu.unisabana.dyas.pos.precios;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class FormateadorPreciosTest {

    private final FormateadorPrecios formateador = new FormateadorPrecios("yyyy/MM/dd", "#,##0.00");

    @Test
    void usaLosFormatosIniciales() {
        assertThat(formateador.formatearValor(new BigDecimal("1234.5"))).isEqualTo("1.234,50");
        assertThat(formateador.formatearFecha(LocalDate.of(2026, 10, 30))).isEqualTo("2026/10/30");
    }
}
