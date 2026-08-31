package com.swetlokognatsk.protected_resource.ports;

import java.util.Set;
import com.swetlokognatsk.protected_resource.AccessTokenNotFoundException;
import com.swetlokognatsk.protected_resource.models.AccessToken;
import com.swetlokognatsk.protected_resource.models.AccessTokenValue;

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
