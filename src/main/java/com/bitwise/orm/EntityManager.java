package com.bitwise.orm;

import java.sql.SQLException;

public interface EntityManager<T> {
    static <T> EntityManager<T> of (Class<T> clazz) {
        return new H2EntityManager<>();
    }
    void persist(T t) throws SQLException, IllegalAccessException;

    T find(Class<T> clazz, Object primaryKey) throws SQLException;
}
