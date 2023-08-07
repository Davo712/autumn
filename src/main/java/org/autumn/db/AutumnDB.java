package org.autumn.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.autumn.annotation.db.Column;
import org.autumn.annotation.db.Model;
import org.autumn.annotation.db.Transient;
import org.autumn.annotation.service.AnnotationService;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Query;
import org.jdbi.v3.core.statement.Update;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.function.IntFunction;

public class AutumnDB {

    public static boolean logSQL = false;
    public static boolean autoCreateModel = false;

    static Handle handle;

    public static String type;

    public static void connectToDB(String dbName, String user, String password, String dbType) {
        type = dbType;
        Jdbi jdbi = Jdbi.create("jdbc:" + dbType + "://localhost/" + dbName, user, password);
        handle = jdbi.open();
        System.out.println("Connected to " + dbName + " DB");

        if (autoCreateModel) {
            Set<Class> classSet = AnnotationService.getAnnotatedClasses(Model.class);
            classSet.forEach(c -> {
                try {
                    StringBuilder sqlQuery = new StringBuilder("CREATE TABLE " + c.getSimpleName().toLowerCase() + "(\n");
                    Field[] fields = c.getDeclaredFields();
                    List<Field> fieldList = Arrays
                            .stream(fields)
                            .filter(field -> !field.isAnnotationPresent(Transient.class))
                            .toList();


                    for (int i = 0; i < fieldList.size(); i++) {


                        String additional = "";
                        if (fieldList.get(i).getAnnotation(Column.class) != null) {
                            additional = " (" + fieldList.get(i).getAnnotation(Column.class).length() + ") ";
                            if (("".equals(fieldList.get(i).getAnnotation(Column.class).length())) || fieldList.get(i).getAnnotation(Column.class) == null) {
                                additional = "";
                            }
                        }
                        String type = getDBTypeSql(fieldList.get(i).getType());
                        if (fieldList.get(i).isAnnotationPresent(Column.class)) {
                            sqlQuery.append(fieldList.get(i).getAnnotation(Column.class).columnName() + " "
                                    + type + additional
                                    + (fieldList.get(i).getAnnotation(Column.class).notNull() ? " NOT NULL" : "")
                                    + (i == fieldList.size() - 1 ? "\n" : ",\n"));
                        } else {
                            sqlQuery.append(fieldList.get(i).getName() + " " + type + (i == fieldList.size() - 1 ? "\n" : ",\n"));
                        }
                    }
                    sqlQuery.append("\n);");
                    execute(sqlQuery.toString());
                    System.out.println("Created table " + c.getSimpleName().toLowerCase());
                } catch (Exception e) {
                    if (e.getMessage().contains("already exists")) {
                        List<Map<String, Object>> map = select("SELECT column_name\n" +
                                "FROM INFORMATION_SCHEMA.COLUMNS\n" +
                                "WHERE TABLE_NAME = '" + c.getSimpleName().toLowerCase() + "'");
                        List<String> columnNames = new ArrayList<>();
                        map.forEach(m -> columnNames.add(m.get("column_name").toString()));


                        Arrays
                                .stream(c.getDeclaredFields())
                                .filter(field -> !field.isAnnotationPresent(Transient.class))
                                .toList()
                                .forEach(field -> {
                                    boolean isContain;
                                    if (field.getAnnotation(Column.class) != null) {
                                        isContain = columnNames.contains((field.getAnnotation(Column.class).columnName()));
                                    } else {
                                        isContain = false;
                                    }
                                    if (!(columnNames.contains(field.getName()) || isContain)) {
                                        StringBuilder alter = new StringBuilder("ALTER TABLE " + c.getSimpleName().toLowerCase() + " ADD ");
                                        alter.append(field.getName() + " " + getDBTypeSql(field.getType()) + " ;");
                                        execute(alter.toString());
                                        System.out.println("Added field in " + c.getSimpleName().toLowerCase() + ", " + field.getName());
                                    }
                                });
                    } else {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    public static Map selectSingle(String nativeQuery) {
        Query query = handle.createQuery(nativeQuery);
        Optional<Map<String, Object>> first = query.mapToMap().findFirst();
        return first.orElse(null);
    }

    public static <T> T selectSingle(String nativeQuery, Class<T> c) {
        Query query = handle.createQuery(nativeQuery);
        Optional<Map<String, Object>> first = query.mapToMap().findFirst();
        ObjectMapper objectMapper = new ObjectMapper();
        return (T) objectMapper.convertValue(first.get(), c);
    }

    public static List select(String nativeQuery) {
        Query query = handle.createQuery(nativeQuery);
        List<Map<String, Object>> results = query.mapToMap().list();
        return results;
    }

    public static <T> List<T> select(String nativeQuery, Class<T> c) {
        Query query = handle.createQuery(nativeQuery);
        List<Map<String, Object>> results = query.mapToMap().list();
        ObjectMapper objectMapper = new ObjectMapper();
        List<T> returned = new ArrayList<>();
        results.forEach(r -> returned.add(objectMapper.convertValue(r, c)));
        return returned;
    }

    public static int save(String nativeQuery) {
        Update update = handle.createUpdate(nativeQuery);
        int rows = update.execute();
        return rows;
    }

    public static <T> String save(T obj) {
        StringBuilder sql1 = new StringBuilder("INSERT INTO " + obj.getClass().getSimpleName().toLowerCase() + " (");
        StringBuilder sql2 = new StringBuilder(" VALUES (");
        Field[] fields = obj.getClass().getDeclaredFields();
        List<Field> fieldList = Arrays
                .stream(fields)
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .toList();
        for (int i = 0; i < fieldList.size(); i++) {
            fieldList.get(i).setAccessible(true);
            if (fieldList.get(i).isAnnotationPresent(Transient.class)) {
                break;
            }
            sql1.append(fieldList.get(i).isAnnotationPresent(Column.class) ? fieldList.get(i).getAnnotation(Column.class).columnName() : fieldList.get(i).getName());
            try {
                if (fieldList.get(i).isAnnotationPresent(Column.class)) {
                    if (fieldList.get(i).getAnnotation(Column.class).autoIncrement()) {
                        System.out.println(handle.execute("SELECT MAX(ID) FROM " + fieldList.get(i).getDeclaringClass().getSimpleName().toLowerCase()));
                    }
                } // TODO
                sql2.append("\'" +fieldList.get(i).get(obj) + "\'");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (i == fieldList.size() - 1) {
                sql1.append(") ");
                sql2.append(");");
            } else {
                sql1.append(",");
                sql2.append(",");
            }
        }
        execute(sql1.toString() + sql2.toString());
        return sql1.toString() + sql2.toString();
    }

    public static int execute(String nativeQuery) {
        return handle.execute(nativeQuery);
    }


    private static String getDBTypeSql(Class c) {
        String type = "";
        if (c == String.class) {
            type = "varchar";
        } else if (c == int.class) {
            type = "int";
        } else if (c == boolean.class) {
            type = "boolean";
        } else if ((c == Date.class) || (c == Timestamp.class) || (c == Instant.class)) {
            type = "date";
        } else if (c == long.class) {
            type = "bigint";
        } else {
            type = "varchar(255)";
        }
        return type;
    }


}

