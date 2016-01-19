package com.cundong.memory.demo.right;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cundong.memory.R;

/**
 * 解决Handler引发的内存溢出
 * 
 * 不使用非静态的内部类，改为使用静态内部类，当静态内部类中需要调用外部的Activity时，改用弱引用。
 * 
 */
public class HandlerOutOfMemoryActivity extends Activity {
	
	private final MyHandler mHandler = new MyHandler(this);

	private static class MyHandler extends Handler {
		private final WeakReference<HandlerOutOfMemoryActivity> mActivity;

		public MyHandler(HandlerOutOfMemoryActivity activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			HandlerOutOfMemoryActivity activity = mActivity.get();
			if (activity != null) {
				/* ... */
			}
		}
	}

	private static final Runnable sRunnable = new Runnable() {
		@Override
		public void run() { 
			/* ... */
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_outofmemory_list);
		
		mHandler.postDelayed(sRunnable, 1000 * 60 * 10);

		finish();
	}
}