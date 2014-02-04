package com.moreapps;

import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;
import java.util.Date;

public class SwaggerUtils {

    public static String asPath(String s) {
        if (s.startsWith("/")) {
            s = "/" + s.substring(1).replace("/", "_");
        } else {
            s = s.replace("/", "_");
        }
        return s;
    }

    public static boolean isSwaggerPrimitive(Class<?> parameterType) {
        return getSwaggerTypeOrNullFor(parameterType) != null;
    }

    public static String getSwaggerTypeFor(Type parameterType) {
        if (parameterType instanceof Class) {
            return getSwaggerTypeFor((Class<?>) parameterType);
        } else {
            return parameterType.toString();
        }
    }

    public static String getSwaggerTypeFor(Class<?> parameterType) {
        String swaggerType = getSwaggerTypeOrNullFor(parameterType);
        if (swaggerType != null) {
            return swaggerType;
        }
        return parameterType.getName();
    }

    public static String getSwaggerTypeOrNullFor(Class<?> parameterType) {
        Class type = parameterType;
        if (parameterType.isArray()) {
            type = type.getComponentType();
        }
        // swagger types are
        // byte
        // boolean
        // int
        // long
        // float
        // double
        // string
        // Date
        if (String.class.isAssignableFrom(type)) {
            return "string";
        } else if (Boolean.class.isAssignableFrom(type)) {
            return "boolean";
        } else if (Byte.class.isAssignableFrom(type)) {
            return "byte";
        } else if (Long.class.isAssignableFrom(type)) {
            return "long";
        } else if (Integer.class.isAssignableFrom(type)) {
            return "int";
        } else if (Float.class.isAssignableFrom(type)) {
            return "float";
        } else if (MultipartFile.class.isAssignableFrom(type)) {
            return "file";
        } else if (Number.class.isAssignableFrom(type)) {
            return "double";
        } else if (Double.class.isAssignableFrom(type)) {
            return "double";
        } else if (Date.class.isAssignableFrom(type)) {
            return "date";
        } else if (Void.class.isAssignableFrom(type)) {
            return "void";
        }
        // others
        return null;
    }
}
