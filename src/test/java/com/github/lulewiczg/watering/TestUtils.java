package com.github.lulewiczg.watering;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utils for tests.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestUtils {

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

}
