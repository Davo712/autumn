package org.autumn.testWebApp;

import org.autumn.annotation.web.BodyParam;
import org.autumn.annotation.web.EndPoint;
import org.autumn.annotation.web.Register;
import org.autumn.db.AutumnDB;
import org.autumn.web.Resp;

import java.util.List;

@Register
public class HomeRegister {

    public static AutumnDB autumnDB;

    @EndPoint(mappingPath = "/home")
    public String home() {
        return "index";
    }

    @EndPoint(mappingPath = "/addUser", type = "post")
    public Resp addUser(@BodyParam Usr user) {
        if (autumnDB.select("select * from usr where username = \'" + user.getUsername() + "\'").isEmpty()) {
            autumnDB.save(user);
            return Resp.response("USER ADDED");
        } else {
            return Resp.response("USER EXIST");
        }
    }

    @EndPoint(mappingPath = "/getUsers")
    public Resp getUsers() {
        List<Usr> usrList = autumnDB.select("select * from usr", Usr.class);
        return Resp.response(usrList);
    }

    @EndPoint(mappingPath = "/getProducts")
    public Resp getProducts(String userId) {
        List<Product> products = autumnDB.select("select * from product where id in (select product_id from usr_product where usr_id = " + userId + ")");
        return Resp.response(products);
    }

}
