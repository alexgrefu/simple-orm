package com.bitwise;

import com.bitwise.annotations.Inject;
import com.bitwise.annotations.Provides;
import com.bitwise.orm.EntityManager;
import com.bitwise.providers.H2ConnectionProvider;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BeanManager {
    private static BeanManager instance;
    private final Map<Class<?>, Supplier<?>> registry = new HashMap<>();

    public static BeanManager getInstance() {
        if (instance == null ) {
            instance = new BeanManager();
        }

        return instance;
    }

    private BeanManager() {
        collectProviders();
    }

    @SneakyThrows
    public <T> T  createInstance(Class<T> clazz) {

        T newInstance = clazz.getConstructor().newInstance();

        Field[] fields = clazz.getDeclaredFields();

        for (var field: fields) {
            Inject inject = field.getAnnotation(Inject.class);
            if (inject != null) {
                Class<?> fieldType = field.getType();
                field.setAccessible(true);
                Supplier<?> supplier = registry.get(fieldType);
                Object objectToInject = supplier.get();
                field.set(newInstance, objectToInject);
            }
        }


        return newInstance;
    }



    private void collectProviders()  {
        List<Class<?>> providers = List.of(H2ConnectionProvider.class);

        for(var provider: providers) {
            Method[] methods = provider.getDeclaredMethods();
            for (var method: methods) {
                Provides provides = method.getAnnotation(Provides.class);
                if (provides != null) {
                    // the type that the provides method returns
                    Class<?> returnType = method.getReturnType();

                    int modifiers = method.getModifiers();
                    Supplier<?> invoker = () -> {
                        try {
                            // an instance of the provider class (needed for method invocation)
                            Object o = provider.getConstructor().newInstance();
                            if (!Modifier.isStatic(modifiers)) {
                                return method.invoke(o);
                            } else {
                                return method.invoke(null);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    };

                    registry.put(returnType, invoker);
                }
            }
        }
    }
}
