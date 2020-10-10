package com.github.lulewiczg.watering.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
class StateControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void testGetState() throws Exception {
        String json = Files.readString(Paths.get("src/test/resources/testData/json/state.json"));
        mvc.perform(get("/rest/state"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

}