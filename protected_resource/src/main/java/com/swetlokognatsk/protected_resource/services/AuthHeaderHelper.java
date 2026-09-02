package com.swetlokognatsk.protected_resource.services;

import com.swetlokognatsk.protected_resource.models.AccessTokenBody;

import jakarta.servlet.http.HttpServletRequest;

public final class AuthHeaderHelper {
    private static final String BEARER_TOKEN_START = "Bearer ";

    public static boolean hasAuthBearerHeader(final String auth) {
        return auth != null && auth.strip().length() > 0 && auth.startsWith(BEARER_TOKEN_START);
    }

    public static String cutAccessToken(final String auth) {
        return auth.substring(BEARER_TOKEN_START.length());
    }

    public static boolean hasFormUrlencodedToken(final String accessTokenValue) {
        if (accessTokenValue == null) {
            return false;
        }

        if (accessTokenValue.length() == 0) {
            return false;
        }

        return true;
    }

    public static boolean hasQueryParamToken(final String accessTokenParam) {
        return accessTokenParam != null && accessTokenParam.length() > 0;
    }

    public static boolean hasQueryParamToken(final HttpServletRequest request) {
        var accessToken = request.getParameter("accessToken");
        return accessToken != null && accessToken.length() > 0;
    }
}
