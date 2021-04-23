package com.github.lulewiczg.watering.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.security.AuthEntryPoint;
import com.github.lulewiczg.watering.security.AuthProvider;
import com.github.lulewiczg.watering.state.AppState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import({AuthEntryPoint.class, AuthProvider.class})
class StateControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AppState appState;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @WithMockUser(roles = "GUEST")
    void testGetStateGuest() throws Exception {
        testState();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetState() throws Exception {
        testState();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStateAdmin() throws Exception {
        testState();
    }

    @Test
    @WithMockUser(roles = "SLAVE")
    void testGetActionsSlave() {
        TestUtils.testForbiddenGet(mvc, mapper, "/rest/actions");
    }

    @Test
    void testGetActionsAnon() {
        TestUtils.testUnauthorizedGet(mvc, mapper, "/rest/actions");
    }

    private void testState() throws Exception {
        AppState state = TestUtils.readJson("state.json", AppState.class, mapper);
        state.setBuild(appState.getBuild());
        mvc.perform(get("/rest/state"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(state)));
    }

}
