package com.dc.ipc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.dc.ipc.model.Parameters;
import com.dc.ipc.model.Request;
import com.dc.ipc.model.Response;
import com.google.gson.Gson;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 不希望使用者直接使用IpcService,预留IpcService0、IpcService1...，方便多个进程使用，如果
 * 预留不够，使用者可以继承IpcService进行使用
 *
 * @author Lemon
 */
public abstract class IpcService extends Service {

    Gson gson = new Gson();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new IIpcService.Stub() {
            @Override
            public Response send(Request request) {
                String methodName = request.getMethodName();
                Parameters[] parameters = request.getParameters();
                String serviceId = request.getServiceId();
                Object[] objects = restoreParameters(parameters);
                Method method = Registry.getInstance().findMethod(serviceId, methodName, objects);
                Response response;
                switch (request.getType()) {
                    case Request.GET_INSTANCE:
                        // 单例方法
                        try {
                            Object result = method.invoke(null, objects);
                            Registry.getInstance().putInstanceObject(serviceId, result);
                            response = new Response(null, true);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                            response = new Response(null, false);
                        }
                        break;
                    case Request.GET_METHOD:
                        // 普通方法
                        Object object = Registry.getInstance().getInstanceObject(serviceId);
                        try {
                            Object returnObject = method.invoke(object, objects);
                            response = new Response(gson.toJson(returnObject), true);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                            response = new Response(null, false);
                        }
                        break;
                    default:
                        response = new Response(null, false);
                        break;
                }
                return response;
            }
        };
    }

    protected Object[] restoreParameters(Parameters[] parameters) {
        Object[] objects = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameters parameter = parameters[i];
            try {
                objects[i] = gson.fromJson(parameter.getValue(), Class.forName(parameter.getType()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return objects;
    }

    public static class IpcService0 extends IpcService {
    }

    public static class IpcService1 extends IpcService {
    }

    public static class IpcService2 extends IpcService {
    }
}
