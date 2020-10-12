package com.github.lulewiczg.watering.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.exception.ApiError;
import com.github.lulewiczg.watering.exception.InvalidParamException;
import com.github.lulewiczg.watering.security.AuthEntryPoint;
import com.github.lulewiczg.watering.service.ActionService;
import com.github.lulewiczg.watering.service.dto.JobDefinitionDto;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(AuthEntryPoint.class)
@WebMvcTest(JobController.class)
@ExtendWith(SpringExtension.class)
class JobControllerTest {

    @MockBean
    private ActionService service;

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
    void testGetJobsGuest() throws Exception {
        ApiError expected = new ApiError(403, TestUtils.FORBIDDEN, TestUtils.FORBIDDEN_MSG);

        String json = mvc.perform(get("/rest/jobs"))
                .andExpect(status().isForbidden())
                .andReturn().getResponse().getContentAsString();

        TestUtils.testError(json, expected, mapper);
    }

    @Test
    void testGetJobsAnon() throws Exception {
        ApiError expected = new ApiError(401, TestUtils.UNAUTHORIZED, TestUtils.UNAUTHORIZED_MSG);

        String json = mvc.perform(get("/rest/jobs"))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        TestUtils.testError(json, expected, mapper);
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
    void testRunJobsGuest() throws Exception {
        ApiError expected = new ApiError(403, TestUtils.FORBIDDEN, TestUtils.FORBIDDEN_MSG);

        String json = mvc.perform(post("/rest/jobs/test123"))
                .andExpect(status().isForbidden())
                .andReturn().getResponse().getContentAsString();

        TestUtils.testError(json, expected, mapper);
    }

    @Test
    void testRunJobsAnon() throws Exception {
        ApiError expected = new ApiError(401, TestUtils.UNAUTHORIZED, TestUtils.UNAUTHORIZED_MSG);

        String json = mvc.perform(post("/rest/jobs/test123"))
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

}