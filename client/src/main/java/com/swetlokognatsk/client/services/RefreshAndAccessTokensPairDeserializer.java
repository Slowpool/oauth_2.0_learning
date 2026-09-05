package com.swetlokognatsk.client.services;

import com.swetlokognatsk.client.model.AccessToken;
import com.swetlokognatsk.client.model.RefreshAndAccessTokensPair;
import com.swetlokognatsk.client.model.RefreshToken;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.node.IntNode;
import tools.jackson.databind.node.StringNode;

class RefreshAndAccessTokensPairDeserializer extends StdDeserializer<RefreshAndAccessTokensPair> {

    public RefreshAndAccessTokensPairDeserializer() {
        this(null);
    }

    public RefreshAndAccessTokensPairDeserializer(Class<?> vc) {
        super(vc);
    }

    public RefreshAndAccessTokensPair deserialize(final JsonParser parser, final DeserializationContext ctx) throws JacksonException {
        
        var jsonRoot = parser.readValueAsTree();
        
        var accessTokenRoot = jsonRoot.get("accessToken");
        var accessTokenValue = ((StringNode) accessTokenRoot.get("accessToken")).asString();
        var accessTokenTypeValue = ((StringNode) accessTokenRoot.get("type")).asString();
        var accessTokenExpiresIn = ((IntNode) accessTokenRoot.get("expiresIn")).intValue();
        // TODO parse expiresIn
        var accessToken = new AccessToken(accessTokenValue, accessTokenTypeValue, accessTokenExpiresIn);

        var refreshTokenRoot = jsonRoot.get("refreshToken");
        var refreshTokenValue = ((StringNode) refreshTokenRoot.get("refreshToken")).asString();
        var expiresIn = ((IntNode) refreshTokenRoot.get("expiresIn")).intValue();
        var refreshToken = new RefreshToken(refreshTokenValue, expiresIn);

        return new RefreshAndAccessTokensPair(refreshToken, accessToken);
    }
}
