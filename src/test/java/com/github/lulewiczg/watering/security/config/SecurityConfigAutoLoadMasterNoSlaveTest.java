package com.github.lulewiczg.watering.security.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestPropertySource(locations = {"classpath:application-appConfigTest.properties", "classpath:application-testSimple.properties", "classpath:application-testMaster.properties"})
class SecurityConfigAutoLoadMasterNoSlaveTest {

    @Autowired
    private SecurityConfig config;

    @Test
    void testPropsLoad() {
        String message = assertThrows(IllegalStateException.class, () -> config.validate()).getMessage();

        assertEquals("Slave user not found!", message);
    }

}