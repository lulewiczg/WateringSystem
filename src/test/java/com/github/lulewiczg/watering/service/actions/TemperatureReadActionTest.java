package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.state.AppState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@Import(TemperatureReadAction.class)
@ExtendWith(SpringExtension.class)
@PropertySource("classpath:application-test.properties")
class TemperatureReadActionTest {

    @MockitoBean
    private AppState state;

    @Autowired
    private TemperatureReadAction action;

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        String os = System.getProperty("os.name").toLowerCase();
        registry.add("com.github.lulewiczg.watering.temperatureRead.command", () -> {
            if (os.contains("win")) {
                return "cmd;/c;echo;69.1";
            } else {
                return "echo;69.1";
            }
        });
    }

    @Test
    void testTemperatureRead() {
        action.doAction(new ActionDto(), null);

        verify(state).setTemperature(BigDecimal.valueOf(69.1));
    }

    @Test
    void testActionEnabled() {
        assertTrue(action.isEnabled());
    }

}