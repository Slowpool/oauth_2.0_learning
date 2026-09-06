package com.swetlokognatsk.authorization_server.models;

import java.util.List;
import java.util.Set;
import com.swetlokognatsk.oauth_db.models.ScopeEntity;
import com.swetlokognatsk.oauth_db.models.Scopes;
import jakarta.persistence.*;

@Entity
@Table(name = "clients")
public class Client {
    // TODO add to db "CLIENT_ID_1", "CLIENT_SECRET_1", scopes= { "foo", "bar" }

    @Id
    int id;

    @Column(name = "client_id")
    String clientId;

    @Column(name = "password_hash")
    String secretHash;

    @OneToMany
    @JoinColumn(name = "client_id")
    List<RedirectUri> redirectUris;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(inverseJoinColumns = @JoinColumn(name = "scope_id"))
    Set<ScopeEntity> scopes;

    public int getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public String getSecretHash() {
        return secretHash;
    }

    public List<RedirectUri> getRedirectUris() {
        return redirectUris;
    }

    public Set<ScopeEntity> getScopes() {
        return scopes;
    }

    public Client() {
    }

    // Client(final String clientId, final String clientSecret, final String[] scopes, final String redirectURI) {
    //     this.clientId = clientId;
    //     this.clientSecret = clientSecret;
    //     this.scopes = scopes;
    //     this.redirectURI = redirectURI;
    // }
}
