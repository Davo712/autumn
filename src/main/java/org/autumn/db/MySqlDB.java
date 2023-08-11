package org.autumn.db;

import org.autumn.annotation.db.Column;
import org.autumn.annotation.db.Model;
import org.autumn.annotation.db.Transient;
import org.autumn.annotation.service.AnnotationService;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySqlDB {

    public MySqlDB(AutumnDB autumnDB) {
        this.autumnDB = autumnDB;
    }

    AutumnDB autumnDB;

    void createModels() {
        if (autumnDB.autoCreateModel) {
            Set<Class> classSet;
            if ((autumnDB.modelsPath != null) && (!autumnDB.modelsPath.equals(""))) {
                classSet = AnnotationService.getAnnotatedClasses(Model.class, autumnDB.modelsPath);
            } else {
                classSet = AnnotationService.getAnnotatedClasses(Model.class);
            }
            classSet.forEach(c -> {
                try {
                    StringBuilder sqlQuery = new StringBuilder("CREATE TABLE " + c.getSimpleName().toLowerCase() + "(\n");
                    Field[] fields = c.getDeclaredFields();
                    List<Field> fieldList = Arrays
                            .stream(fields)
                            .filter(field -> !field.isAnnotationPresent(Transient.class))
                            .peek(field -> {
                                if (field.getType() == List.class) {
                                    StringBuilder createListTable = new StringBuilder("CREATE TABLE ");

                                    String input = field.getGenericType().getTypeName();
                                    Pattern pattern = Pattern.compile("\\b(\\w+)\\b>");
                                    Matcher matcher = pattern.matcher(input);

                                    String table1Name = field.getDeclaringClass().getSimpleName();
                                    String table2Name = "";

                                    if (matcher.find()) {
                                        table2Name = matcher.group(1);
                                    }

                                    String tableName = table1Name.toLowerCase() + "_" + table2Name.toLowerCase();
                                    createListTable.append(tableName);
                                    createListTable.append(" ( ");
                                    createListTable.append(table1Name.toLowerCase() + "_id" + " varchar(255), ");
                                    createListTable.append(table2Name.toLowerCase() + "_id" + " varchar(255) ");
                                    createListTable.append(" );");


                                    autumnDB.execute(createListTable.toString());
                                } // check if field is list, and create tables
                            })
                            .filter(field -> field.getType() != List.class)
                            .toList();


                    for (int i = 0; i < fieldList.size(); i++) {
                        String additional = "";
                        if (fieldList.get(i).getAnnotation(Column.class) != null) {
                            additional = " (" + fieldList.get(i).getAnnotation(Column.class).length() + ") ";
                            if (("".equals(fieldList.get(i).getAnnotation(Column.class).length())) || fieldList.get(i).getAnnotation(Column.class) == null) {
                                additional = "";
                            }
                        }


                        String type = autumnDB.getDBTypeSql(fieldList.get(i).getType());
                        String autoIncrement = "";
                        if (autumnDB.dbType.equals("mysql")) {
                            if (fieldList.get(i).getAnnotation(Column.class) != null) {
                                if (fieldList.get(i).getAnnotation(Column.class).autoIncrement()) {
                                    autoIncrement = " AUTO_INCREMENT PRIMARY KEY ";
                                }
                            }
                        }
                        if (fieldList.get(i).isAnnotationPresent(Column.class)) {
                            sqlQuery.append(fieldList.get(i).getAnnotation(Column.class).columnName() + " "
                                    + type + (additional.equals("") ? " (255) " : additional) + autoIncrement
                                    + (fieldList.get(i).getAnnotation(Column.class).notNull() ? " NOT NULL" : "")
                                    + (i == fieldList.size() - 1 ? "\n" : ",\n"));
                        } else {
                            sqlQuery.append(HelperDB.camelToSnake(fieldList.get(i).getName()) + " " + type + (type.equals("varchar") ? " (255) " : " ") + (i == fieldList.size() - 1 ? "\n" : ",\n"));
                        }
                    }
                    sqlQuery.append("\n);");
                    autumnDB.execute(sqlQuery.toString());
                    System.out.println(sqlQuery);
                    System.out.println("Created table " + c.getSimpleName().toLowerCase());
                } catch (Exception e) {
                    if (e.getMessage().contains("already exists")) {
                        List<Map<String, Object>> map = autumnDB.select("SELECT column_name\n" +
                                "FROM INFORMATION_SCHEMA.COLUMNS\n" +
                                "WHERE TABLE_NAME = '" + c.getSimpleName().toLowerCase() + "'");
                        List<String> columnNames = new ArrayList<>();
                        map.forEach(m -> columnNames.add(HelperDB.snakeToCamel(m.get("column_name").toString())));


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
                                        alter.append(HelperDB.camelToSnake(field.getName()) + " " + autumnDB.getDBTypeSql(field.getType()) + (((field.getAnnotation(Column.class) == null) || ("".equals(field.getAnnotation(Column.class).length()))) ? (autumnDB.getDBTypeSql(field.getType()).equals("varchar") ? " (255) " : " ") : " (" + field.getAnnotation(Column.class).length() + ") ") + " ;");
                                        autumnDB.execute(alter.toString());
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

}
