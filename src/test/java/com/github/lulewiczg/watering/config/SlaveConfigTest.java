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
@ActiveProfiles({"test", "testSlave", "testJobs"})
class SlaveConfigTest {

    @Autowired
    private ApplicationContext ctx;

    @Test
    void testBeans() {
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(ActionMasterController.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(JobMasterController.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(StateMasterController.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(MasterService.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(MasterState.class));

        assertDoesNotThrow(() -> ctx.getBean(EmergencyStopAction.class));
        assertDoesNotThrow(() -> ctx.getBean(OutputsCloseAction.class));
        assertDoesNotThrow(() -> ctx.getBean(OutputsOpenAction.class));
        assertDoesNotThrow(() -> ctx.getBean(TanksCloseAction.class));
        assertDoesNotThrow(() -> ctx.getBean(TanksOpenAction.class));
        assertDoesNotThrow(() -> ctx.getBean(TapsOpenAction.class));
        assertDoesNotThrow(() -> ctx.getBean(TapsCloseAction.class));
        assertDoesNotThrow(() -> ctx.getBean(ValveOpenAction.class));
        assertDoesNotThrow(() -> ctx.getBean(ValveCloseAction.class));
        assertDoesNotThrow(() -> ctx.getBean(WaterLevelReadAction.class));

        assertDoesNotThrow(() -> ctx.getBean(ScheduledMasterSync.class));
        assertDoesNotThrow(() -> ctx.getBean(ScheduledOverflowWaterControl.class));
        assertDoesNotThrow(() -> ctx.getBean(ScheduledSensorRead.class));
        assertDoesNotThrow(() -> ctx.getBean(ScheduledValveRead.class));
        assertDoesNotThrow(() -> ctx.getBean(ScheduledWaterEscapeControl.class));
        assertDoesNotThrow(() -> ctx.getBean(ScheduledWaterFillControl.class));
        assertDoesNotThrow(() -> ctx.getBean(ScheduledWatering.class));
        assertDoesNotThrow(() -> ctx.getBean(SetDefaults.class));

        assertDoesNotThrow(() -> ctx.getBean(ActionServiceImpl.class));
        assertDoesNotThrow(() -> ctx.getBean(EmergencyStopAction.class));
        assertDoesNotThrow(() -> ctx.getBean(ActionController.class));
        assertDoesNotThrow(() -> ctx.getBean(JobController.class));
        assertDoesNotThrow(() -> ctx.getBean(ActionController.class));
    }
}