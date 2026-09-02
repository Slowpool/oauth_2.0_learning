package com.swetlokognatsk.protected_resource.ports;

import java.util.Set;

import com.swetlokognatsk.oauth_db.models.AccessToken;
import com.swetlokognatsk.oauth_db.models.AccessTokenValue;
import com.swetlokognatsk.oauth_db.AccessTokenNotFoundException;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

public interface Database {

    AccessToken findAccessToken(AccessTokenValue accessTokenValue) throws AccessTokenNotFoundException;

    void saveAccessToken(AccessToken accessToken);

    void deleteAccessToken(AccessTokenValue accessTokenValue);

    void deleteAll();

    Set<String> getWords();

    void addWord(String word) throws EntityExistsException;

    void removeWord(String word) throws EntityNotFoundException;
}
