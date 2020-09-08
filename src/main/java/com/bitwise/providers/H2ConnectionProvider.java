package com.bitwise.providers;

import com.bitwise.annotations.Provides;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2ConnectionProvider {

    @Provides
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:file:C:\\src\\java-start\\reflection\\bw-orm\\db-files\\db",
                "sa", "");
    }
}
