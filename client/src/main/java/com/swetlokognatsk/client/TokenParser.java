package com.swetlokognatsk.client;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

public final class TokenParser {
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = JsonMapper.builder()
            .build();
    }

    public static Token parse(final String jsonContent) {
        return objectMapper.readValue(jsonContent, Token.class);
    }
}
