package com.swetlokognatsk.protected_resource.adapters;

import java.util.HashSet;
import java.util.Set;
import com.swetlokognatsk.protected_resource.AccessTokenNotFoundException;
import com.swetlokognatsk.protected_resource.adapters.hibernate.AccessTokenDao;
import com.swetlokognatsk.protected_resource.adapters.hibernate.WordsDao;
import com.swetlokognatsk.protected_resource.models.AccessToken;
import com.swetlokognatsk.protected_resource.models.AccessTokenValue;
import com.swetlokognatsk.protected_resource.ports.Database;

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

    public void addWord(final String word) {
        wordsDao.addWord(word);
    }

    public void deleteWord(final String word) {
        wordsDao.deleteWord(word);
    }

}
