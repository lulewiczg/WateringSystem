package com.github.lulewiczg.watering.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.security.AuthEntryPoint;
import com.github.lulewiczg.watering.security.AuthProvider;
import com.github.lulewiczg.watering.service.dto.SlaveStateDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@ActiveProfiles({"test", "testSlave"})
@ExtendWith(SpringExtension.class)
@WebMvcTest(StateMasterController.class)
@Import({AuthEntryPoint.class, AuthProvider.class})
class StateMasterControllerSlaveTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @WithMockUser(roles = "USER")
    void testUpdateState() {
        TestUtils.tesNotFoundPost(mvc, mapper, "/rest/state", new SlaveStateDto());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateStateAdmin() {
        TestUtils.tesNotFoundPost(mvc, mapper, "/rest/state", new SlaveStateDto());

    }

    @Test
    @WithMockUser(roles = "GUEST")
    void testUpdateStateGuest() {
        TestUtils.tesNotFoundPost(mvc, mapper, "/rest/state", new SlaveStateDto());

    }

    @Test
    void testUpdateStateAnon() {
        TestUtils.testUnauthorizedPost(mvc, mapper, "/rest/state", new SlaveStateDto());
    }


}