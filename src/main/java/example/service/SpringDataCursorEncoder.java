package example.service;

import java.util.Map;

/**
 * Cursor encoder for Spring Data.
 */
public interface SpringDataCursorEncoder {
    /**
     * Encode the cursor.
     * 
     * @param keys the keys
     * @return the encoded cursor
     */
    String encode(Map<String, ?> keys);

    /**
     * Decode the cursor.
     * 
     * @param cursor the encoded cursor
     * @return the keys
     */
    Map<String, ?> decode(String cursor);
}
