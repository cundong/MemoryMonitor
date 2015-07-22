package com.cundong.memory;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cundong.memory.service.CoreService;
import com.cundong.memory.util.MemoryUtil;

public class MainActivity extends Activity {

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
				Intent intent = new Intent(MainActivity.this, CoreService.class);
				intent.putExtra("action", 1);
				startService(intent);
				
				finish();
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
	
	@Override
	protected void onResume() {
		super.onResume();
		
		boolean isServiceRunning = isServiceRunning(this, "com.cundong.memory.service.CoreService");
		
		mButton1.setVisibility(isServiceRunning ? View.GONE : View.VISIBLE);
		mButton2.setVisibility(isServiceRunning ? View.VISIBLE : View.GONE);
	}
	
	private boolean isServiceRunning(Context mContext, String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
        mContext.getSystemService(Context.ACTIVITY_SERVICE); 
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