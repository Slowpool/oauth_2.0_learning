package com.swetlokognatsk.authorization_server.adapters;

import org.springframework.stereotype.Component;
import com.swetlokognatsk.authorization_server.models.AuthorizationCode;
import com.swetlokognatsk.authorization_server.models.AuthorizationRequest;
import com.swetlokognatsk.authorization_server.ports.JsonSerializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Component
public class JacksonJsonSerializer implements JsonSerializer {

    private final ObjectMapper objectMapper;

    public JacksonJsonSerializer() {
        objectMapper = JsonMapper.builder().build();
    }

    private <T> T readValue(final String json, final Class<T> clazz) {
        return objectMapper.readValue(json, clazz);
    }

    private String writeValueAsString(final Object value) {
        return objectMapper.writeValueAsString(value);
    }

    public String serializeAuthorizationRequest(final AuthorizationRequest authorizationRequest) {
        return writeValueAsString(authorizationRequest);
    }

    public AuthorizationRequest deserializeAuthorizationRequest(final String authorizationRequestJson) {
        return readValue(authorizationRequestJson, AuthorizationRequest.class);
    }

    public String serializeAuthorizationCode(AuthorizationCode authorizationCode) {
        return writeValueAsString(authorizationCode);
    }

    public AuthorizationCode deserializeAuthorizationCode(final String authorizationCodeJson) {
        return readValue(authorizationCodeJson, AuthorizationCode.class);
    }
}
