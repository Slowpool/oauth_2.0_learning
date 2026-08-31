package com.swetlokognatsk.protected_resource.models;

import java.util.Set;
import jakarta.persistence.*;

@Entity
@Table(name = "access_tokens")
public final class AccessToken {
    @Id
    private int id;

    @Embedded
    // TODO what's going on here?
    @AttributeOverride(name = "value", column = @Column(name = "access_token", nullable = false))
    private AccessTokenValue value;

    // TODO why there's no session at the moment of accessing it
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(inverseJoinColumns = @JoinColumn(name = "scope_id"))
    private Set<ScopeEntity> scopes;

    public int getId() {
        return id;
    }

    public AccessTokenValue getValue() {
        return value;
    }

    public Set<ScopeEntity> getScopes() {
        return scopes;
    }

    public AccessToken() {
    }

    public AccessToken(final int id, final AccessTokenValue value) {
        this.id = id;
        this.value = value;
    }

}
