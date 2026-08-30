package com.swetlokognatsk.protected_resource;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.swetlokognatsk.protected_resource.models.AccessTokenValue;
import com.swetlokognatsk.protected_resource.services.AccessTokenVerifier;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static com.swetlokognatsk.protected_resource.services.AuthHeaderHelper.*;
import java.io.IOException;

@Component
public final class OAuthInterceptor implements HandlerInterceptor {

    private AccessTokenVerifier accessTokenVerifier;

    public OAuthInterceptor(final AccessTokenVerifier accessTokenVerifier) {
        this.accessTokenVerifier = accessTokenVerifier;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String authHeader = request.getHeader("Authorization");
        var bodyOrQueryAccessToken = request.getParameter("accessToken");

        String accessTokenValue;
        if (hasAuthBearerHeader(authHeader)) {
            accessTokenValue = cutAccessToken(authHeader);
        } else if (bodyOrQueryAccessToken != null && bodyOrQueryAccessToken.length() > 0) {
            accessTokenValue = bodyOrQueryAccessToken;
        } else {
            accessTokenValue = null;
        }

        boolean authIsSuccessful;
        if (accessTokenValue == null) {
            authIsSuccessful = false;
            response.setStatus(400);
            response.getWriter().write("accessToken is not found in request");
        } else {
            var accessToken = new AccessTokenValue(accessTokenValue);
            try {
                verifyAccessToken(accessToken);
                authIsSuccessful = true;
            } catch (AccessTokenNotFoundException e) {
                authIsSuccessful = false;
                response.setStatus(401);
                response.getWriter().write("accessToken is not found in database: %s".formatted(e.getMessage()));
            }
        }
        return authIsSuccessful;
    }

    private void verifyAccessToken(final AccessTokenValue accessTokenValue) throws AccessTokenNotFoundException {
        accessTokenVerifier.verifyAccessToken(accessTokenValue);
    }

}
