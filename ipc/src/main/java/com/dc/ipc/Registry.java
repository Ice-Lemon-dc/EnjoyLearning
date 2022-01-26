package com.dc.ipc;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 负责记录服务端注册的信息
 *
 * @author Lemon
 */
public class Registry {

    private static volatile Registry mInstance;

    /**
     * 服务表
     */
    private final ConcurrentHashMap<String, Class<?>> mServices = new ConcurrentHashMap<>();

    /**
     * 方法表
     */
    private final ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, Method>> mMethods = new ConcurrentHashMap<>();

    /**
     * 类id：实例对象
     */
    private final ConcurrentHashMap<String, Object> objects = new ConcurrentHashMap<>();

    public static Registry getInstance() {
        if (mInstance == null) {
            synchronized (Registry.class) {
                if (mInstance == null) {
                    mInstance = new Registry();
                }
            }
        }
        return mInstance;
    }

    public void register(Class<?> service) {
        // 1.服务id与class的表
        ServiceId serviceId = service.getAnnotation(ServiceId.class);
        if (null == serviceId) {
            throw new RuntimeException("必须使用ServiceId注解的服务才能注册！");
        }
        String value = serviceId.value();
        mServices.put(value, service);

        // 2.class的方法表
        ConcurrentHashMap<String, Method> methods = mMethods.get(service);
        if (methods == null) {
            methods = new ConcurrentHashMap<>(10);
            mMethods.put(service, methods);
        }
        for (Method method : service.getMethods()) {
            // 因为有重载方法的存在，不能以方法名作为key，需要方法名带上参数作为key
            // 方法的参数类型数组
            Class<?>[] parameterTypes = method.getParameterTypes();
            String methodString = getMethodString(method.getName(), parameterTypes);
            methods.put(methodString, method);
        }
    }

    /**
     * 查找对应的Method
     *
     * @param serviceId String
     * @param methodName String
     * @param objects Object
     * @return Method
     */
    public Method findMethod(String serviceId, String methodName, Object[] objects) {
        Class<?> service = mServices.get(serviceId);
        ConcurrentHashMap<String, Method> methods = mMethods.get(service);
        String methodString = getMethodString(methodName, objects);
        return methods.get(methodString);
    }

    /**
     * 获取方法字符串
     *
     * @param methodName 方法名
     * @param objects 参数列表
     *
     * @return 方法字符串（带参数）
     */
    private String getMethodString(String methodName, Object[] objects) {
        StringBuilder builder = new StringBuilder(methodName);
        builder.append("(");
        if (objects.length != 0) {
            builder.append(objects[0].getClass().getName());
        }
        for (int i = 1; i < objects.length; i++) {
            builder.append(",").append((objects[i].getClass().getName()));
        }
        builder.append(")");
        return builder.toString();
    }

    public void putInstanceObject(String serviceId, Object object) {
        objects.put(serviceId, object);
    }

    public Object getInstanceObject(String serviceId) {
        return objects.get(serviceId);
    }
}
