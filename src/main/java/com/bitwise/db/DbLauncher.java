package com.bitwise.db;

import org.h2.tools.Server;

import java.sql.SQLException;

public class DbLauncher {
    public static void main(String[] args) throws SQLException {
        Server.main();
        System.out.println("DB launched");
    }
}
