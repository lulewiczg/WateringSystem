package com.github.lulewiczg.watering.config;

import com.github.lulewiczg.watering.config.dto.Valve;
import com.github.lulewiczg.watering.config.dto.ValveType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
class ValveConfigTest {

    @Autowired
    private ValveConfig config;

    @Test
    void testPropsLoad() {
        Map<String, Valve> expected = new HashMap<>();
        expected.put("tank1", new Valve("Tank 1", ValveType.TANK, false));
        expected.put("garden", new Valve("out", ValveType.OUTPUT, true));
        expected.put("tap", new Valve("tap water", ValveType.TAP, false));

        assertEquals(expected, config.getItems());
    }

}