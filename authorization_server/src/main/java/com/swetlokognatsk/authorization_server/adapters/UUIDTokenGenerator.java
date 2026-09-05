package com.swetlokognatsk.authorization_server.adapters;

import java.util.UUID;
import org.springframework.stereotype.Component;
import com.swetlokognatsk.authorization_server.ports.AccessTokenGenerator;
import com.swetlokognatsk.authorization_server.ports.RefreshTokenGenerator;
import com.swetlokognatsk.oauth_db.models.AccessTokenValue;
import com.swetlokognatsk.oauth_db.models.RefreshTokenValue;

@Component
public class UUIDTokenGenerator implements AccessTokenGenerator, RefreshTokenGenerator {

    public AccessTokenValue generateAccessToken() {
        var value = generateToken();
        return new AccessTokenValue(value);
    }

    public RefreshTokenValue generateRefreshToken() {
        var value = generateToken();
        return new RefreshTokenValue(value);
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
