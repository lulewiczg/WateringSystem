package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.service.dto.ActionDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@Import(ShutdownAction.class)
@ExtendWith(SpringExtension.class)
@PropertySource("classpath:application-test.properties")
class ShutdownActionTest {

    @MockitoSpyBean
    private ShutdownAction action;

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        String os = System.getProperty("os.name").toLowerCase();
        registry.add("com.github.lulewiczg.watering.shutdown.command", () -> {
            if (os.contains("win")) {
                return "cmd;/c;echo;shutdown";
            } else {
                return "echo;shutdown";
            }
        });
    }

    @Test
    void testShutdown() throws InterruptedException {
        doNothing().when(action).exit();

        action.doAction(new ActionDto(), null);

        Thread.sleep(100); //Wait for thread to finish
        verify(action).exit();
    }
}