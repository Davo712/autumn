package org.autumn.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.autumn.annotation.db.Model;
import org.autumn.annotation.service.AnnotationService;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Query;
import org.jdbi.v3.core.statement.Update;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

public class AutumnDB {

    public static boolean logSQL = false;
    public static boolean autoCreateModel = false;

    static Handle handle;

    public static void connectToDB(String dbName, String user, String password) {
        Jdbi jdbi = Jdbi.create("jdbc:mysql://localhost/" + dbName, user, password);
        handle = jdbi.open();
        System.out.println("Connected to " + dbName + " DB");

        if (autoCreateModel) {
            Set<Class> classSet = AnnotationService.getAnnotatedClasses(Model.class);
            classSet.forEach(c -> {
                try {
                    StringBuilder sqlQuery = new StringBuilder("CREATE TABLE " + c.getSimpleName().toLowerCase() + "(\n");
                    Field[] fields = c.getDeclaredFields();
                    for (int i = 0; i < fields.length; i++) {
                        String type = getDBTypeMySql(fields[i].getType());

                        sqlQuery.append(fields[i].getName() + " " + type + (i == fields.length - 1 ? "\n" : ",\n"));
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

                        Arrays.stream(c.getDeclaredFields()).forEach(field -> {
                            if (!columnNames.contains(field.getName())) {
                                StringBuilder alter = new StringBuilder("ALTER TABLE " + c.getSimpleName().toLowerCase() + " ADD ");
                                alter.append(field.getName() + " " + getDBTypeMySql(field.getType()) + " ;");
                                execute(alter.toString());
                                System.out.println("Added field in " + c.getSimpleName().toLowerCase() + ", " + field.getName());
                            }
                        });
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

    public static int execute(String nativeQuery) {
        return handle.execute(nativeQuery);
    }


    private static String getDBTypeMySql(Class c) {
        String type = "";
        if (c == String.class) {
            type = "varchar(255)";
        } else if (c == int.class) {
            type = "int";
        } else if (c == boolean.class) {
            type = "boolean";
        } else if ((c == Date.class) || (c == Timestamp.class) || (c == Instant.class)) {
            type = "date";
        }
        return type;
    }


}
