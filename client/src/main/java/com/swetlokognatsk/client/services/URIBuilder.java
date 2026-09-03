package com.swetlokognatsk.client.services;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;
import com.swetlokognatsk.client.Client;
import com.swetlokognatsk.client.external_services.AuthorizationServer;
import com.swetlokognatsk.client.external_services.ProtectedResource;

public class URIBuilder {

    private static String buildURI(final String base, final Map<String, String> options, final String hash, final boolean encode) {
        var uri = UriComponentsBuilder.fromUriString(base);
        uri.replaceQuery("");
        for (var option : options.keySet()) {
            uri.queryParam(option, options.get(option));
        }

        if (hash != null) {
            uri.fragment(hash);
        }

        if (encode) {
            uri.encode();
        }

        return uri.build(encode).toUriString();
    }

    public static String buildAuthorizationURI(final String state) {
        var baseUri = AuthorizationServer.getExternalAuthorizationEndpoint();

        var options = new HashMap<String, String>();
        options.put("response_type", "code");
        options.put("client_id", Client.getId());
        var encodedRedirectUri = getClientRedirectURI(true);
        options.put("redirect_uri", encodedRedirectUri);
        options.put("state", state);

        var authorizationUri = buildURI(baseUri, options, null, false);
        return authorizationUri;
    }

    private static String getClientRedirectURI(final boolean encode) {
        var redirectUri = Client.getRedirectURI();
        var uriBuilder = UriComponentsBuilder.fromUriString(redirectUri);
        // TODO that's it or some query params should be added to that uri?

        var decodedUri = uriBuilder.toUriString();
        var uri = encode ? UriUtils.encode(decodedUri, StandardCharsets.UTF_8) : decodedUri;
        return uri;
    }

    public static String buildProtectedResourceURI() {
        return "nope";
    }

    public static String buildRemoveWordUri(final String wordToDelete) {
        var endpointUri = ProtectedResource.getRemoveWordEndpoint();

        var options = new HashMap<String, String>();
        options.put("wordToDelete", wordToDelete);

        return buildURI(endpointUri, options, null, true);
    }

}
