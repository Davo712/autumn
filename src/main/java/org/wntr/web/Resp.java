package org.wntr.web;

public class Resp<T> {

    public Object body;

    public <T> Resp(T body) {
        this.body = body;
    }

    private static <T> Resp<T> body(T body) {
        Resp resp = new Resp(body);
        return resp;
    }


    public static <T> Resp<T> response(T body) {
        return body(body);
    }


}
