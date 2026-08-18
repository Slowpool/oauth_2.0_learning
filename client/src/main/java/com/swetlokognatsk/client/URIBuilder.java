package com.swetlokognatsk.client;

import java.util.Map;

import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UrlPathHelper;

public class URIBuilder {

    public static String buildAuthorizationURI() {
        var baseUri = AuthorizationServer.getAuthorizationEndpoint();
        var authorizationUri = buildURI(baseUri, null, "");
        return authorizationUri;
    }

    private static String buildURI(String base, Map<String,String> options, String hash) {
        var uri = UriComponentsBuilder.fromUriString(base);
        uri.replaceQuery("");
        for (var option : options.keySet()) {
            uri.queryParam(option, options.get(option));
        }
        uri.fragment(hash);
        return uri.toUriString();
    }
}
