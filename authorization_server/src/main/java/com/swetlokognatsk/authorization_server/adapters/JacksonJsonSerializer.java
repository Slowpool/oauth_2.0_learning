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

    public String serializeAuthorizationRequest(final AuthorizationRequest authorizationRequest) {
        return objectMapper.writeValueAsString(authorizationRequest);
    }

    public AuthorizationRequest deserializeAuthorizationRequest(final String authorizationRequestJson) {
        return objectMapper.readValue(authorizationRequestJson, AuthorizationRequest.class);
    }

    public String serializeAuthorizationCode(AuthorizationCode authorizationCode) {
        return objectMapper.writeValueAsString(authorizationCode);
    }

    
}
