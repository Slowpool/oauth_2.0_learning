package com.swetlokognatsk.protected_resource.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "words")
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String word;

    public int getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public Word() {

    }

    public Word(final String word) {
        this.word = word;
    }
}
