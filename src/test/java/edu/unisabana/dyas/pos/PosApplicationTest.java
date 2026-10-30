package edu.unisabana.dyas.pos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * La aplicación arranca sin necesitar MySQL: la base de datos solo se usa al registrar o consultar la auditoría.
 */
@SpringBootTest
class PosApplicationTest {

    @Test
    void contextoArranca() {
    }
}
