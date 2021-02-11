package com.github.lulewiczg.watering.config;

import com.github.lulewiczg.watering.controller.*;
import com.github.lulewiczg.watering.service.ActionServiceImpl;
import com.github.lulewiczg.watering.service.MasterService;
import com.github.lulewiczg.watering.service.actions.*;
import com.github.lulewiczg.watering.service.io.IOServiceImpl;
import com.github.lulewiczg.watering.service.job.*;
import com.github.lulewiczg.watering.state.MasterState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles({"test", "testMaster"})
class MasterConfigTest {

    @Autowired
    private ApplicationContext ctx;

    @Test
    void testBeans() {
        assertDoesNotThrow(() -> ctx.getBean(ActionMasterController.class));
        assertDoesNotThrow(() -> ctx.getBean(JobMasterController.class));
        assertDoesNotThrow(() -> ctx.getBean(StateMasterController.class));
        assertDoesNotThrow(() -> ctx.getBean(MasterService.class));
        assertDoesNotThrow(() -> ctx.getBean(MasterState.class));

        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(EmergencyStopAction.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(OutputsCloseAction.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(OutputsOpenAction.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(TanksCloseAction.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(TanksOpenAction.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(TapsOpenAction.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(TapsCloseAction.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(ValveOpenAction.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(ValveCloseAction.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(WaterLevelReadAction.class));

        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(ScheduledMasterSync.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(ScheduledOverflowWaterControl.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(ScheduledSensorRead.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(ScheduledValveRead.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(ScheduledWaterEscapeControl.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(ScheduledWaterFillControl.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(ScheduledWatering.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(SetDefaults.class));

        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(IOServiceImpl.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(ActionServiceImpl.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(EmergencyStopAction.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(ActionController.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(JobController.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(ActionController.class));
    }
}