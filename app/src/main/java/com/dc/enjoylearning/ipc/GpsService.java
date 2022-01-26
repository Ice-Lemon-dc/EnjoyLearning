package com.dc.enjoylearning.ipc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.dc.enjoylearning.ipc.location.Location;
import com.dc.enjoylearning.ipc.location.LocationManager;
import com.dc.ipc.Ipc;

/**
 * @author Lemon
 */
public class GpsService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LocationManager.getDefault().setLocation(new Location("龙岗区龙岗大道", 1.1d, 2.2d));
        Ipc.register(LocationManager.class);
    }
}
