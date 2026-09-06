package com.swetlokognatsk.authorization_server.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.*;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import com.swetlokognatsk.authorization_server.daos.ClientsDao;
import com.swetlokognatsk.authorization_server.exceptions.AuthorizationRequestNotFoundException;
import com.swetlokognatsk.authorization_server.exceptions.ClientNotFoundException;
import com.swetlokognatsk.authorization_server.exceptions.InvalidRedirectUriException;
import com.swetlokognatsk.authorization_server.exceptions.InvalidScopeException;
import com.swetlokognatsk.authorization_server.exceptions.UnsupportedResponseTypeException;
import com.swetlokognatsk.authorization_server.models.AuthorizationCode;
import com.swetlokognatsk.authorization_server.models.AuthorizationRequest;
import com.swetlokognatsk.authorization_server.models.Client;
import com.swetlokognatsk.authorization_server.models.RedirectUri;
import com.swetlokognatsk.authorization_server.ports.Database;
import com.swetlokognatsk.authorization_server.services.UriBuilder;
import com.swetlokognatsk.oauth_db.models.ScopeEntity;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class FrontChannelAuthorizationServerController {

    private static final String HOME = "/";
    private static final String AUTHORIZATION_ENDPOINT = "/authorize";
    private static final String APPROVE_AUTH_ENDPOINT = "/approve-authorization";
    private static final String DENY_AUTH_ENDPOINT = "/deny-authorization";

    private static final String SCOPE_SEPARATOR = " ";

    private final ApplicationContext ctx;
    private final Database database;

    public FrontChannelAuthorizationServerController(final ApplicationContext ctx, final Database database) {
        this.ctx = ctx;
        this.database = database;
    }

    @RequestMapping(HOME)
    public String home() {
        return "hello";
    }

    @GetMapping(AUTHORIZATION_ENDPOINT)
    public ModelAndView authorize(final HttpServletResponse response, @RequestParam(name = "client_id") final String clientId, @RequestParam(name = "redirect_uri") final String redirectUri, @RequestParam(name = "response_type", required = false) final String responseType, @RequestParam final String state, @RequestParam final String scope, final Model model) {
        String view;
        try {
            var client = getClientByClientId(clientId);
            validateClient(client, redirectUri);
            model.addAttribute("clientId", clientId);
            model.addAttribute("redirectUri", redirectUri);

            validateScope(client, scope);
            var scopes = parseScopes(scope);
            model.addAttribute("scopes", scopes);

            var requestId = saveAuthorizationRequest(database, clientId, redirectUri, responseType, state);
            model.addAttribute("requestId", requestId);

            model.addAttribute("approveEndpoint", APPROVE_AUTH_ENDPOINT);
            model.addAttribute("denyEndpoint", DENY_AUTH_ENDPOINT);
            view = "approve";
        } catch (ClientNotFoundException e) {
            model.addAttribute("error", "unknown client: %s".formatted(clientId));
            response.setStatus(NOT_FOUND.value());
            view = "error";
        } catch (InvalidRedirectUriException e) {
            model.addAttribute("error", "incorrect redirectUri: %s".formatted(redirectUri));
            response.setStatus(UNPROCESSABLE_CONTENT.value());
            view = "error";
        } catch (InvalidScopeException e) {
            model.addAttribute("error", "invalid scopes requested: %s".formatted(scope));
            response.setStatus(BAD_REQUEST.value());
            view = "error";
        }
        return new ModelAndView(view, model.asMap());
    }

    @PostMapping(APPROVE_AUTH_ENDPOINT)
    public RedirectView approveAuthorization(final HttpServletResponse response, @RequestParam final String requestId, @RequestParam final List<String> permittedScopes) {
        AuthorizationRequest authorizationRequest;
        try {
            validateRequestId(requestId);
            authorizationRequest = database.popAuthorizationRequest(requestId);
        } catch (AuthorizationRequestNotFoundException e) {
            sendErrorDirectlyToUser(e, response, UNPROCESSABLE_CONTENT.value(), "No matching authorization request");
            return null;
        }

        String redirectUri;
        try {
            validateResponseType(authorizationRequest.responseType());
            var clientId = authorizationRequest.clientId();
            var client = getClientByClientId(clientId);
            validateScopes(client, permittedScopes);

            var code = generateCode();
            saveAuthorizationCode(requestId, code, clientId, permittedScopes);

            redirectUri = UriBuilder.buildRedirectUriOnSuccess(code, authorizationRequest);
        } catch (UnsupportedResponseTypeException e) {
            // TODO how client should react to it?
            redirectUri = UriBuilder.buildRedirectUriOnUnsupportedResponseType(authorizationRequest);
        }
        // ofc redirects should be further, but ain't gonna bother myself with it
        catch (ClientNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InvalidScopeException e) {
            throw new RuntimeException(e);
        }

        return new RedirectView(redirectUri);
    }

    private Client getClientByClientId(final String clientId) throws ClientNotFoundException {
        return database.getClientByClientId(clientId);
    }

    private void saveAuthorizationCode(final String requestId, final String code, final String clientId, final List<String> scopes) {
        var authorizationCode = new AuthorizationCode(requestId, code, clientId, scopes);
        database.saveAuthorizationCode(authorizationCode);
    }

    private static String generateCode() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private void validateResponseType(final String responseType) throws UnsupportedResponseTypeException {
        switch (responseType) {
        case "code":
            break;
        default:
            throw new UnsupportedResponseTypeException();
        }
    }

    @PostMapping(DENY_AUTH_ENDPOINT)
    public RedirectView denyAuthorization(final HttpServletResponse response, @RequestParam final String requestId) {
        try {
            validateRequestId(requestId);
            var authorizationRequest = database.popAuthorizationRequest(requestId);
            // TODO eliminate UriBuilder and use RedirectView attributes instead
            // TODO how client should react to it?
            var redirectUri = UriBuilder.buildRedirectUriOnAccessDenied(authorizationRequest);
            return new RedirectView(redirectUri);
        } catch (AuthorizationRequestNotFoundException e) {
            sendErrorDirectlyToUser(e, response, UNPROCESSABLE_CONTENT.value(), "No matching authorization request");
            return null;
        }
    }

    // sends the error not via front channel
    private void sendErrorDirectlyToUser(final AuthorizationRequestNotFoundException e, final HttpServletResponse response, final int status, final String message) {
        response.setStatus(status);
        try {
            response.getWriter().write(message);
        } catch (Throwable innerE) {
            e.addSuppressed(innerE);
        }
    }

    private void validateRequestId(final String requestId) throws AuthorizationRequestNotFoundException {
        database.getAuthorizationRequest(requestId);
    }

    /**
     * @return String requestId
     */
    private String saveAuthorizationRequest(final Database database, final String clientId, final String redirectUri, final String responseType, final String state) {
        String requestId = UUID.randomUUID()
                .toString();
        var authorizationRequest = new AuthorizationRequest(requestId, clientId, redirectUri, responseType, state);
        database.saveAuthorizationRequest(authorizationRequest);
        return requestId;
    }

    private void validateClient(final Client client, final String receivedRedirectUri) throws InvalidRedirectUriException {
        var clientRedirectUris = client.getRedirectUris()
                .stream()
                .map((RedirectUri redirectUri) -> redirectUri.uri)
                .toList();
        if (!clientRedirectUris.contains(receivedRedirectUri)) {
            throw new InvalidRedirectUriException();
        }
    }

    private void validateScope(final Client client, final String scope) throws InvalidScopeException {
        if (scope == null) {
            throw new InvalidScopeException();
        }

        var parsedScopes = parseScopes(scope);
        validateScopes(client, parsedScopes);
    }

    private void validateScopes(final Client client, final List<String> scopes) throws InvalidScopeException {
        if (clientAsksTooManyScopes(client.getScopes(), scopes)) {
            throw new InvalidScopeException();
        }

        if (!clientHasTheseScopes(client, scopes)) {
            throw new InvalidScopeException();
        }
    }

    private List<String> parseScopes(final String scope) {
        return Arrays.asList(scope.toLowerCase()
                .split(SCOPE_SEPARATOR));
    }

    private boolean clientAsksTooManyScopes(final Set<ScopeEntity> clientScopes, final List<String> parsedScopes) {
        return parsedScopes.size() > clientScopes.size();
    }

    private boolean clientHasTheseScopes(final Client client, final List<String> scopes) {
        var clientScopes = client.getScopes()
                .stream()
                .map((ScopeEntity scopeEntity) -> scopeEntity.getName()
                        .toLowerCase())
                .toList();

        return clientScopes.containsAll(scopes);
    }

    @RequestMapping("/clients-test")
    public String clientsTest() {
        var clientsDao = ctx.getBean(ClientsDao.class);
        var clients = clientsDao.getClients();
        var firstClient = clients.getFirst();
        var redirectUris = firstClient.getRedirectUris();
        int numberOfRedirectUris = redirectUris.size();
        var firstRedirectUri = redirectUris.getFirst();
        return "done";
    }

    @RequestMapping("/clients-test2")
    public ResponseEntity<?> clientsTest2() {
        try {
            var client = database.getClientByClientId("client-1");
            return ok("done");
        } catch (ClientNotFoundException e) {
            return internalServerError().body("fail: " + e.getMessage());
        }
    }
}
