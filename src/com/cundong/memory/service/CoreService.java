package com.cundong.memory.service;

import java.util.Timer;
import java.util.TimerTask;

import com.cundong.memory.Constants;
import com.cundong.memory.MainActivity;
import com.cundong.memory.util.MemoryUtil;
import com.cundong.memory.util.NotificationHandler;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class CoreService extends Service {

	private Context mContext = null;
	private Timer timer;

	private NotificationHandler mHandler = null;
	
	@Override
	public void onCreate() {
		super.onCreate();

		mContext = getApplicationContext();
		mHandler = NotificationHandler.getInstance(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		timer.cancel();
		timer = null;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (timer == null) {
			timer = new Timer();
			timer.scheduleAtFixedRate(new RefreshTask(), 0, 500);
		}

		return super.onStartCommand(intent, flags, startId);
	}

	class RefreshTask extends TimerTask {

		@Override
		public void run() {
			
			String usedPercentValue = MemoryUtil.getUsedPercentValue(mContext);
			long availableMemory = MemoryUtil.getAvailableMemory(mContext);
			long totalPss = MemoryUtil.getTotalPss(mContext, Constants.TEST_PACKAGENAME);
			
			Intent intent = new Intent(Constants.FILTER);
			intent.putExtra("usedPercentValue", usedPercentValue);
			intent.putExtra("availableMemory", availableMemory);
			intent.putExtra("totalPss", totalPss);
			
			String[] content = new String[] {
					"usedPercentValue:" + usedPercentValue,
					"availableMemory:" + availableMemory / (float) 1024 / (float) 1024 + "M",
					Constants.TEST_PACKAGENAME + " totalPss:" + totalPss + "K" };
			
			mHandler.createExpandableNotification(mContext, "MemoryMonitor",
					content, MainActivity.class);
			
			sendBroadcast(intent);
		}
	}
}