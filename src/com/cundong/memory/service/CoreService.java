package com.cundong.memory.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.cundong.memory.Constants;
import com.cundong.memory.MainActivity;
import com.cundong.memory.util.MemoryUtil;
import com.cundong.memory.util.NotificationHandler;
import com.cundong.memory.R;

/**
 * 类说明：  后台轮询service 每1秒钟更新一次通知栏 更新内存
 * 
 * @date 	2015-4-18
 * @version 1.0
 */
public class CoreService extends Service {

	private Context mContext = null;
	private Timer mTimer;

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

		mTimer.cancel();
		mTimer = null;

		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancelAll();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (mTimer == null) {
			mTimer = new Timer();
			mTimer.scheduleAtFixedRate(new RefreshTask(), 0, 1000);
		}

		return super.onStartCommand(intent, flags, startId);
	}

	class RefreshTask extends TimerTask {

		@Override
		public void run() {

			String usedPercentValue = MemoryUtil.getUsedPercentValue(mContext);
			long availableMemory = MemoryUtil.getAvailableMemory(mContext);
			long totalPss = MemoryUtil.getTotalPss(mContext, Constants.TEST_PACKAGENAME);

			Log.i("@Cundong", "totalPss:"+totalPss);
			
			Intent intent = new Intent(Constants.FILTER);
			intent.putExtra("usedPercentValue", usedPercentValue);
			intent.putExtra("availableMemory", availableMemory);
			intent.putExtra("totalPss", totalPss);

			String[] content = new String[] {
					getString(R.string.used_percent_value, usedPercentValue),
					getString(R.string.available_memory, availableMemory / (float) 1024 / (float) 1024),
					getString(R.string.total_pss, Constants.TEST_PACKAGENAME, totalPss) };

			mHandler.createExpandableNotification(mContext, "MemoryMonitor", content, MainActivity.class);

			sendBroadcast(intent);
		}
	}
}