package com.swetlokognatsk.authorization_server.adapters;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import com.swetlokognatsk.authorization_server.exceptions.AuthorizationRequestNotFoundException;
import com.swetlokognatsk.authorization_server.models.AuthorizationCode;
import com.swetlokognatsk.authorization_server.models.AuthorizationRequest;
import com.swetlokognatsk.authorization_server.ports.AuthorizationRequestsRepository;
import com.swetlokognatsk.authorization_server.ports.JsonSerializer;

@Repository
public class RedisAuthorizationRequestsRepository extends RedisRepository implements AuthorizationRequestsRepository {

    public RedisAuthorizationRequestsRepository(final StringRedisTemplate redisTemplate, final JsonSerializer serializer) {
        super(redisTemplate, serializer);
    }

    public void save(final AuthorizationRequest authorizationRequest) {
        var serializedRequest = serializer.serializeAuthorizationRequest(authorizationRequest);
        // TODO why such a weird interface (.opsForValue())?
        var key = buildKey(authorizationRequest);
        redisTemplate.opsForValue().set(key, serializedRequest);
    }

    public AuthorizationRequest findByRequestId(final String requestId) throws AuthorizationRequestNotFoundException {
        var key = buildKey(requestId);
        var serializedRequest = redisTemplate.opsForValue().get(key);
        if (serializedRequest == null) {
            throw new AuthorizationRequestNotFoundException();
        }
        return serializer.deserializeAuthorizationRequest(serializedRequest);
    }

    public AuthorizationRequest popByRequestId(final String requestId) throws AuthorizationRequestNotFoundException {
        var key = buildKey(requestId);
        var serializedRequest = redisTemplate.opsForValue().getAndDelete(key);
        if (serializedRequest == null) {
            throw new AuthorizationRequestNotFoundException();
        }
        return serializer.deserializeAuthorizationRequest(serializedRequest);
    }

    private static String buildKey(final AuthorizationRequest authorizationRequest) {
        return buildKey(authorizationRequest.id());
    }

    private static String buildKey(final String requestId) {
        return "authorizationRequest:%s".formatted(requestId);
    }
}
