package org.autumn.web;

public class DynamicWebApp {

    static MainVerticle mainVerticle = new MainVerticle();

    public DynamicWebApp() {
    }

    public void run() {
        mainVerticle.run();
        mainVerticle.addHandler();
    }

    public void setParams(int port, String host) {
        mainVerticle.host = host;
        mainVerticle.port = port;
    }
}
