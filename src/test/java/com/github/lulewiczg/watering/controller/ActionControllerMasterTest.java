package com.github.lulewiczg.watering.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.exception.ApiError;
import com.github.lulewiczg.watering.exception.InvalidParamException;
import com.github.lulewiczg.watering.security.AuthEntryPoint;
import com.github.lulewiczg.watering.security.AuthProvider;
import com.github.lulewiczg.watering.service.ActionService;
import com.github.lulewiczg.watering.service.dto.ActionDefinitionDto;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.state.MasterState;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles({"test", "testMaster"})
@ExtendWith(SpringExtension.class)
@Import({AuthEntryPoint.class, AuthProvider.class, MasterConfig.class, ActionMasterController.class})
@WebMvcTest(ActionController.class)
class ActionControllerMasterTest {

    @MockBean
    private ActionService service;

    @MockBean
    private MasterState masterState;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @WithMockUser(roles = "USER")
    void testGetActions() throws Exception {
        testGetAction();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetActionsAdmin() throws Exception {
        testGetAction();
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void testGetActionsGuest() {
        TestUtils.testForbiddenGet(mvc, mapper, "/rest/actions");
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

    @Test
    @WithMockUser(roles = "USER")
    void testRunAction() throws Exception {
        testRun();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testRunActionAdmin() throws Exception {
        testRun();
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void testRunActionGuest() {
        ActionDto actionDto = new ActionDto("test", "test2", "test3");

        TestUtils.testForbiddenPost(mvc, mapper, "/rest/actions", actionDto);
    }

    @Test
    @WithMockUser(roles = "SLAVE")
    void testRunActionSlave() {
        ActionDto actionDto = new ActionDto("test", "test2", "test3");

        TestUtils.testForbiddenPost(mvc, mapper, "/rest/actions", actionDto);
    }

    @Test
    void testRunActionGuestAnon() {
        ActionDto actionDto = new ActionDto("test", "test2", "test3");

        TestUtils.testUnauthorizedPost(mvc, mapper, "/rest/actions", actionDto);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetPending() throws Exception {
        testPending();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPendingAdmin() throws Exception {
        testPending();
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void testGetPendingGuest() {
        TestUtils.testForbiddenGet(mvc, mapper, "/rest/actions/pending");
    }

    @Test
    @WithMockUser(roles = "SLAVE")
    void testGetPendingSlave() {
        TestUtils.testForbiddenGet(mvc, mapper, "/rest/actions/pending");
    }

    @Test
    void testGetPendingAnon() {
        TestUtils.testUnauthorizedGet(mvc, mapper, "/rest/actions/pending");

    }

    @Test
    @WithMockUser(roles = "USER")
    void testRunActionError() throws Exception {
        ActionDto actionDto = new ActionDto("test", "test2", "test3");
        InvalidParamException ex = new InvalidParamException(Object.class, String.class);
        when(service.runAction(actionDto)).thenThrow(ex);
        ApiError expected = new ApiError(400, "Bad Request", ex.getMessage());

        String json = mvc.perform(post("/rest/actions")
                .content(mapper.writeValueAsString(actionDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        TestUtils.testError(json, expected, mapper);
    }

    private void testGetAction() throws Exception {
        ActionDefinitionDto[] jobDefinitionDto = TestUtils.readJson("actions.json", ActionDefinitionDto[].class, mapper);
        when(service.getActions()).thenReturn(Arrays.asList(jobDefinitionDto));

        mvc.perform(get("/rest/actions"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(jobDefinitionDto)));
    }

    private void testRun() throws Exception {
        ActionDto actionDto = new ActionDto("test", "test2", "test3");
        when(service.runAction(actionDto)).thenReturn("testResult");
        mvc.perform(post("/rest/actions")
                .content(mapper.writeValueAsString(actionDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    private void testPending() throws Exception {
        ActionDto actionDto = new ActionDto("test", "test2", "test3");
        ActionDto actionDto2 = new ActionDto("test4", "test5", "test6");

        when(masterState.getActions()).thenReturn(List.of(actionDto, actionDto2));
        mvc.perform(get("/rest/actions/pending"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(actionDto, actionDto2))));
    }
}
