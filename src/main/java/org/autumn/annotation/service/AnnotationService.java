package org.autumn.annotation.service;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.util.Set;

public class AnnotationService {

    public static Set getAnnotatedMethods(Class<? extends Annotation> annotation) {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder().forPackage("").setScanners(Scanners.MethodsAnnotated));
        return reflections.getMethodsAnnotatedWith(annotation);
    }

    public static Set getAnnotatedClasses(Class<? extends Annotation> annotation) {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder().forPackage("").setScanners(Scanners.TypesAnnotated));
        return reflections.getTypesAnnotatedWith(annotation);
    }

    public static Set getAnnotatedClasses(Class<? extends Annotation> annotation, String path) {
        Reflections reflections = new Reflections(path);
        return reflections.getTypesAnnotatedWith(annotation);
    }

}
