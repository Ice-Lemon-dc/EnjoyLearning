package com.dc.enjoylearning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dc.enjoylearning.ipc.IpcActivity;

/**
 * @author Lemon
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void customView(View view) {

    }

    public void ipcCommunication(View view) {
        startActivity(new Intent(this, IpcActivity.class));
    }
}