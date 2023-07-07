package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.exception.ActionException;
import com.github.lulewiczg.watering.service.actions.dto.WateringDto;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Import(WateringAction.class)
@PropertySource("classpath:application-testJobs.properties")
class WateringActionTest {

    @MockBean
    private AppState state;

    @MockBean
    private TanksOpenAction tanksOpenAction;

    @MockBean
    private TanksCloseAction tanksCloseAction;

    @MockBean
    private ValveOpenAction valveOpenAction;

    @MockBean
    private ValveCloseAction valveCloseAction;

    @MockBean
    private ActionRunner runner;

    @Autowired
    private WateringAction action;

    @AfterEach
    void after() {
        verifyNoInteractions(tanksCloseAction);
        verifyNoInteractions(tanksOpenAction);
        verifyNoInteractions(valveOpenAction);
        verifyNoInteractions(valveCloseAction);
    }

    @Test
    void testWateringOk() throws InterruptedException {
        when(runner.run("test.", valveOpenAction, TestUtils.Objects.OUT)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", valveCloseAction, TestUtils.Objects.OUT)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tanksOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        ActionDto actionDto = new ActionDto("test");

        action.doAction(actionDto, new WateringDto("test", TestUtils.Objects.OUT, 1, null));

        verify(state).setState(SystemStatus.WATERING);
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner, never()).run(any(), eq(valveCloseAction), any());
        verify(runner).run("test.", tanksOpenAction, null);
        verify(runner).run("test.", valveOpenAction, TestUtils.Objects.OUT);

        Thread.sleep(1500);

        verify(state).setState(SystemStatus.IDLE);
        verify(runner).run("test.", tanksCloseAction, null);
        verify(runner).run("test.", valveCloseAction, TestUtils.Objects.OUT);
    }

    @Test
    void testWateringMultipleValves() throws InterruptedException {
        when(runner.run("test.", valveOpenAction, TestUtils.Objects.OUT)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", valveCloseAction, TestUtils.Objects.OUT)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", valveOpenAction, TestUtils.Objects.OUT2)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", valveCloseAction, TestUtils.Objects.OUT2)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tanksOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        WateringDto dto = new WateringDto("test", TestUtils.Objects.OUT, 1, null);
        WateringDto dto2 = new WateringDto("test2", TestUtils.Objects.OUT2, 3, null);
        List<WateringDto> dtoList = new ArrayList<>();
        when(state.getRunningWaterings()).thenReturn(dtoList);
        ActionDto actionDto = new ActionDto("test");

        action.doAction(actionDto, dto);
        action.doAction(actionDto, dto2);

        assertEquals(dtoList, List.of(dto, dto2));
        Thread.sleep(1200);
        assertEquals(dtoList, List.of(dto2));

        verify(state, atLeast(1)).setState(SystemStatus.WATERING);
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner, atLeast(1)).run("test.", tanksOpenAction, null);
        verify(runner).run("test.", valveOpenAction, TestUtils.Objects.OUT);
        verify(runner).run("test.", valveOpenAction, TestUtils.Objects.OUT2);

        Thread.sleep(2500);

        verify(state, times(1)).setState(SystemStatus.IDLE);
        verify(runner).run("test.", tanksCloseAction, null);
        verify(runner).run("test.", valveCloseAction, TestUtils.Objects.OUT);
        verify(runner).run("test.", valveCloseAction, TestUtils.Objects.OUT2);
    }

    @Test
    void testWateringTanksOpenFail() {
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tanksOpenAction, null)).thenReturn(TestUtils.ERROR_RESULT);
        ActionDto actionDto = new ActionDto("test");
        WateringDto dto = new WateringDto("test", TestUtils.Objects.OUT, 1, null);

        String error = assertThrows(ActionException.class, () -> action.doAction(actionDto, dto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(state).setState(SystemStatus.WATERING);
        verify(runner).run("test.", tanksOpenAction, null);
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner, never()).run(any(), eq(valveCloseAction), any());
        verify(runner, never()).run(any(), eq(valveOpenAction), any());
    }

    @Test
    void testWateringValveOpenFail() {
        when(runner.run("test.", valveOpenAction, TestUtils.Objects.OUT)).thenReturn(TestUtils.ERROR_RESULT);
        when(runner.run("test.", tanksOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        ActionDto actionDto = new ActionDto("test");
        WateringDto dto = new WateringDto("test", TestUtils.Objects.OUT, 1, null);

        String error = assertThrows(ActionException.class, () -> action.doAction(actionDto, dto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(state).setState(SystemStatus.WATERING);
        verify(runner).run("test.", tanksOpenAction, null);
        verify(runner).run("test.", valveOpenAction, TestUtils.Objects.OUT);
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
        verify(runner, never()).run(any(), eq(valveCloseAction), any());
    }

    @Test
    void testWateringTanksCloseFail() throws InterruptedException {
        when(runner.run("test.", tanksOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", valveOpenAction, TestUtils.Objects.OUT)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", valveCloseAction, TestUtils.Objects.OUT)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.ERROR_RESULT);
        ActionDto actionDto = new ActionDto("test");

        action.doAction(actionDto, new WateringDto("test", TestUtils.Objects.OUT, 1, null));

        Thread.sleep(1500);
        verify(state).setState(SystemStatus.WATERING);
        verify(runner).run("test.", tanksOpenAction, null);
        verify(runner).run("test.", valveOpenAction, TestUtils.Objects.OUT);
        verify(runner).run("test.", tanksCloseAction, null);
    }

    @Test
    void testWateringValveCloseFail() throws InterruptedException {
        when(runner.run("test.", tanksOpenAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", valveOpenAction, TestUtils.Objects.OUT)).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run("test.", valveCloseAction, TestUtils.Objects.OUT)).thenReturn(TestUtils.ERROR_RESULT);
        when(runner.run("test.", tanksCloseAction, null)).thenReturn(TestUtils.EMPTY_RESULT);
        ActionDto actionDto = new ActionDto("test");

        action.doAction(actionDto, new WateringDto("test", TestUtils.Objects.OUT, 1, null));

        Thread.sleep(1500);
        verify(state).setState(SystemStatus.WATERING);
        verify(runner).run("test.", tanksOpenAction, null);
        verify(runner).run("test.", valveOpenAction, TestUtils.Objects.OUT);
        verify(runner).run("test.", valveCloseAction, TestUtils.Objects.OUT);
        verify(runner, never()).run(any(), eq(tanksCloseAction), any());
    }

}