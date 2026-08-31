package com.swetlokognatsk.client.services;

import static com.swetlokognatsk.client.model.TokenStrategy.SINGLE_ACCESS_TOKEN;
import java.io.IOException;
import java.net.Authenticator;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;
import com.swetlokognatsk.client.Client;
import com.swetlokognatsk.client.external_services.AuthorizationServer;
import com.swetlokognatsk.client.external_services.ProtectedResource;
import com.swetlokognatsk.client.model.AccessToken;
import com.swetlokognatsk.client.model.RefreshAndAccessTokensPair;
import com.swetlokognatsk.client.model.RefreshToken;
import com.swetlokognatsk.client.model.Token;
import com.swetlokognatsk.client.model.TokenStrategy;
import static org.springframework.web.bind.annotation.RequestMethod.*;

public final class AppHttpClient {

    private static HttpResponse<?> sendHttpRequest(final RequestMethod method, final String uri, final Map<String, String> headers, final Map<String, String> body) throws IOException, InterruptedException {
        var requestBuilder = HttpRequest.newBuilder(URI.create(uri));
        switch (method) {
        case GET:
            requestBuilder.GET();
            break;
        case POST:
            var bodyContent = mapBodyToString(body);
            var bodyPublisher = BodyPublishers.ofString(bodyContent);
            requestBuilder.POST(bodyPublisher);
            break;
        default:
            throw new RuntimeException("unknown method: %s".formatted(method));
        }

        addHeaders(requestBuilder, headers);

        var request = requestBuilder.build();

        var client = HttpClient.newBuilder().followRedirects(Redirect.NEVER).connectTimeout(Duration.ofSeconds(10)).build();
        var response = client.send(request, BodyHandlers.ofString());
        return response;
    }

    private static HttpResponse<?> sendHttpRequest(final RequestMethod method, final String uri, final Map<String, String> headers) throws IOException, InterruptedException {
        return sendHttpRequest(method, uri, headers, new HashMap<>());
    }

    private static void addHeaders(final HttpRequest.Builder requestBuilder, final Map<String, String> headers) {
        for (var headerName : headers.keySet()) {
            requestBuilder.header(headerName, headers.get(headerName));
        }
    }

    private static String mapBodyToString(final Map<String, String> body) {
        if (body.size() == 0) {
            return "";
        }

        var bodyCopy = Map.copyOf(body);
        var uriBuilder = UriComponentsBuilder.newInstance();
        bodyCopy.forEach(uriBuilder::queryParam);

        var urlEncodedBody = uriBuilder.build().encode().toUriString()
                // "?key1=val1,key2=val2" => "key1=val1,key2=val2"
                .substring(1);
        return urlEncodedBody;
    }

    private static String encodeClientCredentials(final String clientId, final String clientSecret) {
        var contatenatedCredentials = "%s:%s".formatted(clientId, clientSecret);
        var encodedCredentials = Base64.getEncoder().encodeToString(contatenatedCredentials.getBytes());
        return encodedCredentials;
    }

    // TODO in prod tokenStrategy is redundant here
    public static String sendTokenRequest(final String code, final TokenStrategy tokenStrategy) throws IOException, InterruptedException {
        var uri = AuthorizationServer.getInternalTokenEndpoint();

        // TODO revise it later, on connecting all parts together
        var headers = buildAuthHeaders();

        var body = new HashMap<String, String>();
        body.put("grant_type", "authorization_code");
        body.put("code", code);
        // TODO what does it do here? upd: it's used for security reasons. book says it'll be implemented on auth server's side in further chapter. this redirect_uri must be the same as the one from `/authorize?redirect_uri=...` redirect from client app.
        body.put("redirect_uri", Client.getRedirectURI());
        var jsonToken = switch (tokenStrategy) {
        case SINGLE_ACCESS_TOKEN -> """
                {
                    "value": "accaccaccaccacc",
                    "type": "access"
                }
                    """;
        case REFRESH_AND_ACCESS_PAIR -> """
                {
                    "access_token": "accaccaccaccacc",
                    "token_type": "Bearer",
                    "refresh_token": "refrefrefrefrefref"
                }
                    """;
        default -> throw new RuntimeException("unknown token strategy: %s".formatted(tokenStrategy));
        };
        return jsonToken;
        // TODO return real raw json
        // var result = sendHttpRequest(POST, uri, new HashMap<>(), body);
        // if (result.statusCode() >= 200 && result.statusCode() <= 299) {
        //     return "latch";
        // }
        // else {
        //     throw new IOException("auth returned error. status: %d, message: %s".formatted(result.statusCode(), result.body()));
        // }
    }

    private static Map<String, String> buildAuthHeaders() {
        var headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        var credentials = encodeClientCredentials(Client.getId(), Client.getSecret());
        headers.put("Authorization", "Basic %s".formatted(credentials));
        return headers;
    }

    public static String sendTokenRefreshingRequest(final RefreshToken refreshToken) throws IOException, InterruptedException {
        var headers = buildAuthHeaders();

        var body = new HashMap<String, String>();
        body.put("grant_type", "refresh_token");
        body.put("refresh_token", refreshToken.value);

        return """
                {
                    "access_token": "newacnewacnewacnewac",
                    "token_type": "Bearer",
                    "refresh_token": "newrefnewrefnewrefnewref"
                }
                    """;
        // TODO return real raw json
        // var result = sendHttpRequest(POST, uri, new HashMap<>(), body);
        // if (result.statusCode() >= 200 && result.statusCode() <= 299) {
        //     return "latch";
        // }
        // else {
        //     throw new IOException("auth returned error. status: %d, message: %s".formatted(result.statusCode(), result.body()));
        // }
    }

    private static boolean isFirstTry = true;

    public static String fetchProtectedResource(final AccessToken accessToken) throws IOException, InterruptedException {
        // // simulating the access token expiring
        // if (isFirstTry) {
        //     isFirstTry = false;
        //     throw new IOException("access token has expired");
        // }

        var uri = ProtectedResource.getFetchResourceEndpoint();

        var headers = buildResourceHeaders(accessToken);

        var result = sendHttpRequest(GET, uri, headers);
        return bodyUnlessError(result);
    }

    private static Map<String, String> buildResourceHeaders(final AccessToken accessToken) {
        var headers = new HashMap<String, String>();
        // TODO what other types exist besides bearer?
        var credentials = "Bearer %s".formatted(accessToken.value);
        headers.put("Authorization", credentials);
        return headers;
    }

    public static String sendGetWordsRequest(final AccessToken accessToken) throws IOException, InterruptedException {
        var uri = ProtectedResource.getWordsListEndpoint();

        var headers = buildResourceHeaders(accessToken);
        var result = sendHttpRequest(GET, uri, headers);

        return bodyUnlessError(result);
    }

    private static String bodyUnlessError(final HttpResponse<?> result) throws IOException {
        var body = result.body();
        if (result.statusCode() >= 200 && result.statusCode() <= 299) {
            return (String) body;
        } else {
            throw new IOException("response code was wrong: %d. message: %s".formatted(result.statusCode(), body));
        }
    }
}
