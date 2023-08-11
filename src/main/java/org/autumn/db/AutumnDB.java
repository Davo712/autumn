package org.autumn.db;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.vertx.core.impl.HAManager;
import org.autumn.annotation.db.Column;
import org.autumn.annotation.db.Id;
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
import java.util.stream.Collectors;

public class AutumnDB {

    public boolean logSQL;
    public boolean autoCreateModel;
    static Handle handle;
    public String dbType;

    public String modelsPath;

    public AutumnDB(boolean logSQL, boolean autoCreateModel) {
        this.logSQL = logSQL;
        this.autoCreateModel = autoCreateModel;
    }

    public void connectToDB(String dbName, String user, String password, String dbType) {
        this.dbType = dbType;
        Jdbi jdbi = Jdbi.create("jdbc:" + dbType + "://localhost/" + dbName, user, password);
        handle = jdbi.open();
        System.out.println("Connected to " + dbName + " DB");
        if (dbType.equals("mysql")) {
            new MySqlDB(this).createModels();
        } else if (dbType.equals("postgresql")) {
            new PostgreSqlDB(this).createModels();
        }
    }

    public Map selectSingle(String nativeQuery) {
        Query query = handle.createQuery(nativeQuery);
        Optional<Map<String, Object>> first = query.mapToMap().findFirst();
        if (logSQL) {
            System.out.println(nativeQuery);
        }
        return first.orElse(null);
    }

    public <T> T selectSingle(String nativeQuery, Class<T> c) {
        Query query = handle.createQuery(nativeQuery);
        Optional<Map<String, Object>> first = query.mapToMap().findFirst();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<String> modifiedKeys = new ArrayList<>();
        for (String key : first.get().keySet()) {
            modifiedKeys.add(key);
        }
        for (String oldKey : modifiedKeys) {
            String newKey = HelperDB.snakeToCamel(oldKey);
            Object value = first.get().remove(oldKey);
            first.get().put(newKey, value);
        }

        if (logSQL) {
            System.out.println(nativeQuery);
        }
        return (T) objectMapper.convertValue(first.get(), c);
    }

    public List select(String nativeQuery) {
        Query query = handle.createQuery(nativeQuery);
        List<Map<String, Object>> results = query.mapToMap().list();
        if (logSQL) {
            System.out.println(nativeQuery);
        }
        return results;
    }

    public <T> List<T> select(String nativeQuery, Class<T> c) {
        Query query = handle.createQuery(nativeQuery);
        List<Map<String, Object>> results = query.mapToMap().list();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<T> returned = new ArrayList<>();
        results.forEach(r -> {
            List<String> modifiedKeys = new ArrayList<>();
            for (String key : r.keySet()) {
                modifiedKeys.add(key);
            }
            for (String oldKey : modifiedKeys) {
                String newKey = HelperDB.snakeToCamel(oldKey);
                Object value = r.remove(oldKey);
                r.put(newKey, value);
            }
            returned.add(objectMapper.convertValue(r, c));
        });
        if (logSQL) {
            System.out.println(nativeQuery);
        }
        return returned;
    }

    public int save(String nativeQuery) {
        Update update = handle.createUpdate(nativeQuery);
        int rows = update.execute();
        if (logSQL) {
            System.out.println(nativeQuery);
        }
        return rows;
    }

    public <T> void save(T obj) {
        StringBuilder sql1 = new StringBuilder("INSERT INTO " + obj.getClass().getSimpleName().toLowerCase() + " (");
        StringBuilder sql2 = new StringBuilder(" VALUES (");
        Field[] fields = obj.getClass().getDeclaredFields();
        List<Field> fieldList = Arrays
                .stream(fields)
                .filter(field -> !(field.isAnnotationPresent(Transient.class) || (field.isAnnotationPresent(Id.class))))
                .toList();

        for (int i = 0; i < fieldList.size(); i++) {
            fieldList.get(i).setAccessible(true);
            if (fieldList.get(i).isAnnotationPresent(Transient.class)) {
                break;
            }
            sql1.append(fieldList.get(i).isAnnotationPresent(Column.class) ? HelperDB.camelToSnake(fieldList.get(i).getAnnotation(Column.class).columnName()) : HelperDB.camelToSnake(fieldList.get(i).getName()));
            try {
                if ((fieldList.get(i).getType() == boolean.class) || (fieldList.get(i).getType() == Boolean.class)) {
                    sql2.append(fieldList.get(i).get(obj));
                } else {
                    sql2.append("\'" + fieldList.get(i).get(obj) + "\'");
                }
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
    }

    public int execute(String nativeQuery) {
        if (logSQL) {
            System.out.println(nativeQuery);
        }
        return handle.execute(nativeQuery);
    }


    String getDBTypeSql(Class c) {
        String type = "";
        if (c == String.class) {
            type = "varchar";
        } else if ((c == int.class) || (c == Integer.class)) {
            type = "int";
        } else if ((c == boolean.class) || (c == Boolean.class)) {
            type = "boolean";
        } else if (c == Date.class) {
            type = "date";
        } else if (c == Timestamp.class) {
            type = "timestamp";
        } else if ((c == long.class) || (c == Long.class)) {
            type = "bigint";
        } else {
            type = "varchar";
        }
        return type;
    }
}

