package org.autumn.testWebApp;

import org.autumn.annotation.JWT.EnableJWT;
import org.autumn.db.AutumnDB;
import org.autumn.web.DynamicWebApp;

public class Main {
    public static void main(String[] args) {
        new DynamicWebApp().run();

        AutumnDB autumnDB = new AutumnDB(true, true);
        autumnDB.modelsPath = "org.autumn.testWebApp";
        autumnDB.connectToDB("autumndb", "root", "", "mysql");
//        autumnDB.connectToDB("autumnpostgres", "postgres", "root", "postgresql");

        HomeRegister.autumnDB = autumnDB;


    }
}
