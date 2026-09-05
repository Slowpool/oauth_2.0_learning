package com.swetlokognatsk.protected_resource.adapters;

import java.util.HashSet;
import java.util.Set;
import com.swetlokognatsk.oauth_db.models.AccessToken;
import com.swetlokognatsk.oauth_db.models.AccessTokenValue;
import com.swetlokognatsk.oauth_db.AccessTokenNotFoundException;
import com.swetlokognatsk.oauth_db.daos.AccessTokensDao;
import com.swetlokognatsk.protected_resource.adapters.jakarta.WordsDao;
import com.swetlokognatsk.protected_resource.ports.Database;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

// TODO DaoDatabase
public final class DaoDatabase implements Database {

    private final AccessTokensDao AccessTokensDao;
    private final WordsDao wordsDao;

    public DaoDatabase(final AccessTokensDao AccessTokensDao, final WordsDao wordsDao) {
        this.AccessTokensDao = AccessTokensDao;
        this.wordsDao = wordsDao;
    }

    public AccessToken findAccessToken(final AccessTokenValue accessTokenValue) throws AccessTokenNotFoundException {
        return AccessTokensDao.findByValue(accessTokenValue);
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
