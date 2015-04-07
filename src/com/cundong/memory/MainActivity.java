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

import com.example.testmemo.R;

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

			StringBuffer sb = new StringBuffer();
			sb.append("usedPercentValue:").append(usedPercentValue)
					.append("\r\n").append("availableMemory:")
					.append(availableMemory).append("M");

			mTextView.setText(sb.toString());
		}
	}
}