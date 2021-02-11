package com.github.lulewiczg.watering.service;

import com.github.lulewiczg.watering.service.dto.ActionDefinitionDto;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.dto.JobDefinitionDto;
import com.github.lulewiczg.watering.state.MasterState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles({"test", "testMaster"})
@Import(ActionServiceMasterImpl.class)
class ActionServiceMasterImplTest {

    @MockBean
    private MasterState state;

    @Autowired
    private ActionService service;

    @Test
    void testGetActions() {
        ActionDefinitionDto action = new ActionDefinitionDto("name", "type", "desc", "ret type");
        when(state.getActionDefinitions()).thenReturn(List.of(action));

        List<ActionDefinitionDto> actions = service.getActions();

        assertEquals(List.of(action), actions);
    }

    @Test
    void testGetJobs() {
        JobDefinitionDto job = new JobDefinitionDto("test", true);
        when(state.getJobDefinitions()).thenReturn(List.of(job));

        List<JobDefinitionDto> actions = service.getJobs();

        assertEquals(List.of(job), actions);
    }

    @Test
    void testRunActionNotFound() {
        ActionDefinitionDto action = new ActionDefinitionDto("name", "type", "desc", "ret type");
        when(state.getActionDefinitions()).thenReturn(List.of(action));
        ActionDto dto = new ActionDto("name2", "type", "param");

        assertThrows(IllegalArgumentException.class, () -> service.runAction(dto));
    }

    @Test
    void testRunAction() {
        ActionDefinitionDto action = new ActionDefinitionDto("name", "type", "desc", "ret type");
        when(state.getActionDefinitions()).thenReturn(List.of(action));

        Object result = service.runAction(new ActionDto("name", "type", "param"));

        assertNull(result);
    }

    @Test
    void testRunJobNotFound() {
        JobDefinitionDto job = new JobDefinitionDto("test", true);
        when(state.getJobDefinitions()).thenReturn(List.of(job));

        assertThrows(IllegalArgumentException.class, () -> service.runJob("test2"));
    }

    @Test
    void testRunJob() {
        JobDefinitionDto job = new JobDefinitionDto("test", true);
        when(state.getJobDefinitions()).thenReturn(List.of(job));

        service.runJob("test");
    }

}