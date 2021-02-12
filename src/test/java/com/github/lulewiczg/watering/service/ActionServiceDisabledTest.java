package com.github.lulewiczg.watering.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.exception.ActionNotFoundException;
import com.github.lulewiczg.watering.exception.JobNotFoundException;
import com.github.lulewiczg.watering.service.actions.WaterLevelReadAction;
import com.github.lulewiczg.watering.service.dto.ActionDefinitionDto;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.dto.JobDefinitionDto;
import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.service.job.ScheduledSensorRead;
import com.github.lulewiczg.watering.service.job.ScheduledWatering;
import com.github.lulewiczg.watering.service.job.SetDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles({"testSimple", "testSimpleJobs"})
class ActionServiceDisabledTest {

    @Autowired
    private ActionServiceImpl service;

    @MockBean
    private IOService ioService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void testGetActions() {
        ActionDefinitionDto[] expected = TestUtils.readJson("actions-small.json", ActionDefinitionDto[].class, mapper);

        List<ActionDefinitionDto> actions = service.getActions();

        assertEquals(Arrays.asList(expected), actions);
    }

    @Test
    void testGetJobs() {
        List<JobDefinitionDto> jobs = service.getJobs();
        List<JobDefinitionDto> expected = List.of(ScheduledWatering.class, SetDefaults.class)
                .stream().map(i -> new JobDefinitionDto(deCapitalize(i.getSimpleName()), true)).collect(Collectors.toList());
        assertEquals(expected, jobs);
    }

    @Test
    void testRunAction() {
        ActionDto actionDto = new ActionDto(deCapitalize(WaterLevelReadAction.class.getSimpleName()), "sensor2");

        String message = assertThrows(ActionNotFoundException.class, () -> service.runAction(actionDto)).getMessage();

        assertEquals("Action not found: waterLevelReadAction", message);
    }

    @Test
    void testRunJob() {
        String jobName = deCapitalize(ScheduledSensorRead.class.getSimpleName());

        String message = assertThrows(JobNotFoundException.class, () -> service.runJob(jobName)).getMessage();

        assertEquals("Job not found: scheduledSensorRead", message);
    }

    private String deCapitalize(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}
