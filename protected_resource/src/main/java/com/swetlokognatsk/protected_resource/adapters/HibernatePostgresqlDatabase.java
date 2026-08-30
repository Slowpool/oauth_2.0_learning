package com.swetlokognatsk.protected_resource.adapters;

import java.util.HashSet;
import java.util.Set;

import com.swetlokognatsk.protected_resource.AccessTokenNotFoundException;
import com.swetlokognatsk.protected_resource.adapters.hibernate.AccessTokenDao;
import com.swetlokognatsk.protected_resource.models.AccessToken;
import com.swetlokognatsk.protected_resource.models.AccessTokenValue;
import com.swetlokognatsk.protected_resource.ports.Database;

// TODO HibernatePostgresqlDatabase
public final class HibernatePostgresqlDatabase implements Database {

    private final AccessTokenDao accessTokenDao;
    
    public HibernatePostgresqlDatabase(final AccessTokenDao accessTokenDao) {
        this.accessTokenDao = accessTokenDao;
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
        return new HashSet<>();
        // return wordsDao.getWords();
    }

    public void addWord(String word) {

    }

    public void deleteWord(String word) {

    }

}
