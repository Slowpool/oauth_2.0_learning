package com.swetlokognatsk.client;

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

public final class AppHttpClient {
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
        body.put("redirect_uri", Client.getRedirectURI());
		var result = sendHttpRequest("POST", uri, body);
        if (result.statusCode() >= 200 && result.statusCode() <= 299) {
            // TODO return real stuff
            return "latch";
        }
        else {
            throw new IOException("response code was wrong: %d".formatted(result.statusCode()));
        }
	}

	private static String encodeClientCredentials(final String clientId, final String clientSecret) {
		var contatenatedCredentials = "%s:%s".formatted(clientId, clientSecret);
		var encodedCredentials = Base64.getEncoder().encodeToString(contatenatedCredentials.getBytes());
		return encodedCredentials;
	}

    private static HttpResponse<?> sendHttpRequest(final String method, final String uri, final Map<String,String> body) throws IOException, InterruptedException {
        var bodyContent = mapBodyToString(body);
        
        var bodyPublisher = BodyPublishers.ofString(bodyContent);
        var request = HttpRequest.newBuilder(URI.create(uri))
            .POST(bodyPublisher)
            .build()
            ;
        
        var client = HttpClient.newBuilder()
            .followRedirects(Redirect.NEVER)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        var response = client.send(request, BodyHandlers.ofString());
        return response;
    }

    private static String mapBodyToString(final Map<String, String> body) {
        var bodyCopy = Map.copyOf(body);
        var uriBuilder = UriComponentsBuilder.newInstance();
        bodyCopy.forEach(uriBuilder::queryParam);

        var urlEncodedBody = uriBuilder.build()
            .encode()
            .toUriString()
            // "?key1=val1,key2=val2" => "key1=val1,key2=val2"
            .substring(1);
        return urlEncodedBody;
    }
}
