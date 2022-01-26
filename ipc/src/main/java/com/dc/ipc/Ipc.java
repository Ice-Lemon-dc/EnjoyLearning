package com.dc.ipc;

import android.content.Context;

import com.dc.ipc.model.Request;
import com.dc.ipc.model.Response;
import com.google.gson.Gson;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Lemon
 */
public class Ipc {

    private static final Gson GSON = new Gson();

    /**
     * 注册接口
     * 服务端需要暴露出去的服务
     *
     * @param service Class
     */
    public static void register(Class<?> service) {
        Registry.getInstance().register(service);
    }

    /**
     * 连接本APP其他进程 服务
     *
     * @param context Context
     * @param service Class<?>
     */
    public static void connect(Context context, Class<? extends IpcService> service) {
        connect(context, null, service);
    }

    /**
     * 连接其他APK进程服务
     *
     * @param context Context
     * @param packageName 包名（不同app绑定需要）
     * @param service Class<?>
     */
    public static void connect(Context context, String packageName, Class<? extends IpcService> service) {
        Channel.getInstance().bind(context, packageName, service);
    }

    public static void disConnect(Context context, Class<? extends IpcService> service) {
        Channel.getInstance().unbind(context.getApplicationContext(), service);
    }

    public static <T> T getInstance(Class<? extends IpcService> service, Class<T> instanceClass, Object... parameters) {
        return getInstanceWithName(service, instanceClass, "getInstance", parameters);
    }

    public static <T> T getInstanceWithName(Class<? extends IpcService> service, Class<T> instanceClass, String methodName, Object... parameters) {
        if (!instanceClass.isInterface()) {
            throw new IllegalArgumentException("必须以接口进行通信。");
        }
        Response response = Channel.getInstance().send(Request.GET_INSTANCE, service, instanceClass, methodName, parameters);
        if (response.isSuccess()) {
            //返回一个对象 动态代理
            return getProxy(instanceClass, service);
        }
        return null;
    }

    private static <T> T getProxy(Class<T> instanceClass, Class<? extends IpcService> service) {
        ClassLoader classLoader = instanceClass.getClassLoader();
        return (T) Proxy.newProxyInstance(classLoader, new Class[]{instanceClass}, new IpcInvocationHandler(instanceClass, service));
    }

    static class IpcInvocationHandler implements InvocationHandler {

        private final Class<?> instanceClass;
        private final Class<? extends IpcService> service;

        public IpcInvocationHandler(Class<?> instanceClass, Class<? extends IpcService> service) {
            this.instanceClass = instanceClass;
            this.service = service;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            Response response = Channel.getInstance().send(Request.GET_METHOD, service, instanceClass, method.getName(), args);
            if (response.isSuccess()) {
                Class<?> returnType = method.getReturnType();
                if (returnType != Void.class && returnType != void.class) {
                    String source = response.getSource();
                    return GSON.fromJson(source, returnType);
                }
            }
            return null;
        }
    }
}
