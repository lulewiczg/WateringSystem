package com.github.lulewiczg.watering.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.exception.ActionNotFoundException;
import com.github.lulewiczg.watering.exception.ApiError;
import com.github.lulewiczg.watering.security.AuthEntryPoint;
import com.github.lulewiczg.watering.security.AuthProvider;
import com.github.lulewiczg.watering.service.ActionService;
import com.github.lulewiczg.watering.service.dto.JobDefinitionDto;
import com.github.lulewiczg.watering.service.dto.JobDto;
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
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@WebMvcTest(JobController.class)
@ExtendWith(SpringExtension.class)
@Import({AuthEntryPoint.class, AuthProvider.class})
class JobControllerTest {

    @MockBean
    private ActionService service;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private final JobDto job = new JobDto("test");

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
    void testGetJobsAnon() throws Exception {
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
        TestUtils.testForbiddenPost(mvc, mapper, "/rest/jobs", job);
    }

    @Test
    @WithMockUser(roles = "SLAVE")
    void testRunJobsSlave() {
        TestUtils.testForbiddenPost(mvc, mapper, "/rest/jobs", job);
    }

    @Test
    void testRunJobsAnon() {
        TestUtils.testUnauthorizedPost(mvc, mapper, "/rest/jobs", job);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testRunJobError() throws Exception {
        ActionNotFoundException ex = new ActionNotFoundException("test");
        when(service.runJob(job)).thenThrow(ex);
        ApiError expected = new ApiError(400, "Bad Request", ex.getMessage());
        Date date = new Date();

        String json = mvc.perform(post("/rest/jobs")
                .content(mapper.writeValueAsString(job))
                .contentType(MediaType.APPLICATION_JSON))
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
        mvc.perform(post("/rest/jobs")
                .content(mapper.writeValueAsString(job))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(service).runJob(job);
    }

}
