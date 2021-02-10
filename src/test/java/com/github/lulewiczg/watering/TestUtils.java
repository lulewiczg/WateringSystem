package com.github.lulewiczg.watering;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lulewiczg.watering.exception.ApiError;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
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
}
