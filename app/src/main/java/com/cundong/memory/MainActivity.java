package com.cundong.memory;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cundong.memory.service.CoreService;
import com.cundong.memory.service.EmptyService;
import com.cundong.memory.util.MemoryUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int OVERLAY_PERMISSION_REQ_CODE = 1;

    private Button mButton1, mButton2, mButton3, mButton4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton1 = (Button) findViewById(R.id.button1);
        mButton2 = (Button) findViewById(R.id.button2);
        mButton3 = (Button) findViewById(R.id.button3);
        mButton4 = (Button) findViewById(R.id.button4);

        mButton3.setVisibility(Constants.SHOW_MEMORY_CLEAR ? View.VISIBLE : View.GONE);
        mButton4.setVisibility(Constants.SHOW_MEMORY_CLEAR ? View.VISIBLE : View.GONE);

        mButton1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                try2StartMonitor();
            }
        });

        mButton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CoreService.class);
                intent.putExtra("action", 2);
                startService(intent);

                finish();
            }
        });

        mButton3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "clearMemory", Toast.LENGTH_LONG).show();
                MemoryUtil.clearMemory(getApplicationContext());
            }
        });

        mButton4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EmptyService.class);
                startService(intent);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void try2StartMonitor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            Toast.makeText(this, R.string.permission_err, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        } else {
            Intent intent = new Intent(MainActivity.this, CoreService.class);
            intent.putExtra("action", 1);
            startService(intent);

            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean isServiceRunning = isServiceRunning(getPackageName() + ".service.CoreService");

        mButton1.setVisibility(isServiceRunning ? View.GONE : View.VISIBLE);
        mButton2.setVisibility(isServiceRunning ? View.VISIBLE : View.GONE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, R.string.permission_err, Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, CoreService.class);
                intent.putExtra("action", 1);
                startService(intent);

                finish();
            }
        }
    }

    /**
     * 使用Application context来调用getSystemService，避免内存泄漏
     *
     * @param className
     * @return
     */
    private boolean isServiceRunning(String className) {

        ActivityManager activityManager = (ActivityManager) App.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(100);

        if (serviceList == null || serviceList.size() <= 0) {
            return false;
        }

        for (ActivityManager.RunningServiceInfo serviceInfo : serviceList) {

            String serviceName = serviceInfo.service.getClassName();

            if (serviceName.equals(className)) {
                return true;
            }
        }

        return false;
    }
}