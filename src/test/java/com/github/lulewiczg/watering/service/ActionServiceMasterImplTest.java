package com.github.lulewiczg.watering.service;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.exception.ActionNotFoundException;
import com.github.lulewiczg.watering.exception.JobNotFoundException;
import com.github.lulewiczg.watering.exception.TypeMismatchException;
import com.github.lulewiczg.watering.exception.ValueNotAllowedException;
import com.github.lulewiczg.watering.service.dto.*;
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

    private final ActionDefinitionDto actionDef = new ActionDefinitionDto("name", "desc",
            String.class, String.class, null, "param desc", String.class);

    private final ActionDefinitionDto actionDefAllowedValues = new ActionDefinitionDto("name", "desc",
            String.class, String.class, List.of("val"), "param desc", String.class);

    private final ActionDefinitionDto actionDefVoid = new ActionDefinitionDto("name", "desc",
            Void.class, Void.class, null, "param desc", String.class);

    @Test
    void testGetActions() {
        when(state.getActionDefinitions()).thenReturn(List.of(actionDef));

        List<ActionDefinitionDto> actions = service.getActions();

        assertEquals(List.of(actionDef), actions);
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
        when(state.getActionDefinitions()).thenReturn(List.of(actionDef));
        ActionDto dto = new ActionDto("name2", "param");

        String message = assertThrows(ActionNotFoundException.class, () -> service.runAction(dto)).getMessage();

        assertEquals("Action not found: name2", message);
    }

    @Test
    void testRunAction() {
        when(state.getActionDefinitions()).thenReturn(List.of(actionDef));

        ActionResultDto<?> result = service.runAction(new ActionDto("name", "param"));

        assertNotNull(result.getId());
        assertNull(result.getErrorMsg());
        assertEquals("name", result.getResult());
    }

    @Test
    void testRunActionMissingParam() {
        when(state.getActionDefinitions()).thenReturn(List.of(actionDef));
        ActionDto dto = new ActionDto("name", null);

        String message = assertThrows(TypeMismatchException.class, () -> service.runAction(dto)).getMessage();

        assertEquals("[null] is not valid value for class java.lang.String type!", message);
    }

    @Test
    void testRunActionVoidWithParam() {
        when(state.getActionDefinitions()).thenReturn(List.of(actionDefVoid));
        ActionDto dto = new ActionDto("name", "test");

        String message = assertThrows(TypeMismatchException.class, () -> service.runAction(dto)).getMessage();

        assertEquals("[test] is not valid value for class java.lang.Void type!", message);
    }

    @Test
    void testRunActionInvalidParamType() {
        when(state.getActionDefinitions()).thenReturn(List.of(actionDef));
        ActionDto dto = new ActionDto("name", 1);

        String message = assertThrows(TypeMismatchException.class, () -> service.runAction(dto)).getMessage();

        assertEquals("[1] is not valid value for class java.lang.String type!", message);
    }

    @Test
    void testRunActionAllowedValue() {
        when(state.getActionDefinitions()).thenReturn(List.of(actionDefAllowedValues));

        ActionResultDto<?> result = service.runAction(new ActionDto("name", "val"));

        assertNotNull(result.getId());
        assertNull(result.getErrorMsg());
        assertEquals("name", result.getResult());
    }

    @Test
    void testRunActionInvalidParam() {
        when(state.getActionDefinitions()).thenReturn(List.of(actionDefAllowedValues));
        ActionDto dto = new ActionDto("name", "invalid");

        String message = assertThrows(ValueNotAllowedException.class, () -> service.runAction(dto)).getMessage();

        assertEquals("Value [invalid] does not match [val]!", message);
    }

    @Test
    void testRunJobNotFound() {
        JobDefinitionDto job = new JobDefinitionDto("test", true);
        when(state.getJobDefinitions()).thenReturn(List.of(job));

        String message = assertThrows(JobNotFoundException.class, () -> service.runJob(new JobDto("test2"))).getMessage();

        assertEquals("Job not found: test2", message);
    }

    @Test
    void testRunJob() {
        JobDefinitionDto job = new JobDefinitionDto("test", true);
        when(state.getJobDefinitions()).thenReturn(List.of(job));

        ActionResultDto<?> result = service.runJob(new JobDto("test"));

        assertNotNull(result.getId());
        assertNull(result.getErrorMsg());
        assertEquals("test", result.getResult());
    }

    @Test
    void testRunDisabledJob() {
        JobDefinitionDto job = new JobDefinitionDto("test", false);
        when(state.getJobDefinitions()).thenReturn(List.of(job));

        String message = assertThrows(JobNotFoundException.class, () -> service.runJob(new JobDto("test"))).getMessage();

        assertEquals("Job not found: test", message);
    }

}
