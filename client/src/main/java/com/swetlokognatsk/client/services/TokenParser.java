package com.swetlokognatsk.client.services;

import com.swetlokognatsk.client.model.AccessToken;
import com.swetlokognatsk.client.model.RefreshAndAccessTokensPair;
import com.swetlokognatsk.client.model.Token;
import com.swetlokognatsk.client.model.TokenStrategy;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

public final class TokenParser {
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = JsonMapper.builder()
            .build();
    }

    public static Token parse(final String jsonContent, final TokenStrategy tokenStrategy) {
        var clazz = switch (tokenStrategy) {
            case SINGLE_ACCESS_TOKEN -> AccessToken.class;
            // TODO custom json deserializer reader
            case REFRESH_AND_ACCESS_PAIR -> RefreshAndAccessTokensPair.class;
            default -> throw new RuntimeException("unknown token strategy");
        };
        return objectMapper.readValue(jsonContent, clazz);
    }
}
