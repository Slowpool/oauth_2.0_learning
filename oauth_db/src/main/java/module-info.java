module com.swetlokognatsk.oauth_db {
    requires jakarta.persistence;
    requires spring.boot;

    exports com.swetlokognatsk.oauth_db.models;
    opens com.swetlokognatsk.oauth_db.models;
}