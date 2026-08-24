package com.swetlokognatsk.client.infrastructure;

import com.swetlokognatsk.client.model.RefreshToken;

public final class Database {

    private static RefreshToken refreshToken = null;

    public static void saveRefreshToken(final RefreshToken refreshToken) {
        Database.refreshToken = refreshToken;
    }

    public static RefreshToken getRefreshToken() {
        return refreshToken;
    }
}
