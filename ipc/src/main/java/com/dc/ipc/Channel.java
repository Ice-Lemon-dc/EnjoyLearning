package com.dc.ipc;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.dc.ipc.model.Parameters;
import com.dc.ipc.model.Request;
import com.dc.ipc.model.Response;
import com.google.gson.Gson;

import java.util.concurrent.ConcurrentHashMap;

public class Channel {

    private static volatile Channel instance;

    /**
     * 已经绑定过的
     */
    private ConcurrentHashMap<Class<? extends IpcService>, Boolean> mBinds =
            new ConcurrentHashMap<>();

    /**
     * 正在绑定的
     */
    private ConcurrentHashMap<Class<? extends IpcService>, Boolean> mBinding =
            new ConcurrentHashMap<>();

    /**
     * 已经绑定的服务对应的ServiceConnect
     */
    private final ConcurrentHashMap<Class<? extends IpcService>, IpcServiceConnection> mServiceConnections =
            new ConcurrentHashMap<>();

    private ConcurrentHashMap<Class<? extends IpcService>, IIpcService> binders = new ConcurrentHashMap<>();

    private Gson gson;

    private Channel() {
        gson = new Gson();
    }

    public static Channel getInstance() {
        if (null == instance) {
            synchronized (Channel.class) {
                if (null == instance) {
                    instance = new Channel();
                }
            }
        }
        return instance;
    }

    /**
     * bind 服务 同APP绑定成功
     *
     * @param context Context
     * @param packageName 包名（不同app绑定需要）
     * @param service Class<?>
     */
    public void bind(Context context, String packageName, Class<? extends IpcService> service) {
        IpcServiceConnection ipcServiceConnection;
        // 是否已经绑定
        Boolean isBound = mBinds.get(service);
        if (isBound != null && isBound) {
            return;
        }
        //是否正在绑定
        Boolean isBinding = mBinding.get(service);
        if (isBinding != null && isBinding) {
            return;
        }
        // 要绑定了
        mBinding.put(service, true);
        ipcServiceConnection = new IpcServiceConnection(service);
        mServiceConnections.put(service, ipcServiceConnection);
        Intent intent;
        if (!TextUtils.isEmpty(packageName)) {
            // 跨app的绑定
            intent = new Intent();
            intent.setClassName(packageName, service.getName());
        } else {
            intent = new Intent(context, service);
        }
        context.bindService(intent, ipcServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbind(Context context, Class<? extends IpcService> service) {
        Boolean bound = mBinds.get(service);
        if (bound != null && bound) {
            IpcServiceConnection connection = mServiceConnections.get(service);
            if (connection != null) {
                context.unbindService(connection);
            }
            mBinds.put(service, false);
        }
    }

    class IpcServiceConnection implements ServiceConnection {

        private final Class<? extends IpcService> mService;

        public IpcServiceConnection(Class<? extends IpcService> service) {
            this.mService = service;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IIpcService iIpcService = IIpcService.Stub.asInterface(service);
            binders.put(mService, iIpcService);
            mBinds.put(mService, true);
            mBinding.remove(mService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binders.remove(mService);
            mBinds.remove(mService);
        }
    }

    public Response send(int type, Class<? extends IpcService> service, Class<?> classType, String methodName, Object[] parameters){
        IIpcService iIpcService = binders.get(service);
        if (iIpcService == null) {
            //没有绑定服务
            return new Response(null, false);
        }
        ServiceId annotation = classType.getAnnotation(ServiceId.class);
        String serviceId = annotation.value();
        Request request = new Request(type, serviceId, methodName, makeParameters(parameters));
        try {
            return iIpcService.send(request);
        } catch (RemoteException e) {
            e.printStackTrace();
            // 可以把null变成错误信息
            return new Response(null, false);
        }
    }

    private Parameters[] makeParameters(Object[] objects) {
        Parameters[] parameters;
        if (objects != null) {
            parameters = new Parameters[objects.length];
            for (int i = 0; i < objects.length; i++) {
                parameters[i] = new Parameters(objects[i].getClass().getName(), gson.toJson(objects[i]));
            }
        } else {
            parameters = new Parameters[0];
        }
        return parameters;
    }
}
