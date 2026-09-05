package com.swetlokognatsk.protected_resource.services;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import com.swetlokognatsk.oauth_db.models.AccessToken;
import com.swetlokognatsk.oauth_db.models.AccessTokenValue;
import com.swetlokognatsk.oauth_db.models.ScopeEntity;
import com.swetlokognatsk.oauth_db.models.Scopes;
import com.swetlokognatsk.oauth_db.models.ScopesSet;
import com.swetlokognatsk.oauth_db.AccessTokenNotFoundException;
import com.swetlokognatsk.protected_resource.exceptions.AccessTokenIsExpiredException;
import com.swetlokognatsk.protected_resource.exceptions.RequiredScopesAreNotGrantedException;
import com.swetlokognatsk.protected_resource.ports.Database;

public final class AccessTokenValidator {

    private final Database database;

    public AccessTokenValidator(final Database database) {
        this.database = database;
    }

    public void validate(final AccessTokenValue accessTokenValue, final ScopesSet requiredScopes) throws AccessTokenNotFoundException, AccessTokenIsExpiredException, RequiredScopesAreNotGrantedException {
        var accessToken = database.findAccessToken(accessTokenValue);
        validateIsNotExpired(accessToken);
        validateScopes(accessToken, requiredScopes);
    }

    public void validateIsNotExpired(final AccessToken accessToken) throws AccessTokenIsExpiredException {
        var expirationMoment = calcExpirationMoment(accessToken);
        if (momentAlreadyHappened(expirationMoment)) {
            // TODO check what happens when it is expired
            throw new AccessTokenIsExpiredException();
        }
    }

    private LocalDateTime calcExpirationMoment(final AccessToken accessToken) {
        var tokenCreatedAt = accessToken.getCreatedOnDb();
        return tokenCreatedAt.plusSeconds(accessToken.getExpiresIn());
    }

    private boolean momentAlreadyHappened(final LocalDateTime moment) {
        var now = LocalDateTime.now();
        return now.isAfter(moment) || now.isEqual(moment);
    }

    public void validateScopes(final AccessToken accessToken, final ScopesSet requiredScopes) throws RequiredScopesAreNotGrantedException {
        var ownedScopes = castScopeEntitiesToEnum(accessToken.getScopes());
        if (!ownedScopes.containsAll(requiredScopes)) {
            throw new RequiredScopesAreNotGrantedException();
        }
    }

    private ScopesSet castScopeEntitiesToEnum(final Set<ScopeEntity> scopeEntities) {
        var mappedScopes = scopeEntities.stream().map((ScopeEntity scopeEntity) -> Scopes.valueOf(scopeEntity.getName())).toList();
        return new ScopesSet(mappedScopes);
    }

}
