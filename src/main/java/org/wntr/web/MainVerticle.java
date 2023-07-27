package org.wntr.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import org.wntr.annotation.EndPoint;
import org.wntr.annotation.Register;
import org.wntr.annotation.RequiredParam;
import org.wntr.annotation.service.AnnotationService;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

public class MainVerticle extends AbstractVerticle {

    Vertx vertx = Vertx.vertx();
    Router router = Router.router(vertx);

    int port = 8080;
    String host = "localhost";

    public void addHandler() {
        Set<Method> annotatedMethods = AnnotationService.getAnnotatedMethods(EndPoint.class);
        annotatedMethods.forEach(method -> {
            if (method.getDeclaringClass().isAnnotationPresent(Register.class)) {
                String mappingPath = method.getAnnotation(EndPoint.class).mappingPath();
                String type = method.getAnnotation(EndPoint.class).type();
                Boolean needRC = method.getAnnotation(EndPoint.class).needRC();
                Class<?> clazz = method.getDeclaringClass();
                if (type.equals("post")) {
                    addPostHandler(mappingPath, method, clazz, needRC);
                } else {
                    addGetHandler(mappingPath, method, clazz, needRC);
                }
            }
        });
    }


    public void addGetHandler(String path, Method method, Class<?> clazz, Boolean needRC) {
        router.get(path).handler(rc -> {
            try {
                Parameter[] parameters = method.getParameters();

                String[] paramNames = new String[parameters.length];
                String[] paramValues = new String[paramNames.length];
                for (int i = 0; i < parameters.length; i++) {
                    paramNames[i] = parameters[i].getName();
                    paramValues[i] = rc.queryParam(paramNames[i]).toString();
                    paramValues[i] = paramValues[i].substring(1, paramValues[i].length() - 1);
                    if ((parameters[i].getAnnotation(RequiredParam.class) != null)&&(paramValues[i].equals(""))) {
                        return;
                    }
                }
                if (needRC) {
                    method.invoke(clazz.getDeclaredConstructor().newInstance(), rc);
                } else {
                    rc.response().end(Json.encode(method.invoke(clazz.getDeclaredConstructor().newInstance(), paramValues)));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("Register GET end-point: " + path);
    }

    public void addPostHandler(String path, Method method, Class<?> clazz, Boolean needRC) {
        router.post(path).handler(rc -> {
            try {
                Parameter[] parameters = method.getParameters();

                String[] paramNames = new String[parameters.length];
                String[] paramValues = new String[paramNames.length];
                for (int i = 0; i < parameters.length; i++) {
                    paramNames[i] = parameters[i].getName();
                    paramValues[i] = rc.queryParam(paramNames[i]).toString();
                    paramValues[i] = paramValues[i].substring(1, paramValues[i].length() - 1);
                    if ((parameters[i].getAnnotation(RequiredParam.class) != null)&&(paramValues[i].equals(""))) {
                        return;
                    }
                }
                if (needRC) {
                    method.invoke(clazz.getDeclaredConstructor().newInstance(), rc);
                } else {
                    rc.response().end(Json.encode(method.invoke(clazz.getDeclaredConstructor().newInstance(), paramValues)));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("Register POST end-point: " + path);
    }

    public void run() {
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port, host, res -> {
                    if (res.succeeded()) {
                        System.out.println("Server started");
                    } else if (res.failed()) {
                        System.out.println("Server failed");
                    }
                });
    }
}
