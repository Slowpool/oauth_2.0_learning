package com.swetlokognatsk.client;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.util.UriComponentsBuilder;

public class URIBuilder {

    private static String buildURI(String base, Map<String, String> options, String hash) {
        var uri = UriComponentsBuilder.fromUriString(base);
        uri.replaceQuery("");
        for (var option : options.keySet()) {
            uri.queryParam(option, options.get(option));
        }
        if (hash != null) {
            uri.fragment(hash);
        }
        return uri.encode().toUriString();
    }

    public static String buildAuthorizationURI() {
        var baseUri = AuthorizationServer.getAuthorizationEndpoint();
        var options = new HashMap<String, String>();
        options.put("response_type", "code");
        options.put("client_id", Client.getId());
        options.put("redirect_uri", Client.getRedirectURI());
        var authorizationUri = buildURI(baseUri, options, null);
        return authorizationUri;
    }

    public static String buildProtectedResourceURI() {
        return "nope";
    }

}
