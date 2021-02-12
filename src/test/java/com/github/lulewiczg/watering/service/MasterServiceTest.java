package com.github.lulewiczg.watering.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.service.dto.SlaveStateDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.MasterState;
import com.github.lulewiczg.watering.state.dto.MasterResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles({"test", "testMaster"})
class MasterServiceTest {

    @Autowired
    private MasterService service;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private MasterState masterState;

    @MockBean
    private AppState state;

    @Test
    void testUpdate() {
        SlaveStateDto dto = TestUtils.readJson("slaveState.json", SlaveStateDto.class, mapper);
        ActionDto action = new ActionDto("name","param");
        JobDto job = new JobDto("test");
        when(masterState.getJobs()).thenReturn(new ArrayList<>(List.of(job)));
        when(masterState.getActions()).thenReturn(new ArrayList<>(List.of(action)));

        MasterResponse result = service.update(dto);

        assertEquals(List.of(job), result.getJobs());
        assertEquals(List.of(action), result.getActions());
        assertEquals(List.of(), masterState.getActions());
        assertEquals(List.of(), masterState.getJobs());

        verify(state).setState(dto.getState().getState());
        verify(state).setOutputs(dto.getState().getOutputs());
        verify(state).setTanks(dto.getState().getTanks());
        verify(state).setTaps(dto.getState().getTaps());
        verify(masterState).setActionDefinitions(dto.getActions());
        verify(masterState).setJobDefinitions(dto.getJobs());
    }

}
