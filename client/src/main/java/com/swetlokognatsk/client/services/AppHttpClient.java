package com.swetlokognatsk.client.services;

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
import org.springframework.web.util.UriComponentsBuilder;
import com.swetlokognatsk.client.Client;
import com.swetlokognatsk.client.external_services.AuthorizationServer;
import com.swetlokognatsk.client.external_services.ProtectedResource;
import com.swetlokognatsk.client.model.AccessToken;
import com.swetlokognatsk.client.model.RefreshAndAccessTokensPair;
import com.swetlokognatsk.client.model.Token;

public final class AppHttpClient {

    private static HttpResponse<?> sendHttpRequest(final String method, final String uri, final Map<String, String> headers, final Map<String, String> body) throws IOException, InterruptedException {
        // TODO use headers
        var bodyContent = mapBodyToString(body);

        var bodyPublisher = BodyPublishers.ofString(bodyContent);
        var request = HttpRequest.newBuilder(URI.create(uri)).POST(bodyPublisher).build();

        var client = HttpClient.newBuilder().followRedirects(Redirect.NEVER).connectTimeout(Duration.ofSeconds(10)).build();
        var response = client.send(request, BodyHandlers.ofString());
        return response;
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

    public static String sendTokenRequest(final String code) throws IOException, InterruptedException {
        var uri = AuthorizationServer.getTokenEndpoint();

        // TODO revise it later, on connecting all parts together
        var headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        var credentials = encodeClientCredentials(Client.getId(), Client.getSecret());
        headers.put("Authorization", "Basic %s".formatted(credentials));

        var body = new HashMap<String, String>();
        body.put("grant_type", "authorization_code");
        body.put("code", code);
        // TODO what does it do here? upd: it's used for security reasons. book says it'll be implemented on auth server's side in further chapter. this redirect_uri must be the same as the one from `/authorize?redirect_uri=...` redirect from client app.
        body.put("redirect_uri", Client.getRedirectURI());
        return """
                {
                    "value": "asdkfjklsadfjksdfdkjfd",
                    "type": "access"
                }
                    """;
        // TODO return real raw json
        // var result = sendHttpRequest("POST", uri, new HashMap<>(), body);
        // if (result.statusCode() >= 200 && result.statusCode() <= 299) {
        //     return "latch";
        // }
        // else {
        //     throw new IOException("auth returned error. status: %d, message: %s".formatted(result.statusCode(), result.body()));
        // }
    }

    public static String sendTokenRefreshingRequest(final RefreshAndAccessTokensPair refreshToken) throws IOException, InterruptedException {
        // TODO sendTokenRefreshingRequest
        return "latch";
    }

    public static String fetchProtectedResource(final AccessToken token) throws IOException, InterruptedException {
        var uri = ProtectedResource.getFetchResourceEndpoint();

        var headers = new HashMap<String, String>();
        var credentials = "Bearer %s".formatted(token.value);
        headers.put("Authorization", credentials);

        var result = sendHttpRequest("POST", uri, headers, new HashMap<>());
        if (result.statusCode() >= 200 && result.statusCode() <= 299) {
            return "latch";
            // TODO return real raw json
            // var result = sendHttpRequest("POST", uri, new HashMap<>(), body);
            // if (result.statusCode() >= 200 && result.statusCode() <= 299) {
            //     return "latch";
            // }
            // else {
            //     throw new IOException("auth returned error. status: %d, message: %s".formatted(result.statusCode(), result.body()));
            // }
        } else {
            throw new IOException("response code was wrong: %d".formatted(result.statusCode()));
        }
    }
}
