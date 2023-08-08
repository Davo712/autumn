package org.autumn.test;

import org.autumn.annotation.JWT.NoJWT;
import org.autumn.annotation.web.EndPoint;
import org.autumn.annotation.web.Register;
import org.autumn.db.AutumnDB;
import org.autumn.web.Resp;

@Register
public class TestRegister {

    public static AutumnDB autumnDB;

    @EndPoint(mappingPath = "/test/get")
    @NoJWT
    public Resp get() {
        System.out.println(autumnDB.selectSingle("select * from user2"));
        return Resp.response("test get ok");
    }
}
