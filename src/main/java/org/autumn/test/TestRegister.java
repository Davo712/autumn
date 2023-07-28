package org.autumn.test;

import org.autumn.annotation.JWT.NoJWT;
import org.autumn.annotation.web.EndPoint;
import org.autumn.annotation.web.Register;
import org.autumn.web.Resp;

@Register
public class TestRegister {

    @EndPoint(mappingPath = "/test/get")
    @NoJWT
    public Resp get() {
        return Resp.response("test get ok");
    }
}
