package com.github.lulewiczg.watering;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lulewiczg.watering.config.dto.ValveType;
import com.github.lulewiczg.watering.config.dto.WaterLevelSensorConfig;
import com.github.lulewiczg.watering.exception.ApiError;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
import com.github.lulewiczg.watering.service.ina219.enums.Address;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.Sensor;
import com.github.lulewiczg.watering.state.dto.Tank;
import com.github.lulewiczg.watering.state.dto.Valve;
import com.github.lulewiczg.watering.state.dto.WaterSource;
import com.pi4j.io.gpio.RaspiPin;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Utils for tests.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestUtils {

    public static final String UNAUTHORIZED = "Unauthorized";

    public static final String UNAUTHORIZED_MSG = "Full authentication is required to access this resource";

    public static final String FORBIDDEN = "Forbidden";

    public static final String FORBIDDEN_MSG = "Access is denied";

    public static final ActionResultDto<Void> EMPTY_RESULT = new ActionResultDto<>("id", null, null, LocalDateTime.now(), null);

    public static final ActionResultDto<Void> ERROR_RESULT = new ActionResultDto<>("id", null, null, LocalDateTime.now(), "error");

    public static final Valve VALVE = new Valve("valve", "valve", ValveType.INPUT, true, false, 1L, RaspiPin.GPIO_10);

    public static final Sensor SENSOR = new Sensor("sensor1", 12, 21, null, Address.ADDR_40, RaspiPin.GPIO_10, 10, 1000, 100, 12);

    public static final Tank TANK = new Tank("tank", 100, SENSOR, VALVE);

    public static final Valve VALVE2 = new Valve("valve2", "valve2", ValveType.INPUT, true, false, 1L, RaspiPin.GPIO_11);

    public static final Sensor SENSOR2 = new Sensor("sensor2", 5, 100, null, Address.ADDR_41, null, 20, 50, 60, 5);

    public static final Tank TANK2 = new Tank("tank2", 100, SENSOR2, VALVE2);

    public static final Valve TAP_VALVE = new Valve("tap", "tap", ValveType.INPUT, false, false, 1L, RaspiPin.GPIO_21);

    public static final WaterSource TAP = new WaterSource("water", TAP_VALVE);

    public static final Valve OUT = new Valve("out", "out", ValveType.OUTPUT, false, false, 1L, RaspiPin.GPIO_30);

    public static final Valve OUT2 = new Valve("out2", "out2", ValveType.OUTPUT, false, true, 1L, RaspiPin.GPIO_31);

    public static final Sensor OVERFLOW_SENSOR = new Sensor("overflowSensor", 10, 90, 100, Address.ADDR_40, null, 10, 12, 100, 200);

    public static final Tank OVERFLOW_TANK = new Tank("overflow", 100, OVERFLOW_SENSOR, VALVE);

    public static final WaterLevelSensorConfig SENSOR_CONFIG = new WaterLevelSensorConfig("test", 1, 10, Address.ADDR_41, null, 10, 100, 200, 12);

    public static final WaterLevelSensorConfig SENSOR_CONFIG2 = new WaterLevelSensorConfig("test", 1, 10, Address.ADDR_40, null, 10, 100, 200, 12);

    /**
     * Reads json from file and maps to object.
     *
     * @param fileName file name
     * @param type     type
     * @param mapper   mapper
     * @param <T>      type
     * @return mapped object
     */
    @SneakyThrows

    public static <T> T readJson(String fileName, Class<T> type, ObjectMapper mapper) {
        return mapper.readValue(readJson(fileName), type);
    }

    /**
     * Reads json from file.
     *
     * @param fileName file name
     * @return json
     */
    @SneakyThrows
    public static String readJson(String fileName) {
        return Files.readString(Paths.get("src/test/resources/testData/json/" + fileName));
    }

    /**
     * Tests REST error response.
     *
     * @param json     response
     * @param expected expected error
     * @param mapper   mapper
     * @throws JsonProcessingException
     */
    public static void testError(String json, ApiError expected, ObjectMapper mapper) throws JsonProcessingException {
        Date expectedDate = expected.getTimestamp();
        ApiError error = mapper.readValue(json, ApiError.class);
        assertNotNull(error.getTimestamp());
        assertTrue(expectedDate.getTime() <= error.getTimestamp().getTime(), "Time should be after expected!");
        error.setTimestamp(expectedDate);
        assertEquals(expected, error);
    }

    /**
     * Tests REST error response.
     *
     * @param error    actual error
     * @param expected expected error
     */
    public static void testError(ApiError error, ApiError expected) {
        Date expectedDate = expected.getTimestamp();
        assertNotNull(error.getTimestamp());
        assertTrue(expectedDate.getTime() <= error.getTimestamp().getTime(), "Time should be after expected!");
        error.setTimestamp(expectedDate);
        assertEquals(expected, error);
    }

    /**
     * Tests forbidden error for post.
     *
     * @param mvc     mock mvc
     * @param mapper  mapper
     * @param url     URL
     * @param payload payload
     */
    @SneakyThrows
    public static void testForbiddenPost(MockMvc mvc, ObjectMapper mapper, String url, Object payload) {
        ApiError expected = new ApiError(403, FORBIDDEN, FORBIDDEN_MSG);

        String json = mvc.perform(post(url)
                .content(mapper.writeValueAsString(payload))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn().getResponse().getContentAsString();

        testError(json, expected, mapper);
    }

    /**
     * Tests unauthorized error for post.
     *
     * @param mvc     mock mvc
     * @param mapper  mapper
     * @param url     URL
     * @param payload payload
     */
    @SneakyThrows
    public static void testUnauthorizedPost(MockMvc mvc, ObjectMapper mapper, String url, Object payload) {
        ApiError expected = new ApiError(401, UNAUTHORIZED, UNAUTHORIZED_MSG);

        String json = mvc.perform(post(url)
                .content(mapper.writeValueAsString(payload))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        testError(json, expected, mapper);
    }

    /**
     * Tests forbidden error for get.
     *
     * @param mvc    mock mvc
     * @param mapper mapper
     * @param url    URL
     */
    @SneakyThrows
    public static void testForbiddenGet(MockMvc mvc, ObjectMapper mapper, String url) {
        ApiError expected = new ApiError(403, FORBIDDEN, FORBIDDEN_MSG);

        String json = mvc.perform(get(url))
                .andExpect(status().isForbidden())
                .andReturn().getResponse().getContentAsString();

        testError(json, expected, mapper);
    }

    /**
     * Tests unauthorized error for get.
     *
     * @param mvc    mock mvc
     * @param mapper mapper
     * @param url    URL
     */
    @SneakyThrows
    public static void testUnauthorizedGet(MockMvc mvc, ObjectMapper mapper, String url) {
        ApiError expected = new ApiError(401, UNAUTHORIZED, UNAUTHORIZED_MSG);

        String json = mvc.perform(get(url))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        testError(json, expected, mapper);
    }


    /**
     * Tests not found error for get.
     *
     * @param mvc    mock mvc
     * @param mapper mapper
     * @param url    URL
     */
    @SneakyThrows
    public static void testNotFoundGet(MockMvc mvc, ObjectMapper mapper, String url) {
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests not found error for post.
     *
     * @param mvc    mock mvc
     * @param mapper mapper
     * @param url    URL
     */
    @SneakyThrows
    public static void tesNotFoundPost(MockMvc mvc, ObjectMapper mapper, String url, Object payload) {
        mvc.perform(post(url)
                .content(mapper.writeValueAsString(payload))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests successful result of action.
     *
     * @param result result.
     */
    public static void testActionResult(ActionResultDto<?> result) {
        assertNotNull(result.getId());
        assertNull(result.getErrorMsg());
        LocalDateTime date = LocalDateTime.now().minusMinutes(1);
        assertTrue(date.isBefore(result.getExecDate()));
    }

    /**
     * Tests failed result of action.
     *
     * @param result result.
     * @param error  expected error
     */
    public static void testActionResult(ActionResultDto<?> result, String error) {
        assertNotNull(result.getId());
        assertNull(result.getResult());
        assertEquals(error, result.getErrorMsg());
        LocalDateTime date = LocalDateTime.now().minusMinutes(1);
        assertTrue(date.isBefore(result.getExecDate()));
    }

    /**
     * Splits ID into segments.
     *
     * @param id ID
     * @return segmented ID
     */
    public static List<String> splitId(String id) {
        return Arrays.asList(id.split("\\."));
    }

    /**
     * Sets up standard system config.
     *
     * @param state app state
     */
    public static void standardMock(AppState state) {
        when(state.getTanks()).thenReturn(List.of(TANK, TANK2));
        when(state.getTaps()).thenReturn(List.of(TAP));
        when(state.getOutputs()).thenReturn(List.of(OUT, OUT2));
    }
}
