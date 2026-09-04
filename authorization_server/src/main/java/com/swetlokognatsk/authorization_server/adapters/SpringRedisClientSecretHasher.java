package com.swetlokognatsk.authorization_server.adapters;

import org.springframework.data.redis.core.script.DigestUtils;
import org.springframework.stereotype.Component;
import com.swetlokognatsk.authorization_server.ports.ClientSecretHasher;

@Component
public class SpringRedisClientSecretHasher implements ClientSecretHasher {

    public String hash(final String clientSecret) {
        // funny workaround. this func generates 40chars hash, whereas db format of password_hash column is char(128)
        return DigestUtils.sha1DigestAsHex(clientSecret).repeat(3) + "88888888";
    }
}
