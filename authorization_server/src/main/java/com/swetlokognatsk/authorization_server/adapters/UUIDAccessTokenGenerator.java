package com.swetlokognatsk.authorization_server.adapters;

import java.util.UUID;
import org.springframework.stereotype.Component;
import com.swetlokognatsk.authorization_server.ports.AccessTokenGenerator;
import com.swetlokognatsk.oauth_db.models.AccessTokenValue;

@Component
public class UUIDAccessTokenGenerator implements AccessTokenGenerator {

    public AccessTokenValue generate() {
        var value = UUID.randomUUID()
            .toString();
        return new AccessTokenValue(value);
    }
}
