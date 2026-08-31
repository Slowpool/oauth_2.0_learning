package com.swetlokognatsk.protected_resource.adapters.hibernate;

import java.util.HashSet;
import java.util.Set;
import com.swetlokognatsk.protected_resource.models.Word;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

public class WordsDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Set<String> getWords() {
        // TODO what this w is about? why not standard `SELECT *`?
        var selectedWords = entityManager.createQuery("SELECT w FROM Word w", Word.class).getResultList();
        var mappedSelectedWords = selectedWords.stream()
            .map((Word selectedWord) -> selectedWord.getWord())
            .toList();
        return new HashSet<>(mappedSelectedWords);
    }

    @Transactional
    public void addWord(final String word) {
        var wordEntity = new Word(word);
        entityManager.persist(wordEntity);
    }

    public void deleteWord(final String word) {

    }
}
