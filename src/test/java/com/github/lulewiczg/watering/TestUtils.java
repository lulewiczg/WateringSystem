package com.github.lulewiczg.watering;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lulewiczg.watering.config.dto.*;
import com.github.lulewiczg.watering.exception.ApiError;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
import com.github.lulewiczg.watering.service.ina219.enums.Address;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.dto.*;
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

    /**
     * Object constants.
     */
    public static class Objects {
        public static final Valve VALVE = new Valve("valve", "valve", ValveType.INPUT, true, false, 1, RaspiPin.GPIO_10);
        public static final Valve VALVE2 = new Valve("valve2", "valve2", ValveType.INPUT, true, false, 1, RaspiPin.GPIO_11);
        public static final Valve TAP_VALVE = new Valve("tap", "tap", ValveType.INPUT, false, false, 1, RaspiPin.GPIO_21);
        public static final Valve OUT = new Valve("out", "out", ValveType.OUTPUT, false, false, 1, RaspiPin.GPIO_30);
        public static final Valve OUT2 = new Valve("out2", "out2", ValveType.OUTPUT, false, true, 2, RaspiPin.GPIO_31);
        public static Sensor SENSOR;
        public static Sensor SENSOR2;
        public static Tank TANK;
        public static Tank TANK2;
        public static final Pump PUMP = new Pump("pump1", "pump", false, RaspiPin.GPIO_25);
        public static final WaterSource TAP = new WaterSource("water", TAP_VALVE);
        public static final Sensor OVERFLOW_SENSOR = new Sensor("overflowSensor", 100, 10, 90, Address.ADDR_40, null, 10, 12, 200);
        public static final Tank OVERFLOW_TANK = new Tank("overflow", 100, OVERFLOW_SENSOR, VALVE, PUMP);

        public static void reset() {
            SENSOR = new Sensor("sensor1", null, 12, 21, Address.ADDR_40, RaspiPin.GPIO_10, 10, 100, 12);
            SENSOR2 = new Sensor("sensor2", null, 5, 100, Address.ADDR_41, null, 20, 50, 5);
            TANK = new Tank("tank", 100, SENSOR, VALVE, PUMP);
            TANK2 = new Tank("tank2", 100, SENSOR2, VALVE2, null);
        }

        static {
            reset();
        }
    }

    /**
     * Config constants.
     */
    public static class Config {
        public static final ValveConfig VALVE = new ValveConfig("valve1", "Tank 1", ValveType.INPUT, "GPIO 3", false, false, null);
        public static final ValveConfig VALVE2 = new ValveConfig("valve2", "Tank 2", ValveType.INPUT, "GPIO 4", false, false, null);
        public static final ValveConfig VALVE3 = new ValveConfig("tap", "tap water", ValveType.INPUT, "GPIO 5", false, false, null);
        public static final ValveConfig OUT = new ValveConfig("out", "out", ValveType.OUTPUT, "GPIO 6", true, true, 333L);
        public static final WaterLevelSensorConfig SENSOR = new WaterLevelSensorConfig("sensor1", 12, 21, Address.ADDR_40, "GPIO 10", 10, 100, 12);
        public static final WaterLevelSensorConfig SENSOR2 = new WaterLevelSensorConfig("sensor2", 5, 100, Address.ADDR_41, "", 20, 50, 5);
        public static final TankConfig TANK = new TankConfig("tank1", 123, "sensor1", "valve1", "pump1", TankType.DEFAULT);
        public static final TankConfig TANK2 = new TankConfig("tank2", 321, "sensor2", "valve2", null, TankType.DEFAULT);
        public static final TankConfig TAP = new TankConfig("tap", null, null, "tap", null, TankType.UNLIMITED);
        public static final TankConfig TANK_NO_SENSOR = new TankConfig("tank1", 123, null, "valve1", null, TankType.DEFAULT);
        public static final PumpConfig PUMP = new PumpConfig("pump1", "Pump 1", "GPIO 15");
    }

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
     */
    @SneakyThrows
    public static void testError(String json, ApiError expected, ObjectMapper mapper) {
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
     * @param mvc mock mvc
     * @param url URL
     */
    @SneakyThrows
    public static void testNotFoundGet(MockMvc mvc, String url) {
        mvc.perform(get(url)).andExpect(status().isNotFound());
    }

    /**
     * Tests not found error for post.
     *
     * @param mvc     mock mvc
     * @param mapper  mapper
     * @param url     URL
     * @param payload payload
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
     * Tests failed result of action.
     *
     * @param result result.
     * @param error  expected error
     */
    public static void testScheduledActionResult(ActionResultDto<?> result, String error) {
        assertNotNull(result.getId());
        assertNull(result.getResult());
        assertEquals(error, result.getErrorMsg());
        assertNull(result.getExecDate());
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
        when(state.getTanks()).thenReturn(List.of(Objects.TANK, Objects.TANK2));
        when(state.getTaps()).thenReturn(List.of(Objects.TAP));
        when(state.getOutputs()).thenReturn(List.of(Objects.OUT, Objects.OUT2));
        when(state.getPumps()).thenReturn(List.of(Objects.PUMP));
    }

}
