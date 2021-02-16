package com.github.lulewiczg.watering.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.security.AuthEntryPoint;
import com.github.lulewiczg.watering.security.AuthProvider;
import com.github.lulewiczg.watering.service.MasterService;
import com.github.lulewiczg.watering.service.dto.SlaveStateDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.MasterState;
import com.github.lulewiczg.watering.state.dto.MasterResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles({"test", "testMaster"})
@ExtendWith(SpringExtension.class)
@WebMvcTest(StateMasterController.class)
@Import({AuthEntryPoint.class, AuthProvider.class, MasterConfig.class})
class StateMasterControllerMasterTest {

    @MockBean
    private MasterService service;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @WithMockUser(roles = "SLAVE")
    void testUpdateStateSlave() throws Exception {
        AppState appState = TestUtils.readJson("state.json", AppState.class, mapper);
        SlaveStateDto slaveState = new SlaveStateDto(appState, List.of(), List.of(), null, null); //TODO
        MasterResponse response = TestUtils.readJson("masterResponse.json", MasterResponse.class, mapper);
        when(service.update(slaveState)).thenReturn(response);

        mvc.perform(post("/rest/state")
                .content(mapper.writeValueAsString(slaveState))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdateState() {
        AppState appState = TestUtils.readJson("state.json", AppState.class, mapper);
        SlaveStateDto slaveState = new SlaveStateDto(appState, List.of(), List.of(), null, null);

        TestUtils.testForbiddenPost(mvc, mapper, "/rest/state", slaveState);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateStateAdmin() {
        AppState appState = TestUtils.readJson("state.json", AppState.class, mapper);
        SlaveStateDto slaveState = new SlaveStateDto(appState, List.of(), List.of(), null, null);

        TestUtils.testForbiddenPost(mvc, mapper, "/rest/state", slaveState);
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void testUpdateStateGuest() {
        AppState appState = TestUtils.readJson("state.json", AppState.class, mapper);
        SlaveStateDto slaveState = new SlaveStateDto(appState, List.of(), List.of(), null, null);

        TestUtils.testForbiddenPost(mvc, mapper, "/rest/state", slaveState);
    }

    @Test
    void testUpdateStateAnon() {
        AppState appState = TestUtils.readJson("state.json", AppState.class, mapper);
        SlaveStateDto slaveState = new SlaveStateDto(appState, List.of(), List.of(), null, null);

        TestUtils.testUnauthorizedPost(mvc, mapper, "/rest/state", slaveState);
    }

}
