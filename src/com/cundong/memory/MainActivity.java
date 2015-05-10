package com.cundong.memory;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cundong.memory.service.CoreService;
import com.cundong.memory.util.MemoryUtil;

public class MainActivity extends Activity {

	private Button mButton1, mButton2, mButton3;
	private TextView mTextView;

	private MyReceiver mMyReceiver = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mButton1 = (Button) findViewById(R.id.button1);
		mButton2 = (Button) findViewById(R.id.button2);
		mButton3 = (Button) findViewById(R.id.button3);
		mTextView = (TextView) findViewById(R.id.desc);

		mButton1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, CoreService.class);
				startService(intent);
				
				finish();
			}
		});

		mButton2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, CoreService.class);
				stopService(intent);
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
	protected void onStart() {
		super.onStart();

		IntentFilter filter = new IntentFilter(Constants.FILTER);
		mMyReceiver = new MyReceiver();
		registerReceiver(mMyReceiver, filter);
	}

	@Override
	protected void onStop() {
		super.onStop();

		unregisterReceiver(mMyReceiver);
	}
	
	private class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String usedPercentValue = intent.getStringExtra("usedPercentValue");
			long availableMemory = intent.getLongExtra("availableMemory", 0);
			long totalPss = intent.getLongExtra("totalPss", 0);

			String[] contentArr = new String[] {
					getString(R.string.used_percent_value, usedPercentValue),
					getString(R.string.available_memory, availableMemory
							/ (float) 1024 / (float) 1024),
					getString(R.string.total_pss, Constants.TEST_PACKAGENAME,
							totalPss) };

			StringBuffer sb = new StringBuffer();
			sb.append(contentArr[0]).append("\r\n").append(contentArr[1])
					.append("\r\n").append(contentArr[2]);

			mTextView.setText(sb.toString());
		}
	}
}