package com.application.util;


import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by chengchao on 16-10-13.
 */
public class SimpleBeanPropertiesUtil {

    public static String getPropertyName(String methodName) {

        if (StringUtils.isBlank(methodName)) {
            return "";
        }

        if (!methodName.startsWith("get") || methodName.length() <= 3) {
            return "";
        }

        String name = methodName.substring(3);

        if (name.length() > 0) {
            return new StringBuffer()
                    .append(Character.toLowerCase(name.charAt(0)))
                    .append(name.substring(1))
                    .toString();
        }

        return name;
    }


    public static final String assignSetterMethodName(String propName) {
        StringBuilder buff = new StringBuilder(propName.length() + 3);
        return buff.append("set")
                .append(Character.toUpperCase(propName.charAt(0)))
                .append(propName.substring(1))
                .toString();
    }


    /**
     * 复制非空的属性从第一个对象到第二个对象
     *
     * @param source
     * @param target
     * @param <T>
     */
    public static final <T> void copyNotNullProperties(T source, T target) {

        if (Objects.isNull(source) || Objects.isNull(target)) {
            return;
        }

        Method[] sourceMethods = source.getClass().getDeclaredMethods();
        Map<String, Method> getterMethods = Stream.of(sourceMethods)
                .filter(method -> method.getName().startsWith("get"))
                .collect(Collectors.toMap(method -> getPropertyName(method.getName()),
                        Function.identity()));

        getterMethods.forEach((prop, method) -> {
            Class<?> type = method.getReturnType();
            Method setterMethod = null;
            try {
                setterMethod = target.getClass()
                        .getDeclaredMethod(assignSetterMethodName(prop), type);

            } catch (NoSuchMethodException e) {
                //e.printStackTrace();
            }
            if (setterMethod != null) {
                try {
                    Object value = method.invoke(source);
                    if (isNonNullAndNonEmptyString(value)) {
                        setterMethod.invoke(target, value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private static final boolean isNonNullAndNonEmptyString(Object input) {

        if (input == null) {
            return false;
        }

        if (input instanceof CharSequence) {
            return StringUtils.isNotBlank((CharSequence) input);
        }

        return true;
    }

    public static void main(String[] args) {

        MyBean a = new MyBean();
        MyBean b = new MyBean();

        a.setId(1);
        a.setAddress("知春路");

        b.setId(2);
        b.setName("chengchao");
        b.setAddress("woqu");

        System.out.println(a);
        System.out.println(b);

        copyNotNullProperties(a, b);

        System.out.println(a);
        System.out.println(b);


        String s = "a";
        System.out.println(s.getClass().isInstance(CharSequence.class));
        System.out.println(s instanceof CharSequence);
    }

    static class MyBean {
        private Integer id;
        private String name;
        private String address;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        @Override
        public String toString() {
            return "id : " + id + ", name : " + name + ", address : " + address;
        }
    }
}
