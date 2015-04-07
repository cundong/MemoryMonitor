package com.cundong.memory;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class CoreService extends Service {

	private Context mContext = null;
	private Timer timer;

	@Override
	public void onCreate() {
		super.onCreate();

		mContext = getApplicationContext();
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
			long availableMemory = MemoryUtil.getAvailableMemory(mContext) / 1024 / 1024;
			
			//TODO
			String packageName = "com.evernote";
			long totalPss = MemoryUtil.getTotalPss(mContext, packageName);
			
			Intent intent = new Intent(Constants.FILTER);
			intent.putExtra("usedPercentValue", usedPercentValue);
			intent.putExtra("availableMemory", availableMemory);

			intent.putExtra("totalPss", totalPss);
			
			sendBroadcast(intent);
		}
	}
}