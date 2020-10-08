package com.github.lulewiczg.watering.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lulewiczg.watering.service.ActionService;
import com.github.lulewiczg.watering.service.dto.ActionDto;
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

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testGetActions() throws Exception {
        when(service.getActions()).thenReturn(List.of("1", "2", "5"));

        mvc.perform(get("/actions/actions"))
                .andExpect(status().isOk())
                .andExpect(content().string("[\"1\",\"2\",\"5\"]"));
    }

    @Test
    void testGetJobs() throws Exception {
        when(service.getJobs()).thenReturn(List.of("1", "2", "5"));

        mvc.perform(get("/actions/jobs"))
                .andExpect(status().isOk())
                .andExpect(content().string("[\"1\",\"2\",\"5\"]"));
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
}