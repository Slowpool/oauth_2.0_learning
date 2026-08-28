package com.swetlokognatsk.protected_resource.models;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "access_tokens")
public final class AccessToken {
    @Id
    public int id;
    
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "access_token", nullable = false))
    public AccessTokenValue value;

    public AccessToken() {
    }

    public AccessToken(final int id, final AccessTokenValue value) {
        this.id = id;
        this.value = value;
    }

}
