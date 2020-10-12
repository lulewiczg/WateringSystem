package com.github.lulewiczg.watering.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.exception.ApiError;
import com.github.lulewiczg.watering.security.AuthEntryPoint;
import com.github.lulewiczg.watering.security.AuthProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Paths;

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
    void testGetActionsAnon() throws Exception {
        ApiError expected = new ApiError(401, TestUtils.UNAUTHORIZED, TestUtils.UNAUTHORIZED_MSG);

        String json = mvc.perform(get("/rest/actions"))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        TestUtils.testError(json, expected, mapper);
    }


    private void testState() throws Exception {
        String json = Files.readString(Paths.get("src/test/resources/testData/json/state.json"));
        mvc.perform(get("/rest/state"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

}