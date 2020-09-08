package com.bitwise.orm;

import java.sql.SQLException;

public interface EntityManager<T> {

    void persist(T t) throws SQLException, IllegalAccessException;

    T find(Class<T> clazz, Object primaryKey) throws SQLException;
}
