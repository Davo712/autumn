package org.autumn.test;

import org.autumn.db.AutumnDB;
import org.autumn.web.DynamicWebApp;

/**
 * @version 1.0
 * @autor Davit Gevorgyan
 */

public class Main {
    public static void main(String[] args) {
        DynamicWebApp.setParams(8080, "localhost");
        DynamicWebApp.run();
        AutumnDB.autoCreateModel = true;
        AutumnDB.connectToDB("autumnpostgres", "postgres", "root", "postgresql");
    }
}

