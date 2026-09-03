package com.swetlokognatsk.authorization_server.services;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.util.UriComponentsBuilder;
import com.swetlokognatsk.authorization_server.models.AuthorizationRequest;

public class UriBuilder {

    // DRY violation, copied from client/UriBuilder. 
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

    private static Map<String, String> newOptions() {
        return new HashMap<String, String>();
    }

    public static String buildRedirectUriOnAccessDenied(final AuthorizationRequest authorizationRequest) {
        var baseUri = authorizationRequest.redirectUri();
        var options = newOptions();
        options.put("error", "access_denied");
        return buildURI(baseUri, options, null, false);
    }

    public static String buildRedirectUriOnUnsupportedResponseType(final AuthorizationRequest authorizationRequest) {
        var baseUri = authorizationRequest.redirectUri();
        var options = newOptions();
        options.put("error", "unsupported_response_type");
        return buildURI(baseUri, options, null, false);
    }
}
