package org.autumn.web;

public interface DynamicWebApp {

    MainVerticle mainVerticle = new MainVerticle();

    static void run() {
        mainVerticle.run();
        mainVerticle.addHandler();
    }

    static void setParams(int port, String host) {
        mainVerticle.host = host;
        mainVerticle.port = port;
    }
}
