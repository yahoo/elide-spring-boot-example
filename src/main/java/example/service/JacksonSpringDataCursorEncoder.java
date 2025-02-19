package example.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * {@link SpringDataCursorEncoder} using Jackson.
 */
public class JacksonSpringDataCursorEncoder implements SpringDataCursorEncoder {
    private static class Holder {
        private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    }

    private final ObjectMapper objectMapper;

    public JacksonSpringDataCursorEncoder() {
        this(Holder.OBJECT_MAPPER);
    }

    public JacksonSpringDataCursorEncoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String encode(Map<String, ?> keys) {
        try {
            byte[] result = this.objectMapper.writeValueAsBytes(keys);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(result);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Map<String, ?> decode(String cursor) {
        if (cursor == null) {
            return Collections.emptyMap();
        }
        try {
            byte[] result = Base64.getUrlDecoder().decode(cursor);
            TypeReference<LinkedHashMap<String, Object>> typeRef = new TypeReference<LinkedHashMap<String, Object>>() {
            };
            return this.objectMapper.readValue(result, typeRef);
        } catch (IOException | IllegalArgumentException e) {
            return Collections.emptyMap();
        }
    }

}
