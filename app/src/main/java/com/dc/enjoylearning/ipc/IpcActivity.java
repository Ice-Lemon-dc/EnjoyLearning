package com.dc.enjoylearning.ipc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dc.enjoylearning.R;
import com.dc.enjoylearning.ipc.location.ILocationManager;
import com.dc.ipc.Ipc;
import com.dc.ipc.IpcService;

/**
 * @author Lemon
 */
public class IpcActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipc);
        startService(new Intent(this, GpsService.class));
        Ipc.connect(this, IpcService.IpcService0.class);
    }

    public void showLocation(View view) {
        ILocationManager location = Ipc.getInstanceWithName(IpcService.IpcService0.class, ILocationManager.class, "getDefault");
        Toast.makeText(this, "" + location.getLocation(), Toast.LENGTH_SHORT).show();
    }
}