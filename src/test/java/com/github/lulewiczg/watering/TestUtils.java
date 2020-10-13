package com.github.lulewiczg.watering;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lulewiczg.watering.exception.ApiError;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Utils for tests.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestUtils {

    public static final String UNAUTHORIZED = "Unauthorized";

    public static final String UNAUTHORIZED_MSG = "Full authentication is required to access this resource";

    public static final String FORBIDDEN = "Forbidden";

    public static final String FORBIDDEN_MSG = "Access is denied";

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
     * @param error   actual error
     * @param expected expected error
     */
    public static void testError(ApiError error, ApiError expected) {
        Date expectedDate = expected.getTimestamp();
        assertNotNull(error.getTimestamp());
        assertTrue(expectedDate.getTime() <= error.getTimestamp().getTime(), "Time should be after expected!");
        error.setTimestamp(expectedDate);
        assertEquals(expected, error);
    }

}
