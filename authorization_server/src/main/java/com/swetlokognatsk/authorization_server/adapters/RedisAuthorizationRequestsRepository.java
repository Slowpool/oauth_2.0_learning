package com.swetlokognatsk.authorization_server.adapters;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import com.swetlokognatsk.authorization_server.exceptions.AuthorizationRequestNotFoundException;
import com.swetlokognatsk.authorization_server.models.AuthorizationRequest;
import com.swetlokognatsk.authorization_server.ports.AuthorizationRequestsRepository;

@Repository
public class RedisAuthorizationRequestsRepository implements AuthorizationRequestsRepository {

    private final StringRedisTemplate redisTemplate;

    // TODO why template?
    public RedisAuthorizationRequestsRepository(final StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(AuthorizationRequest authorizationRequest) {
        // TODO RedisAuthorizationRequestsRepository
        // redisTemplate.();
    }

    public AuthorizationRequest findByKey(final String key) throws AuthorizationRequestNotFoundException {
        // TODO RedisAuthorizationRequestsRepository
        return null;
        // redisTemplate.
    }

}
