package com.swetlokognatsk.protected_resource.adapters;

import com.swetlokognatsk.protected_resource.AccessToken;
import com.swetlokognatsk.protected_resource.AccessTokenNotFoundException;
import com.swetlokognatsk.protected_resource.ports.Database;

public final class FileDatabase implements Database {

    public static final String DB_DIR = "/Java/oauth_2_0_learning/file_db";

    public void saveAccessToken(final AccessToken accessToken) {

    }

    public AccessToken findAccessToken(final String accessToken) throws AccessTokenNotFoundException {

    }
}
