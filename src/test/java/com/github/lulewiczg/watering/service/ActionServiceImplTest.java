package com.github.lulewiczg.watering.service;

import com.github.lulewiczg.watering.exception.ActionNotFoundException;
import com.github.lulewiczg.watering.exception.JobNotFoundException;
import com.github.lulewiczg.watering.exception.TypeMismatchException;
import com.github.lulewiczg.watering.exception.ValueNotAllowedException;
import com.github.lulewiczg.watering.service.actions.*;
import com.github.lulewiczg.watering.service.actions.dto.WateringDto;
import com.github.lulewiczg.watering.service.dto.ActionDefinitionDto;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.dto.JobDefinitionDto;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.service.job.JobRunner;
import com.github.lulewiczg.watering.service.job.ScheduledJob;
import com.github.lulewiczg.watering.service.job.ScheduledValveRead;
import com.github.lulewiczg.watering.service.job.SetDefaults;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Valve;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "testJobs"})
class ActionServiceImplTest {

    @MockBean
    private AppState state;

    @MockBean
    private IOService ioService;

    @MockBean
    private ApplicationContext applicationContext;

    @MockBean
    private ActionRunner actionRunner;

    @MockBean
    private JobRunner jobRunner;

    @MockBean
    private ActionParamService actionParamService;

    @Mock
    private Action<?, ?> action;

    @Mock
    private SetDefaults setDefaults;

    @Mock
    private Valve valve;

    private ValveOpenAction valveOpenAction;

    private OutputsOpenAction outputsOpenAction;

    private ScheduledValveRead job;

    private ActionServiceImpl service;

    private ActionDefinitionDto valveOpenActionDef;

    private ActionDefinitionDto outputsOpenActionDef;

    @BeforeEach
    void before() {
        service = new ActionServiceImpl(applicationContext, actionRunner, jobRunner, actionParamService);
        valveOpenAction = new ValveOpenAction(ioService, state);
        valveOpenActionDef = new ActionDefinitionDto(deCapitalize(ValveOpenAction.class.getSimpleName()), valveOpenAction.getDescription(),
                valveOpenAction.getParamType(), valveOpenAction.getDestinationParamType(), valveOpenAction.getAllowedValues(),
                valveOpenAction.getParamDescription(), valveOpenAction.getReturnType());
        outputsOpenAction = new OutputsOpenAction(state, valveOpenAction, actionRunner);
        outputsOpenActionDef = new ActionDefinitionDto(deCapitalize(ValveOpenAction.class.getSimpleName()), outputsOpenAction.getDescription(),
                outputsOpenAction.getParamType(), outputsOpenAction.getDestinationParamType(), outputsOpenAction.getAllowedValues(),
                outputsOpenAction.getParamDescription(), outputsOpenAction.getReturnType());
        job = new ScheduledValveRead(state, ioService, jobRunner);
    }

    @Test
    void testGetActions() {
        when(applicationContext.getBeansOfType(Action.class))
                .thenReturn(Map.of(deCapitalize(ValveOpenAction.class.getSimpleName()), valveOpenAction,
                        "action", action));

        List<ActionDefinitionDto> actions = service.getActions();

        List<ActionDefinitionDto> expected = List.of(valveOpenActionDef);
        assertEquals(expected, actions);
    }

    @Test
    void testGetJobs() {
        when(applicationContext.getBeansOfType(ScheduledJob.class))
                .thenReturn(Map.of(deCapitalize(ScheduledValveRead.class.getSimpleName()), job));

        List<JobDefinitionDto> jobs = service.getJobs();

        List<JobDefinitionDto> expected = List.of(
                new JobDefinitionDto(deCapitalize(ScheduledValveRead.class.getSimpleName()), true));
        assertEquals(expected, jobs);
    }

    @Test
    void testRunAction() {
        when(state.getAllValves()).thenReturn(List.of(valve));
        when(valve.getId()).thenReturn("id");
        when(applicationContext.getBeansOfType(Action.class))
                .thenReturn(Map.of(deCapitalize(ValveOpenAction.class.getSimpleName()), valveOpenAction));
        valveOpenActionDef.setAllowedValues(List.of("id"));
        ActionDto dto = new ActionDto(deCapitalize(ValveOpenAction.class.getSimpleName()), "id");
        when(actionParamService.mapParam(valveOpenActionDef, "id")).thenReturn(valve);

        service.runAction(dto);

        verify(actionRunner).run(dto, valve);
    }

    @Test
    void testRunActionNotFound() {
        ActionDto dto = new ActionDto(deCapitalize(ValveOpenAction.class.getSimpleName()), "id");

        String message = assertThrows(ActionNotFoundException.class, () -> service.runAction(dto)).getMessage();

        assertEquals("Action not found: valveOpenAction", message);
    }

    @Test
    void testRunActionInvalidParam() {
        when(state.getAllValves()).thenReturn(List.of(valve));
        when(valve.getId()).thenReturn("id");
        when(applicationContext.getBeansOfType(Action.class))
                .thenReturn(Map.of(deCapitalize(ValveOpenAction.class.getSimpleName()), valveOpenAction));
        valveOpenActionDef.setAllowedValues(List.of("id"));
        ActionDto dto = new ActionDto(deCapitalize(ValveOpenAction.class.getSimpleName()), "invalid");
        when(actionParamService.mapParam(valveOpenActionDef, dto)).thenReturn(valve);

        String message = assertThrows(ValueNotAllowedException.class, () -> service.runAction(dto)).getMessage();

        assertEquals("Value [invalid] does not match [id]!", message);
    }

    @Test
    void testRunActionMissingParam() {
        when(state.getAllValves()).thenReturn(List.of(valve));
        when(valve.getId()).thenReturn("id");
        when(applicationContext.getBeansOfType(Action.class))
                .thenReturn(Map.of(deCapitalize(ValveOpenAction.class.getSimpleName()), valveOpenAction));
        valveOpenActionDef.setAllowedValues(List.of("id"));
        ActionDto dto = new ActionDto(deCapitalize(ValveOpenAction.class.getSimpleName()), null);
        when(actionParamService.mapParam(valveOpenActionDef, dto)).thenReturn(valve);

        String message = assertThrows(TypeMismatchException.class, () -> service.runAction(dto)).getMessage();

        assertEquals("[null] is not valid value for class java.lang.String type!", message);
    }

    @Test
    void testRunActionInvalidParamType() {
        when(state.getAllValves()).thenReturn(List.of(valve));
        when(valve.getId()).thenReturn("id");
        when(applicationContext.getBeansOfType(Action.class))
                .thenReturn(Map.of(deCapitalize(ValveOpenAction.class.getSimpleName()), valveOpenAction));
        valveOpenActionDef.setAllowedValues(List.of("id"));
        ActionDto dto = new ActionDto(deCapitalize(ValveOpenAction.class.getSimpleName()), 1);
        when(actionParamService.mapParam(valveOpenActionDef, dto)).thenReturn(valve);

        String message = assertThrows(TypeMismatchException.class, () -> service.runAction(dto)).getMessage();

        assertEquals("[1] is not valid value for class java.lang.String type!", message);
    }

    @Test
    void testRunActionVoidType() {
        when(state.getAllValves()).thenReturn(List.of(valve));
        when(valve.getId()).thenReturn("id");
        when(applicationContext.getBeansOfType(Action.class))
                .thenReturn(Map.of(deCapitalize(ValveOpenAction.class.getSimpleName()), outputsOpenAction));
        ActionDto dto = new ActionDto(deCapitalize(OutputsOpenAction.class.getSimpleName()), null);
        when(actionParamService.mapParam(outputsOpenActionDef, dto)).thenReturn(valve);

        service.runAction(dto);

        verify(actionRunner).run(dto, null);
    }

    @Test
    void testRunActionVoidWithParam() {
        when(state.getAllValves()).thenReturn(List.of(valve));
        when(valve.getId()).thenReturn("id");
        when(applicationContext.getBeansOfType(Action.class))
                .thenReturn(Map.of(deCapitalize(ValveOpenAction.class.getSimpleName()), outputsOpenAction));
        ActionDto dto = new ActionDto(deCapitalize(OutputsOpenAction.class.getSimpleName()), "some value");
        when(actionParamService.mapParam(outputsOpenActionDef, dto)).thenReturn(valve);

        String message = assertThrows(TypeMismatchException.class, () -> service.runAction(dto)).getMessage();

        assertEquals("[some value] is not valid value for class java.lang.Void type!", message);
    }

    @Test
    void testRunActionWithCustomParam() {
        WateringAction action = Mockito.mock(WateringAction.class);
        when(action.isEnabled()).thenReturn(true);
        when(action.getParamType()).thenReturn((Class) WateringDto.class);
        when(action.getAllowedValues()).thenReturn(null);
        String actionName = deCapitalize(action.getClass().getSimpleName());
        when(applicationContext.getBeansOfType(Action.class)).thenReturn(Map.of(actionName, action));
        Map<String, Object> param = Map.of("valveId", "out", "seconds", 1);
        ActionDto dto = new ActionDto(actionName, param);
        WateringDto expected = new WateringDto("out", null, 1, null);
        when(actionParamService.mapParam(any(), eq(param))).thenReturn(expected);

        service.runAction(dto);

        verify(actionRunner).run(dto, expected);
    }

    @Test
    void testRunActionWithCustomParamMissing() {
        WateringAction action = Mockito.mock(WateringAction.class);
        when(action.isEnabled()).thenReturn(true);
        when(action.getParamType()).thenReturn((Class) WateringDto.class);
        when(action.getAllowedValues()).thenReturn(null);
        String actionName = deCapitalize(action.getClass().getSimpleName());
        when(applicationContext.getBeansOfType(Action.class)).thenReturn(Map.of(actionName, action));
        ActionDto dto = new ActionDto(actionName, null);

        String message = assertThrows(TypeMismatchException.class, () -> service.runAction(dto)).getMessage();

        assertEquals("[null] is not valid value for class com.github.lulewiczg.watering.service.actions.dto.WateringDto type!", message);
    }

    @Test
    void testRunActionWithCustomParamInvalidType() {
        WateringAction action = Mockito.mock(WateringAction.class);
        when(action.isEnabled()).thenReturn(true);
        when(action.getParamType()).thenReturn((Class) WateringDto.class);
        when(action.getAllowedValues()).thenReturn(null);
        String actionName = deCapitalize(action.getClass().getSimpleName());
        when(applicationContext.getBeansOfType(Action.class)).thenReturn(Map.of(actionName, action));
        ActionDto dto = new ActionDto(actionName, 1);

        String message = assertThrows(TypeMismatchException.class, () -> service.runAction(dto)).getMessage();

        assertEquals("[1] is not valid value for class com.github.lulewiczg.watering.service.actions.dto.WateringDto type!", message);
    }

    @Test
    void testRunJob() {
        String name = deCapitalize(setDefaults.getClass().getSimpleName());
        when(applicationContext.getBeansOfType(ScheduledJob.class))
                .thenReturn(Map.of(name, setDefaults));
        when(applicationContext.getBean(name, ScheduledJob.class)).thenReturn(setDefaults);
        when(setDefaults.canBeStarted()).thenReturn(true);
        JobDto dto = new JobDto(name);
        service.runJob(dto);

        verify(jobRunner).run(dto);
    }

    @Test
    void testRunJobNotFound() {
        JobDto jobDto = new JobDto("abc");
        String message = assertThrows(JobNotFoundException.class, () -> service.runJob(jobDto)).getMessage();

        assertEquals("Job not found: abc", message);
    }

    private String deCapitalize(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}
