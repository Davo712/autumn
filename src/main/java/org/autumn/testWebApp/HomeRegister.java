package org.autumn.testWebApp;

import org.autumn.annotation.JWT.NoJWT;
import org.autumn.annotation.web.BodyParam;
import org.autumn.annotation.web.EndPoint;
import org.autumn.annotation.web.Register;
import org.autumn.db.AutumnDB;
import org.autumn.web.Resp;

@Register
public class HomeRegister {

    public static AutumnDB autumnDB;

    @EndPoint(mappingPath = "/home")
    public String home() {
        return "index";
    }

    @EndPoint(mappingPath = "/addUser", type = "post")
    public Resp addUser() {
        User user = new User();
        user.setName("Hakob");
        user.setSurname("Paronyan");
        user.setUsername("hp1212");
        user.setPassword("misho");
        user.setAge(28);
        user.setActive(true);

        autumnDB.save(user);

        return Resp.response("OK");
    }


    @EndPoint(mappingPath = "/addUser2", type = "post")
    public Resp addUser2(@BodyParam User user, String username) {
        System.out.println(user);
        System.out.println(username);
        return Resp.response("OK");
    }
}
