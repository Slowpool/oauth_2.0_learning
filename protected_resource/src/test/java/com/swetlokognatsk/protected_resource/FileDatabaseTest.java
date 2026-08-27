package com.swetlokognatsk.protected_resource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.*;
import org.junit.jupiter.api.*;

import com.swetlokognatsk.protected_resource.adapters.FileDatabase;

public final class FileDatabaseTest {

    private static String SOME_ACCESS_TOKEN = "some_access_token";
    private FileDatabase database;
    
    @BeforeAll
    public void setup() {
        database = new FileDatabase();
    }

    @BeforeEach
    public void prepareTest() {
        // of course it's bad idea to do the stuff which actually must be tested but well
        database.deleteAll();
    }

    private File getAccessTokenFile(final AccessToken accessToken) {
        return new File(FileDatabase.DB_DIR + "/" + accessToken.value());
    }
    
    @Test
    public void save() {
        var accessToken = new AccessToken(SOME_ACCESS_TOKEN);

        var expectedNewFile = getAccessTokenFile(accessToken);
        // ensuring the required fixture is clean
        assertFalse(expectedNewFile.exists());

        database.saveAccessToken(accessToken);

        assertTrue(expectedNewFile.exists());
    }
    
    @Test
    public void delete() {
        save();

        var accessToken = new AccessToken(SOME_ACCESS_TOKEN);
        database.deleteAccessToken(accessToken);

        getAccessTokenFile(accessToken);
        var expectedDeletedFile = getAccessTokenFile(accessToken);
        
        assertFalse(expectedDeletedFile.exists());
    }
    
    @Test
    public void findExistingAccessToken() {
        save();
        
    }
    
    @Test
    public void findAbsentAccessToken() {

    }

}
