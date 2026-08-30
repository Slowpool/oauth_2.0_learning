package com.swetlokognatsk.protected_resource;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import com.swetlokognatsk.protected_resource.models.AccessTokenValue;
import com.swetlokognatsk.protected_resource.models.Scopes;
import com.swetlokognatsk.protected_resource.models.ScopesSet;
import com.swetlokognatsk.protected_resource.services.AccessTokenVerifier;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static com.swetlokognatsk.protected_resource.services.AuthHeaderHelper.*;
import java.io.IOException;
import java.util.Set;

@Component
public final class OAuthInterceptor implements HandlerInterceptor {

    private AccessTokenVerifier accessTokenVerifier;

    public OAuthInterceptor(final AccessTokenVerifier accessTokenVerifier) {
        this.accessTokenVerifier = accessTokenVerifier;
    }

    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws IOException {
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
                var requiredScopes = getRequiredScopes(request);
                verifyAccessTokenAndScopes(accessToken, requiredScopes);
                authIsSuccessful = true;
            } catch (AccessTokenNotFoundException e) {
                authIsSuccessful = false;
                response.setStatus(401);
                response.getWriter().write("accessToken is not found in database: %s".formatted(e.getMessage()));
            } catch (RequiredScopesAreNotGrantedException e) {
                authIsSuccessful = false;
                response.setStatus(403);
                response.getWriter().write("this accessToken is not granted with required permissions: %s".formatted(e.getMessage()));
            }
        }
        return authIsSuccessful;
    }

    private void verifyAccessTokenAndScopes(final AccessTokenValue accessTokenValue, final ScopesSet requiredScopes) throws AccessTokenNotFoundException, RequiredScopesAreNotGrantedException {
        accessTokenVerifier.verify(accessTokenValue, requiredScopes);
    }

    private ScopesSet getRequiredScopes(final HttpServletRequest request) {
        var requestUri = request.getRequestURI();
        var scopes = new ScopesSet();
        if (requestUri.startsWith("/words")) {
            var requestMethod = request.getMethod();
            switch (requestMethod) {
            case "GET":
                scopes.add(Scopes.READ);
                break;
            case "POST":
                scopes.add(Scopes.WRITE);
                break;
            case "DELETE":
                scopes.add(Scopes.DELETE);
                break;
            default:
                throw new RuntimeException("unknown request method: %s".formatted(requestMethod));
            }
        }
        return scopes;
    }

}
