package com.swetlokognatsk.authorization_server.adapters;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import com.swetlokognatsk.authorization_server.exceptions.AuthorizationRequestNotFoundException;
import com.swetlokognatsk.authorization_server.models.AuthorizationRequest;
import com.swetlokognatsk.authorization_server.ports.AuthorizationRequestsRepository;
import com.swetlokognatsk.authorization_server.ports.JsonSerializer;

@Repository
public class RedisAuthorizationRequestsRepository implements AuthorizationRequestsRepository {

    private final StringRedisTemplate redisTemplate;
    private final JsonSerializer serializer;

    // TODO why it's called template?
    public RedisAuthorizationRequestsRepository(final StringRedisTemplate redisTemplate, final JsonSerializer serializer) {
        this.redisTemplate = redisTemplate;
        this.serializer = serializer;
    }

    public void save(final AuthorizationRequest authorizationRequest) {
        var serializedRequest = serializer.serializeAuthorizationRequest(authorizationRequest);
        // TODO why such a weird interface (.opsForValue())?
        redisTemplate.opsForValue().set(authorizationRequest.id(), serializedRequest);
    }

    public AuthorizationRequest findByKey(final String key) throws AuthorizationRequestNotFoundException {
        var serializedRequest = redisTemplate.opsForValue().get(key);
        return serializer.deserializeAuthorizationRequest(serializedRequest);
    }

}
