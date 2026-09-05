package com.swetlokognatsk.authorization_server.models;

public record AccessTokenBody(String accessToken, String type, int expiresIn) {

}
