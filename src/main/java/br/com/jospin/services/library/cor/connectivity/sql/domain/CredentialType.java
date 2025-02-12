package br.com.jospin.services.library.cor.connectivity.sql.domain;

import lombok.Getter;

@Getter
public enum CredentialType {
    H2("org.h2.Driver", "SELECT 1"),
    SQLITE ("org.sqlite.JDBC", "SELECT 1"),
    MYSQL ("com.mysql.jdbc.Driver", "SELECT 1"),
    POSTGRESQL ("org.postegresql.Driver", "SELECT 1"),
    SQLSERVER("org.microsoft.sqlserver.jdbc.SQLServerDriver", "SELECT 1");


    private final String driverName;
    private final String defaultQuery;

    CredentialType(String driverName, String defaultQuery) {
        this.driverName = driverName;
        this.defaultQuery = defaultQuery;
    }

    public static CredentialType getConnetionTypeByDriveName (final String driverName) {
        for (CredentialType t : values()) {
            if(t.getDriverName().equals(driverName)) {
                return t;
            }
        }
        return null;
    }
}
