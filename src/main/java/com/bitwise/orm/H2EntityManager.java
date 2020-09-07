package com.bitwise.orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2EntityManager<T> extends AbstractEntityManager<T> {
    public Connection buildConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:file:C:\\src\\java-start\\reflection\\bw-orm\\db-files\\db",
                "sa", "");
    }
}
