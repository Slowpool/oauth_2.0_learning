package com.swetlokognatsk.protected_resource.adapters;

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

}
