package com.github.lulewiczg.watering.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.exception.ApiError;
import com.github.lulewiczg.watering.exception.InvalidParamException;
import com.github.lulewiczg.watering.service.ActionService;
import com.github.lulewiczg.watering.service.dto.ActionDefinitionDto;
import com.github.lulewiczg.watering.service.dto.ActionDto;
import com.github.lulewiczg.watering.service.dto.JobDefinitionDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
@WebMvcTest(ActionController.class)
@ExtendWith(SpringExtension.class)
class ActionControllerTest {

    @MockBean
    private ActionService service;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void testGetActions() throws Exception {
        ActionDefinitionDto[] jobDefinitionDto = TestUtils.readJson("actions.json", ActionDefinitionDto[].class, mapper);
        when(service.getActions()).thenReturn(Arrays.asList(jobDefinitionDto));

        mvc.perform(get("/actions/actions"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(jobDefinitionDto)));
    }

    @Test
    void testGetJobs() throws Exception {
        JobDefinitionDto[] jobDefinitionDto = TestUtils.readJson("jobs.json", JobDefinitionDto[].class, mapper);
        when(service.getJobs()).thenReturn(Arrays.asList(jobDefinitionDto));

        mvc.perform(get("/actions/jobs"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(jobDefinitionDto)));
    }

    @Test
    void testRunAction() throws Exception {
        ActionDto actionDto = new ActionDto("test", "test2", "test3");
        when(service.runAction(actionDto)).thenReturn("testResult");

        mvc.perform(post("/actions/actions")
                .content(mapper.writeValueAsString(actionDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("testResult"));
    }

    @Test
    void testRunJob() throws Exception {
        mvc.perform(post("/actions/jobs/test123"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(service).runJob("test123");
    }

    @Test
    void testRunActionError() throws Exception {
        ActionDto actionDto = new ActionDto("test", "test2", "test3");
        InvalidParamException ex = new InvalidParamException(Object.class, String.class);
        when(service.runAction(actionDto)).thenThrow(ex);
        ApiError expected = new ApiError(400, "Bad Request", ex.getMessage());
        Date date = new Date();

        String json = mvc.perform(post("/actions/actions")
                .content(mapper.writeValueAsString(actionDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        ApiError error = mapper.readValue(json, ApiError.class);
        assertNotNull(error.getTimestamp());
        assertTrue(date.before(error.getTimestamp()));
        error.setTimestamp(expected.getTimestamp());
        assertEquals(expected, error);
    }

    @Test
    void testRunJobError() throws Exception {
        InvalidParamException ex = new InvalidParamException(Object.class, String.class);
        doThrow(ex).when(service).runJob("test123");
        ApiError expected = new ApiError(400, "Bad Request", ex.getMessage());
        Date date = new Date();

        String json = mvc.perform(post("/actions/jobs/test123"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        ApiError error = mapper.readValue(json, ApiError.class);
        assertNotNull(error.getTimestamp());
        assertTrue(date.before(error.getTimestamp()));
        error.setTimestamp(expected.getTimestamp());
        assertEquals(expected, error);
    }
}