package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.exception.ActionException;
import com.github.lulewiczg.watering.service.actions.ActionRunner;
import com.github.lulewiczg.watering.service.actions.ValveCloseAction;
import com.github.lulewiczg.watering.service.actions.ValveOpenAction;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.service.io.IOService;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Import(SetDefaults.class)
@PropertySource("classpath:application-testJobs.properties")
class SetDefaultsTest {

    @MockBean
    private AppState state;

    @MockBean
    private ValveOpenAction openAction;

    @MockBean
    private ValveCloseAction closeAction;

    @MockBean
    private ActionRunner runner;

    @MockBean
    private JobRunner jobRunner;

    @MockBean
    private IOService ioService;

    @Autowired
    private SetDefaults job;

    @BeforeEach
    void before() {
        TestUtils.standardMock(state);
    }

    @Test
    void testJob() {
        when(runner.run(eq("test."), eq(openAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run(eq("test."), eq(closeAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        JobDto jobDto = new JobDto("test", null);

        job.doJob(jobDto);

        verify(runner).run("test.", openAction, TestUtils.Objects.VALVE);
        verify(runner).run("test.", openAction, TestUtils.Objects.VALVE2);
        verify(runner).run("test.", closeAction, TestUtils.Objects.OUT);
        verify(runner).run("test.", closeAction, TestUtils.Objects.OUT2);
        verify(runner).run("test.", closeAction, TestUtils.Objects.TAP_VALVE);
        verify(ioService, times(1)).toggleOff(RaspiPin.GPIO_10);
    }

    @Test
    void testOpenFail() {
        when(runner.run(eq("test."), eq(openAction), any())).thenReturn(TestUtils.ERROR_RESULT);
        when(runner.run(eq("test."), eq(closeAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        JobDto jobDto = new JobDto("test", null);

        String error = assertThrows(ActionException.class, () -> job.doJob(jobDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(runner).run("test.", openAction, TestUtils.Objects.VALVE);
    }

    @Test
    void testCloseFail() {
        when(runner.run(eq("test."), eq(closeAction), any())).thenReturn(TestUtils.ERROR_RESULT);
        when(runner.run(eq("test."), eq(openAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        JobDto jobDto = new JobDto("test", null);

        String error = assertThrows(ActionException.class, () -> job.doJob(jobDto)).getLocalizedMessage();

        assertEquals("Action [id] failed: error", error);
        verify(runner).run("test.", openAction, TestUtils.Objects.VALVE);
        verify(runner).run("test.", openAction, TestUtils.Objects.VALVE2);
        verify(runner).run("test.", closeAction, TestUtils.Objects.OUT);
    }

    @Test
    void testJobNoTankValve() {
        when(state.getTanks()).thenReturn(List.of(new Tank("id", 100, null, null, null)));
        when(runner.run(eq("test."), eq(openAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        when(runner.run(eq("test."), eq(closeAction), any())).thenReturn(TestUtils.EMPTY_RESULT);
        JobDto jobDto = new JobDto("test", null);

        assertDoesNotThrow(() -> job.doJob(jobDto));
    }

    @ParameterizedTest
    @EnumSource(value = SystemStatus.class)
    void testCanBeRun(SystemStatus status) {
        when(state.getState()).thenReturn(status);

        assertTrue(job.canBeStarted());
    }

    @Test
    void testSchedule() {
        job.schedule(jobRunner);

        verify(jobRunner).run(argThat(i -> i.getId() != null && i.getName().equals(job.getName()) && i.getJob() == job));
    }

}
