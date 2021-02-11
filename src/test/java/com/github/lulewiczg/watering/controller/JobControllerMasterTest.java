package com.github.lulewiczg.watering.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.config.MasterConfig;
import com.github.lulewiczg.watering.exception.ApiError;
import com.github.lulewiczg.watering.exception.InvalidParamException;
import com.github.lulewiczg.watering.security.AuthEntryPoint;
import com.github.lulewiczg.watering.security.AuthProvider;
import com.github.lulewiczg.watering.service.ActionService;
import com.github.lulewiczg.watering.service.dto.JobDefinitionDto;
import com.github.lulewiczg.watering.state.MasterState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles({"test", "testMaster"})
@WebMvcTest(JobController.class)
@ExtendWith(SpringExtension.class)
@Import({AuthEntryPoint.class, AuthProvider.class, MasterConfig.class, JobMasterController.class})
class JobControllerMasterTest {

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
    void testGetJobs() throws Exception {
        testGet();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetJobsAdmin() throws Exception {
        testGet();
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void testGetJobsGuest() {
        TestUtils.testForbiddenGet(mvc, mapper, "/rest/jobs");
    }

    @Test
    @WithMockUser(roles = "SLAVE")
    void testGetJobsSlave() {
        TestUtils.testForbiddenGet(mvc, mapper, "/rest/jobs");
    }

    @Test
    void testGetJobsAnon() {
        TestUtils.testUnauthorizedGet(mvc, mapper, "/rest/jobs");
    }

    @Test
    @WithMockUser(roles = "USER")
    void testRunJob() throws Exception {
        testRun();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testRunJobAdmin() throws Exception {
        testRun();
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void testRunJobsGuest() {
        TestUtils.testForbiddenPost(mvc, mapper, "/rest/jobs/test123", null);
    }

    @Test
    @WithMockUser(roles = "SLAVE")
    void testRunJobsSlave() {
        TestUtils.testForbiddenPost(mvc, mapper, "/rest/jobs/test123", null);
    }

    @Test
    void testRunJobsAnon() {
        TestUtils.testUnauthorizedPost(mvc, mapper, "/rest/jobs/test123", null);

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
    void testGetPendingGuest() throws Exception {
        ApiError expected = new ApiError(403, TestUtils.FORBIDDEN, TestUtils.FORBIDDEN_MSG);

        String json = mvc.perform(get("/rest/jobs/pending"))
                .andExpect(status().isForbidden())
                .andReturn().getResponse().getContentAsString();

        TestUtils.testError(json, expected, mapper);
    }

    @Test
    @WithMockUser(roles = "SLAVE")
    void testGetPendingSlave() throws Exception {
        ApiError expected = new ApiError(403, TestUtils.FORBIDDEN, TestUtils.FORBIDDEN_MSG);

        String json = mvc.perform(get("/rest/jobs/pending"))
                .andExpect(status().isForbidden())
                .andReturn().getResponse().getContentAsString();

        TestUtils.testError(json, expected, mapper);
    }

    @Test
    void testGetPendingAnon() throws Exception {
        ApiError expected = new ApiError(401, TestUtils.UNAUTHORIZED, TestUtils.UNAUTHORIZED_MSG);

        String json = mvc.perform(get("/rest/jobs/pending"))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        TestUtils.testError(json, expected, mapper);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testRunJobError() throws Exception {
        InvalidParamException ex = new InvalidParamException(Object.class, String.class);
        doThrow(ex).when(service).runJob("test123");
        ApiError expected = new ApiError(400, "Bad Request", ex.getMessage());
        Date date = new Date();

        String json = mvc.perform(post("/rest/jobs/test123"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        ApiError error = mapper.readValue(json, ApiError.class);
        assertNotNull(error.getTimestamp());
        assertTrue(date.before(error.getTimestamp()));
        error.setTimestamp(expected.getTimestamp());
        assertEquals(expected, error);
    }

    private void testGet() throws Exception {
        JobDefinitionDto[] jobDefinitionDto = TestUtils.readJson("jobs.json", JobDefinitionDto[].class, mapper);
        when(service.getJobs()).thenReturn(Arrays.asList(jobDefinitionDto));

        mvc.perform(get("/rest/jobs"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(jobDefinitionDto)));
    }

    private void testRun() throws Exception {
        mvc.perform(post("/rest/jobs/test123"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(service).runJob("test123");
    }

    private void testPending() throws Exception {
        List<String> list = List.of("test", "test");
        when(masterState.getJobs()).thenReturn(list);
        mvc.perform(get("/rest/jobs/pending"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(list)));
    }

}