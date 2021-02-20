package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Import(ActionRunner.class)
@ExtendWith(SpringExtension.class)
class ActionRunnerTest {

    @Autowired
    private ActionRunner runner;

    @Mock
    private Action<String, Double> action;

    @Mock
    private Action<Void, Void> voidAction;

    @Test
    void testRun() {
        when(action.doAction(new ActionDto(action.getClass().getSimpleName(), "id", action, null), "param"))
                .thenReturn(12.34);

        ActionResultDto<Double> result = runner.run("id", action, "param");

        assertEquals(12.34, result.getResult());
        TestUtils.testActionResult(result);
    }

    @Test
    void testRunVoid() {
        ActionResultDto<Void> result = runner.run("id", voidAction, null);

        TestUtils.testActionResult(result);
        verify(voidAction).doAction(new ActionDto(action.getClass().getSimpleName(), "id", voidAction, null), null);
    }

    @Test
    void testRunNested() {
        when(action.doAction(argThat(i -> i.getId().startsWith("id.") && i.getAction().equals(action)
                && i.getName().equals(action.getClass().getSimpleName())), eq("param")))
                .thenReturn(12.34);

        ActionResultDto<Double> result = runner.run("id.", action, "param");

        assertEquals(12.34, result.getResult());
        TestUtils.testActionResult(result);
        List<String> segments = TestUtils.splitId(result.getId());
        assertEquals(2, segments.size());
        assertEquals("id", segments.get(0));
        assertNotNull(segments.get(1));
    }

    @Test
    void testRunError() {
        when(action.doAction(new ActionDto(action.getClass().getSimpleName(), "id", action, null), "param"))
                .thenThrow(new RuntimeException("some error"));

        ActionResultDto<Double> result = runner.run("id", action, "param");

        TestUtils.testActionResult(result, "some error");
    }

    @Test
    void testRunNoId() {
        when(action.doAction(argThat(i -> i.getId() != null && i.getAction().equals(action)
                && i.getName().equals(action.getClass().getSimpleName())), eq("param")))
                .thenReturn(12.34);

        ActionResultDto<Double> result = runner.run("id", action, "param");

        assertEquals(12.34, result.getResult());
        TestUtils.testActionResult(result);
    }

    @Test
    void testRunErrorNoId() {
        when(action.doAction(argThat(i -> i.getId() != null && i.getAction().equals(action)
                && i.getName().equals(action.getClass().getSimpleName())), eq("param")))
                .thenThrow(new RuntimeException("some error"));

        ActionResultDto<Double> result = runner.run("id", action, "param");

        TestUtils.testActionResult(result, "some error");
    }

    @Test
    void testRunDto() {
        when(action.doAction(new ActionDto(action.getClass().getSimpleName(), "id", action, null), "param"))
                .thenReturn(12.34);

        ActionResultDto<Double> result = runner.run(
                new ActionDto(action.getClass().getSimpleName(), "id", action, null), "param");

        assertEquals(12.34, result.getResult());
        TestUtils.testActionResult(result);
    }

    @Test
    void testRunVoidDto() {
        ActionResultDto<Void> result = runner.run(
                new ActionDto(action.getClass().getSimpleName(), "id", voidAction, null), null);

        TestUtils.testActionResult(result);
        verify(voidAction).doAction(new ActionDto(action.getClass().getSimpleName(), "id", voidAction, null), null);
    }

    @Test
    void testRunNestedDto() {
        when(action.doAction(argThat(i -> i.getId().startsWith("id.") && i.getAction().equals(action)
                && i.getName().equals(action.getClass().getSimpleName())), eq("param")))
                .thenReturn(12.34);

        ActionResultDto<Double> result = runner.run(
                new ActionDto(action.getClass().getSimpleName(), "id.", action, null), "param");

        assertEquals(12.34, result.getResult());
        TestUtils.testActionResult(result);
        List<String> segments = TestUtils.splitId(result.getId());
        assertEquals(2, segments.size());
        assertEquals("id", segments.get(0));
        assertNotNull(segments.get(1));
    }

    @Test
    void testRunErrorDto() {
        when(action.doAction(new ActionDto(action.getClass().getSimpleName(), "id", action, null), "param"))
                .thenThrow(new RuntimeException("some error"));

        ActionResultDto<Double> result = runner.run(
                new ActionDto(action.getClass().getSimpleName(), "id", action, null), "param");

        TestUtils.testActionResult(result, "some error");
    }

    @Test
    void testRunNoIdDto() {
        when(action.doAction(argThat(i -> i.getId() != null && i.getAction().equals(action)
                && i.getName().equals(action.getClass().getSimpleName())), eq("param")))
                .thenReturn(12.34);

        ActionResultDto<Double> result = runner.run(
                new ActionDto(action.getClass().getSimpleName(), null, action, null), "param");

        assertEquals(12.34, result.getResult());
        TestUtils.testActionResult(result);
    }

    @Test
    void testRunErrorNoIdDto() {
        when(action.doAction(argThat(i -> i.getId() != null && i.getAction().equals(action)
                && i.getName().equals(action.getClass().getSimpleName())), eq("param")))
                .thenThrow(new RuntimeException("some error"));

        ActionResultDto<Double> result = runner.run(
                new ActionDto(action.getClass().getSimpleName(), null, action, null), "param");

        TestUtils.testActionResult(result, "some error");
    }
}
