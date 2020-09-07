package com.bitwise.util;

import com.bitwise.annotations.Column;
import com.bitwise.annotations.PrimaryKey;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MetaModel {

    private final Class<?> clazz;

    public static MetaModel of(Class<?> clazz) {
        return new MetaModel(clazz);
    }

    public MetaModel(Class<?> clazz) {
        this.clazz = clazz;
    }

    public PrimaryKeyField getPrimaryKeyField() {

        var fields = this.clazz.getDeclaredFields();
        for (var field: fields) {

            var primaryKey = field.getAnnotation(PrimaryKey.class);
            if ( primaryKey != null) {
                return new PrimaryKeyField(field);
            }
        }
        return null;
    }

    public List<ColumnField> getColumns() {
        return
        Arrays.stream(this.clazz.getDeclaredFields())
                .filter(f -> f.getAnnotation(Column.class) != null)
                .map(ColumnField::new)
                .collect(Collectors.toList());
    }

    public String buildInsertRequest() {
        // insert into Person (id, name, age) value (?, ? ,?)

        String columnNameFragment = buildColumnNamesTextFragment();

        String valueFragment = buildValueTextFragment();

        return "insert into " +
                this.clazz.getSimpleName() +
                " (" + columnNameFragment + ") values (" + valueFragment +")";
    }

    public String buildSelectSql() {
        // select id, name, age from Person

        String columnNameFragment = buildColumnNamesTextFragment();

        return "select " + columnNameFragment +
                " from " + this.clazz.getSimpleName() +
                " where " + getPrimaryKeyField().getName() + " = ?";
    }

    private String buildValueTextFragment() {
        return IntStream.range(0, getColumns().size() + 1)
                                        .mapToObj(index -> "?")
                                        .collect(Collectors.joining(", "));
    }

    private String buildColumnNamesTextFragment() {
        String primaryKeyColumnName = getPrimaryKeyField().getName();
        List<String> columnNames = getColumns().stream().map(ColumnField::getName).collect(Collectors.toList());

        columnNames.add(0, primaryKeyColumnName);

        return String.join(",", columnNames);
    }


}
