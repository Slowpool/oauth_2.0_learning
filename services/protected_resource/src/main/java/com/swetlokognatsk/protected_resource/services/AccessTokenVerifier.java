package com.swetlokognatsk.protected_resource.services;

import java.util.HashSet;
import java.util.Set;

import com.swetlokognatsk.oauth_db.models.AccessToken;
import com.swetlokognatsk.oauth_db.models.AccessTokenValue;
import com.swetlokognatsk.oauth_db.models.ScopeEntity;
import com.swetlokognatsk.oauth_db.models.Scopes;
import com.swetlokognatsk.oauth_db.models.ScopesSet;
import com.swetlokognatsk.protected_resource.AccessTokenNotFoundException;
import com.swetlokognatsk.protected_resource.RequiredScopesAreNotGrantedException;
import com.swetlokognatsk.protected_resource.ports.Database;

public final class AccessTokenVerifier {

    private final Database database;

    public AccessTokenVerifier(final Database database) {
        this.database = database;
    }

    public void verify(final AccessTokenValue accessTokenValue, final ScopesSet requiredScopes) throws AccessTokenNotFoundException, RequiredScopesAreNotGrantedException {
        var accessToken = database.findAccessToken(accessTokenValue);
        // TODO verify is not expired

        verifyScopes(accessToken, requiredScopes);
    }

    public void verifyScopes(final AccessToken accessToken, final ScopesSet requiredScopes) throws RequiredScopesAreNotGrantedException {
        var ownedScopes = castScopeEntitiesToEnum(accessToken.getScopes());
        if (!ownedScopes.containsAll(requiredScopes)) {
            throw new RequiredScopesAreNotGrantedException();
        }
    }

    private ScopesSet castScopeEntitiesToEnum(final Set<ScopeEntity> scopeEntities) {
        var mappedScopes = scopeEntities.stream()
            .map((ScopeEntity scopeEntity) -> Scopes.valueOf(scopeEntity.getName()))
            .toList();
        return new ScopesSet(mappedScopes);
    }

}
