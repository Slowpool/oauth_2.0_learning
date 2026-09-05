package com.swetlokognatsk.protected_resource;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.swetlokognatsk.oauth_db.AccessTokenNotFoundException;
import com.swetlokognatsk.oauth_db.models.AccessTokenValue;
import com.swetlokognatsk.oauth_db.models.Scopes;
import com.swetlokognatsk.oauth_db.models.ScopesSet;
import com.swetlokognatsk.protected_resource.exceptions.AccessTokenIsExpiredException;
import com.swetlokognatsk.protected_resource.exceptions.RequiredScopesAreNotGrantedException;
import com.swetlokognatsk.protected_resource.services.AccessTokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static com.swetlokognatsk.protected_resource.services.AuthHeaderHelper.*;
import java.io.IOException;

@Component
public final class OAuthInterceptor implements HandlerInterceptor {

    private AccessTokenValidator accessTokenValidator;

    public OAuthInterceptor(final AccessTokenValidator accessTokenValidator) {
        this.accessTokenValidator = accessTokenValidator;
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
            // TODO figure out what is this realm about
            response.addHeader("WWW-Authenticate", "Bearer realm=protected-resource:8083");
            response.setStatus(400);
            response.getWriter().write("accessToken is not found in request");
        } else {
            var accessToken = new AccessTokenValue(accessTokenValue);
            try {
                // TODO ensure it works fine when auth server is done
                var requiredScopes = getRequiredScopes(request);
                validateAccessToken(accessToken, requiredScopes);
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
            catch (AccessTokenIsExpiredException e) {
                authIsSuccessful = false;
                response.setStatus(401);
                response.getWriter().write("accessToken is expired");
            }
        }
        return authIsSuccessful;
    }

    private void validateAccessToken(final AccessTokenValue accessTokenValue, final ScopesSet requiredScopes) throws AccessTokenNotFoundException, AccessTokenIsExpiredException, RequiredScopesAreNotGrantedException {
        accessTokenValidator.validate(accessTokenValue, requiredScopes);
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
