package org.wntr.test;

import org.wntr.annotation.web.EndPoint;
import org.wntr.annotation.web.Register;
import org.wntr.web.Resp;

@Register
public class TestRegister {

    @EndPoint(mappingPath = "/test/get")
    public Resp get() {
        return Resp.response("test get ok");
    }
}
