package org.wntr.test;

import io.vertx.ext.web.RoutingContext;
import org.wntr.annotation.JWT.EnableJWT;
import org.wntr.annotation.JWT.GetParamJWT;
import org.wntr.annotation.JWT.GetTokenJWT;
import org.wntr.annotation.JWT.NoJWT;
import org.wntr.annotation.web.EndPoint;
import org.wntr.annotation.web.Register;
import org.wntr.annotation.web.RequiredParam;
import org.wntr.web.AutumnJWT;
import org.wntr.web.Resp;

import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @autor Davit Gevorgyan
 */

@Register
@EnableJWT(secretKey = "MySecretKey")
public class MainRegister {


    @EndPoint(mappingPath = "/login", type = "post")
    @NoJWT
    public Resp login(@RequiredParam String username, @RequiredParam String password) {
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
    public Resp getJWTParams(@GetParamJWT String username) {
        System.out.println(username);
        return Resp.response("OK");
    }

    @EndPoint(mappingPath = "/redirect", redirectPath = "/login")
    public void redirect() {
    }

    @EndPoint(mappingPath = "/home", type = "get", needRC = true)
    public void home(RoutingContext rc) {
        System.out.println(rc.queryParam("username"));
        rc.response().end("Home");
    }

}