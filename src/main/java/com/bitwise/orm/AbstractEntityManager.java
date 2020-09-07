package com.bitwise.orm;

import com.bitwise.util.MetaModel;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractEntityManager<T> implements EntityManager<T> {

    private final AtomicLong idGenerator = new AtomicLong(0L);

    @Override
    public void persist(T t) throws SQLException, IllegalAccessException {
        MetaModel metaModel = MetaModel.of(t.getClass());
        String sql = metaModel.buildInsertRequest();
        System.out.println(sql);
        PreparedStatement statement = prepareStatementWith(sql).andParameters(t);

        statement.executeUpdate();

    }

    @Override
    public T find(Class<T> clazz, Object primaryKey) throws SQLException {
        MetaModel metaModel = MetaModel.of(clazz);
        String sql = metaModel.buildSelectSql();
        PreparedStatement statement = prepareStatementWith(sql).andPrimaryKey(primaryKey);
        ResultSet resultSet = statement.executeQuery();

        return buildInstance(clazz, resultSet);
    }

    @SneakyThrows
    private T buildInstance(Class<T> clazz, ResultSet resultSet) {
        MetaModel metaModel = MetaModel.of(clazz);

        T t = clazz.getConstructor().newInstance();

        Field primaryKeyField = metaModel.getPrimaryKeyField().getField();
        String primaryKeyColumnName = metaModel.getPrimaryKeyField().getName();
        Class<?> primaryKeyType = metaModel.getPrimaryKeyField().getType();

        resultSet.next();

        if (primaryKeyType == long.class) {
            long primaryKeyValue = resultSet.getInt(primaryKeyColumnName);
            primaryKeyField.setAccessible(true);
            primaryKeyField.set(t, primaryKeyValue);
        }

        for (var columnField: metaModel.getColumns()) {
            Field field = columnField.getField();
            field.setAccessible(true);
            String columnName = columnField.getName();
            Class<?> columnType = columnField.getType();

            if (columnType == int.class) {
                int value = resultSet.getInt(columnName);
                field.set(t, value);
            }

            if (columnType == String.class) {
                String value = resultSet.getString(columnName);
                field.set(t, value);
            }
        }

        return t;

    }

    private PreparedStatementWrapper prepareStatementWith(String sql) throws SQLException {
        Connection connection = buildConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        return new PreparedStatementWrapper(statement);
    }

    public abstract Connection buildConnection() throws SQLException;

    private class PreparedStatementWrapper {

        private PreparedStatement statement;

        public PreparedStatementWrapper(PreparedStatement statement) {
            this.statement = statement;
        }

        public PreparedStatement andParameters(T t) throws SQLException, IllegalAccessException {

            int statementIndex = 0;

            MetaModel model = MetaModel.of(t.getClass());

            var primaryKeyField = model.getPrimaryKeyField();

            var generatedId = idGenerator.incrementAndGet();

            if (primaryKeyField.getType() == long.class) {
                statement.setLong(++statementIndex, generatedId);

                // set id
                var field = primaryKeyField.getField();
                field.setAccessible(true);
                field.set(t, generatedId);
            }

            var columns = model.getColumns();
            for (var column: columns) {
                Field field = column.getField();
                field.setAccessible(true);

                if (column.getType() == int.class) {
                    statement.setInt(++statementIndex, (int)field.get(t));
                }

                if (column.getType() == String.class) {
                    statement.setString(++statementIndex, (String) field.get(t));
                }
            }

            return statement;
        }

        public PreparedStatement andPrimaryKey(Object primaryKey) throws SQLException {
            if (primaryKey.getClass() == Long.class) {
                statement.setLong(1, (Long) primaryKey);
            }

            return statement;
        }
    }
}
