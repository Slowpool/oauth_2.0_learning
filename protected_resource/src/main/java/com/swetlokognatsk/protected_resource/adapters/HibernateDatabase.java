package com.swetlokognatsk.protected_resource.adapters;

import java.util.HashSet;
import java.util.Set;
import com.swetlokognatsk.protected_resource.AccessTokenNotFoundException;
import com.swetlokognatsk.protected_resource.adapters.jakarta.AccessTokenDao;
import com.swetlokognatsk.protected_resource.adapters.jakarta.WordsDao;
import com.swetlokognatsk.protected_resource.models.AccessToken;
import com.swetlokognatsk.protected_resource.models.AccessTokenValue;
import com.swetlokognatsk.protected_resource.ports.Database;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

// TODO HibernateDatabase
public final class HibernateDatabase implements Database {

    private final AccessTokenDao accessTokenDao;
    private final WordsDao wordsDao;

    public HibernateDatabase(final AccessTokenDao accessTokenDao, final WordsDao wordsDao) {
        this.accessTokenDao = accessTokenDao;
        this.wordsDao = wordsDao;
    }

    public AccessToken findAccessToken(final AccessTokenValue accessTokenValue) throws AccessTokenNotFoundException {
        return accessTokenDao.findByValue(accessTokenValue);
    }

    public void saveAccessToken(final AccessToken accessToken) {

    }

    public void deleteAccessToken(final AccessTokenValue accessTokenValue) {

    }

    public void deleteAll() {

    }

    public Set<String> getWords() {
        return wordsDao.getWords();
    }

    public void addWord(final String word) throws EntityExistsException {
        wordsDao.addWord(word);
    }

    public void removeWord(final String word) throws EntityNotFoundException {
        wordsDao.removeWord(word);
    }

}
