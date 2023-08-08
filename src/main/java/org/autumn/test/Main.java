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

        AutumnDB autumnDB = new AutumnDB(false, true);
        autumnDB.modelsPath = "org.autumn.test";
        autumnDB.connectToDB("autumnpostgres", "postgres", "root", "postgresql");
        MainRegister.autumnDB = autumnDB;

        AutumnDB autumnDB2 = new AutumnDB(false, true);
        autumnDB.modelsPath = "org.autumn.test2";
        autumnDB.connectToDB("autumndb", "root", "", "mysql");
        TestRegister.autumnDB = autumnDB2;


    }
}

