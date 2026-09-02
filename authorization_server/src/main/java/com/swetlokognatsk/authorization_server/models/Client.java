package com.swetlokognatsk.authorization_server.models;

import java.util.List;
import java.util.Set;

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

    public List<RedirectUri> getRedirectUris() {
        return redirectUris;
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
