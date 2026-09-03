package com.swetlokognatsk.authorization_server.adapters;

import org.springframework.data.redis.core.StringRedisTemplate;

import com.swetlokognatsk.authorization_server.ports.JsonSerializer;

abstract class RedisRepository {

    protected final StringRedisTemplate redisTemplate;
    protected final JsonSerializer serializer;

    // TODO why it's called template?
    public RedisRepository(final StringRedisTemplate redisTemplate, final JsonSerializer serializer) {
        this.redisTemplate = redisTemplate;
        this.serializer = serializer;
    }

}
