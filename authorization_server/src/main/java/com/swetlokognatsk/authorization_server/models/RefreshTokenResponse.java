package com.swetlokognatsk.authorization_server.models;

public record RefreshTokenResponse(String refreshToken, int expiresIn) {

}
