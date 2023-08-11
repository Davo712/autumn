package org.autumn.db;

public class HelperDB {

    public static String camelToSnake(String input) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            if (Character.isUpperCase(currentChar)) {
                output.append('_').append(Character.toLowerCase(currentChar));
            } else {
                output.append(currentChar);
            }
        }

        return output.toString();
    }

    public static String snakeToCamel(String input) {
        StringBuilder output = new StringBuilder();
        boolean capitalizeNext = false;

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            if (currentChar == '_') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    output.append(Character.toUpperCase(currentChar));
                    capitalizeNext = false;
                } else {
                    output.append(currentChar);
                }
            }
        }

        return output.toString();
    }

}
