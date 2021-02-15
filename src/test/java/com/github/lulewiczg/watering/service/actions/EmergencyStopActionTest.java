package com.github.lulewiczg.watering.service.actions;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
import com.github.lulewiczg.watering.state.AppState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@Import(EmergencyStopAction.class)
@ExtendWith(SpringExtension.class)
class EmergencyStopActionTest {

    @MockBean
    private AppState state;

    @MockBean
    private TapsCloseAction tapsCloseAction;

    @MockBean
    private TanksCloseAction tanksCloseAction;

    @MockBean
    private OutputsCloseAction outputsCloseAction;

    @Autowired
    private EmergencyStopAction action;

    @Test
    void testAction() {
        ActionDto actionDto = new ActionDto();
        when(tanksCloseAction.doAction(argThat(i -> i.getId() != null), eq(null))).thenCallRealMethod();
        when(tapsCloseAction.doAction(argThat(i -> i.getId() != null), eq(null))).thenCallRealMethod();
        when(outputsCloseAction.doAction(argThat(i -> i.getId() != null), eq(null))).thenCallRealMethod();

        ActionResultDto<Void> result = action.doAction(actionDto, null);

        verify(tanksCloseAction).doAction(argThat(i -> i.getId() != null), eq(null));
        verify(tapsCloseAction).doAction(argThat(i -> i.getId() != null), eq(null));
        verify(outputsCloseAction).doAction(argThat(i -> i.getId() != null), eq(null));
        TestUtils.testActionResult(result);
        assertNull(actionDto.getId());
    }

    @Test
    void testActionWithId() {
        String id = "test";
        String id2 = "test.";
        ActionDto actionDto = new ActionDto(id);
        ActionDto nestedDto = new ActionDto(id2);
        when(tanksCloseAction.doAction(nestedDto, null)).thenCallRealMethod();
        when(tapsCloseAction.doAction(nestedDto, null)).thenCallRealMethod();
        when(outputsCloseAction.doAction(nestedDto, null)).thenCallRealMethod();

        ActionResultDto<Void> result = action.doAction(actionDto, null);

        verify(tanksCloseAction).doAction(nestedDto, null);
        verify(tapsCloseAction).doAction(nestedDto, null);
        verify(outputsCloseAction).doAction(nestedDto, null);
        TestUtils.testActionResult(result);
        assertEquals(id, actionDto.getId());
        assertEquals(id, result.getId());
    }

}
