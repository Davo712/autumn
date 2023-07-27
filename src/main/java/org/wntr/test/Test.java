package org.wntr.test;

import io.vertx.core.http.HttpConnection;
import io.vertx.ext.web.RoutingContext;
import org.wntr.annotation.EndPoint;
import org.wntr.annotation.Register;
import org.wntr.annotation.RequiredParam;

@Register
public class Test {

    @EndPoint(mappingPath = "/home/test", type = "get", needRC = true)
    public void test(RoutingContext rc) {
        System.out.println(rc.queryParam("username"));
        rc.response().end("sdssdsd");
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
        return "S";
    }
}