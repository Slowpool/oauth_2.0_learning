package com.swetlokognatsk.oauth_db.daos;

import com.swetlokognatsk.oauth_db.models.AccessToken;
import com.swetlokognatsk.oauth_db.models.AccessTokenValue;
import com.swetlokognatsk.oauth_db.AccessTokenNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

public final class AccessTokenDao {

    // TODO how it works https://www.baeldung.com/jpa-hibernate-persistence-context
    @PersistenceContext
    private EntityManager entityManager;

    public AccessToken findByValue(final AccessTokenValue accessTokenValue) throws AccessTokenNotFoundException {
        var criteriaBuilder = entityManager.getCriteriaBuilder();

        var query = criteriaBuilder.createQuery(AccessToken.class);

        var root = query.from(AccessToken.class);

        var valuePredicate = criteriaBuilder.equal(root.get("value"), accessTokenValue);

        query.where(valuePredicate);

        var result = entityManager.createQuery(query);

        try {
            return result.getSingleResult();
        }
        catch (NoResultException e) {
            throw new AccessTokenNotFoundException();
        }
    }
}
