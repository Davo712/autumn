package org.autumn.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.autumn.annotation.JWT.EnableJWT;
import org.autumn.annotation.JWT.GetParamJWT;
import org.autumn.annotation.JWT.GetTokenJWT;
import org.autumn.annotation.JWT.NoJWT;
import org.autumn.annotation.service.AnnotationService;
import org.autumn.annotation.web.EndPoint;
import org.autumn.annotation.web.Register;
import org.autumn.annotation.web.RequiredParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;


public class MainVerticle extends AbstractVerticle {

    Vertx vertx = Vertx.vertx();
    Router router = Router.router(vertx);

    int port = 8080;
    String host = "localhost";

    boolean needJWT = !AnnotationService.getAnnotatedClasses(EnableJWT.class).isEmpty();

    public void addHandler() {
        if (needJWT) {
            Set<Class> classes = AnnotationService.getAnnotatedClasses(EnableJWT.class);
            classes.forEach(c -> {
                try {
                    AutumnJWT.timeoutHours = c.getConstructor().newInstance().getClass().getAnnotation(EnableJWT.class).timeoutHours();
                    AutumnJWT.SECRET_KEY = c.getConstructor().newInstance().getClass().getAnnotation(EnableJWT.class).secretKey();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        Set<Method> annotatedMethods = AnnotationService.getAnnotatedMethods(EndPoint.class);
        annotatedMethods.forEach(method -> {
            if (method.getDeclaringClass().isAnnotationPresent(Register.class)) {
                String mappingPath = method.getAnnotation(EndPoint.class).mappingPath();
                String type = method.getAnnotation(EndPoint.class).type();
                Boolean needRC = method.getAnnotation(EndPoint.class).needRC();
                String redirectPath = method.getAnnotation(EndPoint.class).redirectPath();
                Class<?> clazz = method.getDeclaringClass();
                if (type.equals("post")) {
                    addPostHandler(mappingPath, method, clazz, needRC, redirectPath);
                } else {
                    addGetHandler(mappingPath, method, clazz, needRC, redirectPath);
                }
            }
        });
    }


    public void addGetHandler(String path, Method method, Class<?> clazz, Boolean needRC, String redirectPath) {
        router.get(path).handler(handle(method, clazz, needRC, redirectPath));
        System.out.println("Register GET end-point: " + path);
    }

    public void addPostHandler(String path, Method method, Class<?> clazz, Boolean needRC, String redirectPath) {
        router.post(path).handler(handle(method, clazz, needRC, redirectPath));
        System.out.println("Register POST end-point: " + path);
    }

    private Handler<RoutingContext> handle(Method method, Class<?> clazz, Boolean needRC, String redirectPath) {
        return rc -> {
            if (needJWT) {
                if (!method.isAnnotationPresent(NoJWT.class)) {
                    if (rc.request().getHeader("Authorization") == null) {
                        rc.response().end("JWT token missing");
                        return;
                    }
                    if (!AutumnJWT.checkJWT(rc.request().getHeader("authorization"))) {
                        rc.response().end("Invalid JWT token");
                        return;
                    }
                }
            }
            if (!redirectPath.equals("")) {
                rc.reroute(redirectPath);
                return;
            }
            try {
                Parameter[] parameters = method.getParameters();

                String[] paramNames = new String[parameters.length];
                String[] paramValues = new String[paramNames.length];
                for (int i = 0; i < parameters.length; i++) {
                    paramNames[i] = parameters[i].getName();
                    if ((needJWT) && (parameters[i].getAnnotation(GetParamJWT.class) != null)) {
                        paramValues[i] = AutumnJWT.getParam(paramNames[i], rc.request().getHeader("Authorization"));
                    } else if ((needJWT) && (parameters[i].getAnnotation(GetTokenJWT.class) != null)) {
                        paramValues[i] = rc.request().getHeader("Authorization");
                    } else {
                        paramValues[i] = rc.queryParam(paramNames[i]).toString();
                        paramValues[i] = paramValues[i].substring(1, paramValues[i].length() - 1);
                    }
                    if ((parameters[i].getAnnotation(RequiredParam.class) != null) && (paramValues[i].equals(""))) {
                        return;
                    }
                }
                if (needRC) {
                    method.invoke(clazz.getDeclaredConstructor().newInstance(), rc);
                } else {
                    rc.response().end(Json.encode(((Resp) method.invoke(clazz.getDeclaredConstructor().newInstance(), paramValues)).body));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
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
