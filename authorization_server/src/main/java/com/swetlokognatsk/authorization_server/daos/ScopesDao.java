package com.swetlokognatsk.authorization_server.daos;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import com.swetlokognatsk.oauth_db.models.ScopeEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class ScopesDao {

    @PersistenceContext
    private EntityManager entityManager;

    // yep, List<String> should be replaced with Set<String> over the whole app, but it doesn't matter for learning purposes.
    public Set<ScopeEntity> findAllByValues(final List<String> scopes) {
        // minor hack, learning project after all
        var scopesInUpperCase = toUpperCase(scopes);

        var query = entityManager.getCriteriaBuilder()
                .createQuery(ScopeEntity.class);
        var root = query.from(ScopeEntity.class);
        query.select(root)
                .where(root.get("name")
                        .in(scopesInUpperCase));
        var scopeEntities = entityManager.createQuery(query)
                .getResultList();
        return new HashSet<ScopeEntity>(scopeEntities);
    }

    private List<String> toUpperCase(final List<String> scopes) {
        return scopes.stream()
                .map((scope) -> scope.toUpperCase())
                .toList();
    }
}
