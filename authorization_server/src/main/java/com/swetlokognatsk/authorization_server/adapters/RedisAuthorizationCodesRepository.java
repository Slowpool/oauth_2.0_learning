package com.swetlokognatsk.authorization_server.adapters;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import com.swetlokognatsk.authorization_server.exceptions.AuthorizationCodeNotFoundException;
import com.swetlokognatsk.authorization_server.models.AuthorizationCode;
import com.swetlokognatsk.authorization_server.ports.AuthorizationCodesRepository;
import com.swetlokognatsk.authorization_server.ports.JsonSerializer;

@Repository
public class RedisAuthorizationCodesRepository extends RedisRepository implements AuthorizationCodesRepository {

    public RedisAuthorizationCodesRepository(final StringRedisTemplate redisTemplate, final JsonSerializer serializer) {
        super(redisTemplate, serializer);
    }

    public void save(final AuthorizationCode authorizationCode) {
        var serializedCode = serializer.serializeAuthorizationCode(authorizationCode);
        var key = buildKey(authorizationCode);
        redisTemplate.opsForValue().set(key, serializedCode);
    }

    public AuthorizationCode findByCode(final String authorizationCode) throws AuthorizationCodeNotFoundException {
        var key = buildKey(authorizationCode);
        var serializedAuthorizationCode = redisTemplate.opsForValue().get(key);
        if (serializedAuthorizationCode == null) {
            throw new AuthorizationCodeNotFoundException();
        }
        return serializer.deserializeAuthorizationCode(serializedAuthorizationCode);
    }

    // well, let this dry violation be. (popByCode == findByCode.replace("get", "getAndDelete"))
    public AuthorizationCode popByCode(final String authorizationCode) throws AuthorizationCodeNotFoundException {
        var key = buildKey(authorizationCode);
        var serializedAuthorizationCode = redisTemplate.opsForValue().getAndDelete(key);
        if (serializedAuthorizationCode == null) {
            throw new AuthorizationCodeNotFoundException();
        }
        return serializer.deserializeAuthorizationCode(serializedAuthorizationCode);
    }

    private static String buildKey(final AuthorizationCode authorizationCode) {
        return buildKey(authorizationCode.code());
    }

    private static String buildKey(final String code) {
        return "authorizationCode:%s".formatted(code);
    }
}
