package org.autumn.test;

import io.vertx.ext.web.RoutingContext;
import org.autumn.annotation.JWT.EnableJWT;
import org.autumn.annotation.JWT.GetParamJWT;
import org.autumn.annotation.JWT.GetTokenJWT;
import org.autumn.annotation.JWT.NoJWT;
import org.autumn.annotation.web.BodyParam;
import org.autumn.annotation.web.EndPoint;
import org.autumn.annotation.web.Register;
import org.autumn.annotation.web.RequiredParam;
import org.autumn.db.AutumnDB;
import org.autumn.testWebApp.User;
import org.autumn.web.AutumnJWT;
import org.autumn.web.Resp;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @autor Davit Gevorgyan
 */

@Register
// need for only one any class, this annotation is global
public class MainRegister {

    public static AutumnDB autumnDB;


    @EndPoint(mappingPath = "/login", type = "post")
    @NoJWT
    public Resp login(String username, String password) {
        if (true /* user exist**/) {
            Map map = new HashMap();
            map.put("username", username);
            map.put("password", password);
            return Resp.response("OK, your token is: " + AutumnJWT.createJWT(map));
        } else {
            return Resp.response("Bad request");
        }
    }


    @EndPoint(mappingPath = "/paramWA")
    public Resp paramsWithoutAnnotation(String username) {
        System.out.println(username);
        return Resp.response("OK");
    }

    @EndPoint(mappingPath = "/getJWTToken")
    public Resp getJWTToken(@GetTokenJWT String token) {
        System.out.println(AutumnJWT.getParamMap(token));
        System.out.println(AutumnJWT.getParam("username", token));
        return Resp.response("OK");
    }


    @EndPoint(mappingPath = "/getJWTParams")
    public Resp getJWTParams(@GetParamJWT String username, String test, @BodyParam User user, @GetTokenJWT String token) {
        System.out.println(username);
        System.out.println(test);
        System.out.println(user);
        System.out.println(token);
        return Resp.response("OK");
    }

    @EndPoint(mappingPath = "/redirect", redirectPath = "/login")
    @NoJWT
    public void redirect() {
    }

    @EndPoint(mappingPath = "/home", type = "get", needRC = true)
    @NoJWT
    public void home(RoutingContext rc) {
        System.out.println(rc.queryParam("username"));
        rc.response().sendFile("index.html");
    }

    @EndPoint(mappingPath = "/home2", type = "get")
    @NoJWT
    public String home2() {
        return "index";
    }

    @EndPoint(mappingPath = "/dbTest", type = "get")
    @NoJWT
    public Resp dbTest() {
//        System.out.println(AutumnDB.selectSingle("select * from usr limit 1", Usr.class)); --> return Usr object
//        System.out.println(AutumnDB.selectSingle("select * from usr limit 1"));  -- > return map
//        autumnDB.select("select * from usr", Usr.class).forEach(System.out::println);
//        autumnDB.execute("create table project (name varchar(50))");
//        User2 user2 = new User2();
//        user2.setAge(1);
//        user2.setName("test");
//        autumnDB.save(user2);
//        System.out.println(autumnDB.selectSingle("SELECT MAX(id), MIN(id)" +
//                "FROM user2;"));

        return Resp.response("OK");
    }

}