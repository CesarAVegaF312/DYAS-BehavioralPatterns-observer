package edu.unisabana.dyas.pos.api;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.unisabana.dyas.pos.configuracion.CambioConfiguracion;
import edu.unisabana.dyas.pos.configuracion.ClaveDesconocidaException;
import edu.unisabana.dyas.pos.configuracion.ServicioConfiguracion;
import edu.unisabana.dyas.pos.configuracion.ValorInvalidoException;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Contrato HTTP de la configuración: 200, 404 y 400.
 */
@WebMvcTest(ConfiguracionController.class)
class ConfiguracionControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ServicioConfiguracion servicio;

    @Test
    void consultaLaConfiguracion() throws Exception {
        given(servicio.obtenerTodo()).willReturn(Map.of("formato-fecha", "yyyy/MM/dd"));

        mvc.perform(get("/api/configuracion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.formato-fecha").value("yyyy/MM/dd"));
    }

    @Test
    void cambiaUnValor() throws Exception {
        given(servicio.cambiar("formato-fecha", "dd/MM/yyyy"))
                .willReturn(new CambioConfiguracion("formato-fecha", "yyyy/MM/dd", "dd/MM/yyyy", Instant.now()));

        mvc.perform(put("/api/configuracion/formato-fecha").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":\"dd/MM/yyyy\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorAnterior").value("yyyy/MM/dd"))
                .andExpect(jsonPath("$.valorNuevo").value("dd/MM/yyyy"));
    }

    @Test
    void respondeNotFoundSiLaClaveNoExiste() throws Exception {
        given(servicio.cambiar("idioma", "es")).willThrow(new ClaveDesconocidaException("idioma"));

        mvc.perform(put("/api/configuracion/idioma").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":\"es\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void respondeBadRequestSiElValorEsInvalido() throws Exception {
        given(servicio.cambiar("formato-moneda", "#.#.#"))
                .willThrow(new ValorInvalidoException("formato-moneda", "#.#.#", new IllegalArgumentException()));

        mvc.perform(put("/api/configuracion/formato-moneda").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":\"#.#.#\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void respondeBadRequestSiFaltaElValor() throws Exception {
        mvc.perform(put("/api/configuracion/formato-fecha").contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
