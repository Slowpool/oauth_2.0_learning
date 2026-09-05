package com.swetlokognatsk.client.services;

import com.swetlokognatsk.client.model.AccessToken;
import com.swetlokognatsk.client.model.RefreshAndAccessTokensPair;
import com.swetlokognatsk.client.model.RefreshToken;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.node.StringNode;

class RefreshAndAccessTokensPairDeserializer extends StdDeserializer<RefreshAndAccessTokensPair> {

    public RefreshAndAccessTokensPairDeserializer() {
        this(null);
    }

    public RefreshAndAccessTokensPairDeserializer(Class<?> vc) {
        super(vc);
    }

    public RefreshAndAccessTokensPair deserialize(final JsonParser parser, final DeserializationContext ctx) throws JacksonException {
        
        var jsonNode = parser.readValueAsTree();

        var accessTokenValue = ((StringNode) jsonNode.get("access_token")).asString();
        var accessTokenTypeValue = ((StringNode) jsonNode.get("token_type")).asString();
        // TODO parse expiresIn
        var accessToken = new AccessToken(accessTokenValue, accessTokenTypeValue, 1937);

        var refreshTokenValue = ((StringNode) jsonNode.get("refresh_token")).asString();
        var refreshToken = new RefreshToken(refreshTokenValue);

        return new RefreshAndAccessTokensPair(refreshToken, accessToken);
    }
}
