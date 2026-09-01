package com.swetlokognatsk.oauth_db.models;

import jakarta.persistence.*;

@Entity
@Table(name = "scopes")
public class ScopeEntity {
    @Id
    private int id;

    @Column(name = "name")
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ScopeEntity() {
    }

    public ScopeEntity(final int id, final String name) {
        this.id = id;
        this.name = name;
    }
}
