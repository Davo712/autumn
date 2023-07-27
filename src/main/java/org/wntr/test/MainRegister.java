package org.wntr.test;

import io.vertx.ext.web.RoutingContext;
import org.wntr.annotation.EndPoint;
import org.wntr.annotation.Register;
import org.wntr.annotation.RequiredParam;

@Register
public class MainRegister {


    @EndPoint(mappingPath = "/home/test", type = "get", needRC = true)
    public void test(RoutingContext rc) {
        System.out.println(rc.queryParam("username"));
        rc.response().end("test success");
    }

    @EndPoint(mappingPath = "/get")
    public User get(@RequiredParam String username, @RequiredParam String age) {
        User user = new User();
        user.setUsername(username);
        user.setAge(Integer.parseInt(age));
        return user;
    }

    @EndPoint(mappingPath = "/get2")
    public String get2(String username, String age) {
        return "get2";
    }

    @EndPoint(mappingPath = "/redirect", redirectPath = "/get2")
    public void redirect() {
    }

}