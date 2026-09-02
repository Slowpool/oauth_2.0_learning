package com.swetlokognatsk.authorization_server.daos;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.swetlokognatsk.authorization_server.ClientNotFoundException;
import com.swetlokognatsk.authorization_server.models.Client;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

@Repository
public class ClientsDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Client> getClients() {
        return entityManager.createQuery("SELECT c FROM Client c", Client.class).getResultList();
    }

    public Client findByClientId(final String clientId) throws ClientNotFoundException {
        var criteriaBuilder = entityManager.getCriteriaBuilder();

        var query = criteriaBuilder.createQuery(Client.class);

        var root = query.from(Client.class);

        var clientIdPredicate = criteriaBuilder.equal(root.get("clientId"), clientId);

        query.where(clientIdPredicate);

        var result = entityManager.createQuery(query);

        try {
            return result.getSingleResult();
        }
        catch (NoResultException e) {
            throw new ClientNotFoundException();
        }
    }

}
