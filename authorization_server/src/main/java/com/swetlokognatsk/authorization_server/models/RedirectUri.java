package com.swetlokognatsk.authorization_server.models;

import jakarta.persistence.*;

@Entity
@Table(name = "redirect_uris")
public class RedirectUri {

    @Id
    public int id;

    @Column(name = "client_id")
    public int clientId;

    public String uri;

    public RedirectUri() {
    }
}
