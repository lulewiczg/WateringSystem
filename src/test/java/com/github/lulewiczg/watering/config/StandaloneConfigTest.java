package com.github.lulewiczg.watering.config;

import com.github.lulewiczg.watering.controller.ActionMasterController;
import com.github.lulewiczg.watering.controller.JobMasterController;
import com.github.lulewiczg.watering.controller.StateMasterController;
import com.github.lulewiczg.watering.service.MasterService;
import com.github.lulewiczg.watering.service.job.ScheduledMasterSync;
import com.github.lulewiczg.watering.state.MasterState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles({"test", "testJobs"})
class StandaloneConfigTest {

    @Autowired
    private ApplicationContext ctx;

    @Test
    void testBeans() {
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(ActionMasterController.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(JobMasterController.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(StateMasterController.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(MasterService.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(MasterState.class));
        assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(ScheduledMasterSync.class));
    }
}