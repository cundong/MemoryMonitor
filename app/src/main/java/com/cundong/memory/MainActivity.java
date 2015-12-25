package com.cundong.memory;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cundong.memory.service.CoreService;
import com.cundong.memory.util.MemoryUtil;

import java.util.List;

public class MainActivity extends Activity {

	public static int OVERLAY_PERMISSION_REQ_CODE = 1;

	private Button mButton1, mButton2, mButton3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mButton1 = (Button) findViewById(R.id.button1);
		mButton2 = (Button) findViewById(R.id.button2);
		mButton3 = (Button) findViewById(R.id.button3);

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
				Toast.makeText(getApplicationContext(), "clearMemory", Toast.LENGTH_SHORT).show();
				MemoryUtil.clearMemory(getApplicationContext());
			}
		});
	}

	@TargetApi(Build.VERSION_CODES.M)
	private void try2StartMonitor() {
		if (!Settings.canDrawOverlays(this)) {
			Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
					Uri.parse("package:" + getPackageName()));
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
		
		boolean isServiceRunning = isServiceRunning("com.cundong.memory.service.CoreService");
		
		mButton1.setVisibility(isServiceRunning ? View.GONE : View.VISIBLE);
		mButton2.setVisibility(isServiceRunning ? View.VISIBLE : View.GONE);
	}

	@TargetApi(Build.VERSION_CODES.M)
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
			if (!Settings.canDrawOverlays(this)) {
				Toast.makeText(this, "sorry, SYSTEM_ALERT_WINDOW permission not granted!!", Toast.LENGTH_SHORT).show();
			} else {
				Intent intent = new Intent(MainActivity.this, CoreService.class);
				intent.putExtra("action", 1);
				startService(intent);

				finish();
			}
		}
	}

	/**
	 * Application context来调用getSystemService，避免内存泄漏
	 *
	 * @param className
	 * @return
	 */
	private boolean isServiceRunning(String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) App.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(100);
        
        if (!(serviceList.size()>0)) {
            return false;
        }

        for (int i=0; i<serviceList.size(); i++) {
        	
        	String serviceName = serviceList.get(i).service.getClassName();
        	
        	Log.i("@Cundong", "serviceName:" + serviceName);
            if (serviceName.equals(className)) {

                isRunning = true;
                break;
            }
        }

        return isRunning;
    }
}