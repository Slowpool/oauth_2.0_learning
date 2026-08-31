package com.swetlokognatsk.protected_resource.adapters.hibernate;

import java.util.HashSet;
import java.util.Set;
import com.swetlokognatsk.protected_resource.models.Word;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.transaction.annotation.Transactional;

public class WordsDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Set<String> getWords() {
        // TODO what this w is about? why not standard `SELECT *`?
        var selectedWords = entityManager.createQuery("SELECT w FROM Word w", Word.class).getResultList();
        var mappedSelectedWords = selectedWords.stream().map((Word selectedWord) -> selectedWord.getWord()).toList();
        return new HashSet<>(mappedSelectedWords);
    }

    @Transactional
    public void addWord(final String word) throws EntityExistsException {
        var wordEntity = new Word(word);
        try {
            entityManager.persist(wordEntity);
        }
        catch (ConstraintViolationException e) {
            throw new EntityExistsException();
        }
    }

    @Transactional
    public void removeWord(final String word) throws EntityNotFoundException {
        var criteriaBuilder = entityManager.getCriteriaBuilder();

        var deleteQuery = criteriaBuilder.createCriteriaDelete(Word.class);
        var root = deleteQuery.from(Word.class);

        var valuePredicate = criteriaBuilder.equal(root.get("word"), word);

        deleteQuery.where(valuePredicate);
        var removedEntitiesNumber = entityManager.createQuery(deleteQuery).executeUpdate();

        if (removedEntitiesNumber == 0) {
            throw new EntityNotFoundException();
        }
    }
}
