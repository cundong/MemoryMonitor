package com.cundong.memory.service;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cundong.memory.Constants;
import com.cundong.memory.MainActivity;
import com.cundong.memory.R;
import com.cundong.memory.util.MemoryUtil;
import com.cundong.memory.util.NotificationHandler;
import com.premnirmal.Magnet.IconCallback;
import com.premnirmal.Magnet.Magnet;

/**
 * 类说明： 后台轮询service 每1秒钟更新一次通知栏 更新内存
 * 
 * @date 2015-4-18
 * @version 1.0
 */
public class CoreService extends Service implements IconCallback {

	private static final String TAG = "Magnet";

	private Context mContext = null;
	private Timer mTimer;

	private NotificationHandler mHandler = null;

	private Magnet mMagnet;

	private View mIconView = null;
	private TextView mDescView;
	private ImageButton mClearBtn, mSettingBtn;
	
	@Override
	public void onCreate() {
		super.onCreate();

		mContext = getApplicationContext();
		mHandler = NotificationHandler.getInstance(this);

		mIconView = getIconView();
		mDescView = (TextView) mIconView.findViewById(R.id.content);
		
		mClearBtn = (ImageButton) mIconView.findViewById(R.id.clear_btn);
		mClearBtn.setOnClickListener( new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				Toast.makeText(getApplicationContext(), "clearMemory", Toast.LENGTH_SHORT).show();
				MemoryUtil.clearMemory(getApplicationContext());
			}
		});
		
		mSettingBtn = (ImageButton) mIconView.findViewById(R.id.setting_btn);
		mSettingBtn.setOnClickListener( new OnClickListener(){

			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "test Setting", Toast.LENGTH_SHORT).show();
			}
		});
		
		mMagnet = new Magnet.Builder(this)
			.setIconView(mIconView)
			.setIconCallback(this)
			.setRemoveIconResId(R.drawable.trash)
			.setRemoveIconShadow(R.drawable.bottom_shadow)
			.setShouldFlingAway(true)
			.setShouldStickToWall(true)
			.setRemoveIconShouldBeResponsive(true)
			.build();
		
		mMagnet.show();
	}

	private View getIconView() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.float_view, null);
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
			long totalPss = MemoryUtil.getTotalPss(mContext,
					Constants.TEST_PACKAGENAME);

			Log.i("@Cundong", "totalPss:" + totalPss);

//			Intent intent = new Intent(Constants.FILTER);
//			intent.putExtra("usedPercentValue", usedPercentValue);
//			intent.putExtra("availableMemory", availableMemory);
//			intent.putExtra("totalPss", totalPss);

			float memory = availableMemory / (float) 1024 / (float) 1024;
			DecimalFormat fnum = new DecimalFormat("##0.00");
			
			final String[] content = new String[] {
					getString(R.string.used_percent_value, usedPercentValue),
					getString(R.string.available_memory, fnum.format(memory)),
					getString(R.string.total_pss, totalPss) };

			StringBuffer sb = new StringBuffer();
			sb.append(content[0]).append("\r\n").append(content[1]).append("\r\n").append(content[2]);
			
//			mHandler.createExpandableNotification(mContext, "MemoryMonitor",
//					content, MainActivity.class);
//
//			sendBroadcast(intent);
			
			Bundle data = new Bundle();
			data.putString("content", sb.toString());
			
			Message message = handler.obtainMessage(1);    
            message.what = 1;   
            message.setData(data);
            
            handler.sendMessage(message);    
		}
	}
	
	Handler handler = new Handler(){   
        public void handleMessage(Message msg) {  
            switch (msg.what) {      
            case 1:      
            	Bundle data = msg.getData();
            	String content = data.getString("content");
            	mDescView.setText(content);
            	
                break;      
            } 
            
            super.handleMessage(msg);  
        }  
          
    };  
    
	@Override
	public void onFlingAway() {
		Log.i(TAG, "onFlingAway");
	}

	@Override
	public void onMove(float x, float y) {
		Log.i(TAG, "onMove(" + x + "," + y + ")");
	}

	@Override
	public void onIconClick(View icon, float iconXPose, float iconYPose) {
		Log.i(TAG, "onIconClick(..)");

		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	@Override
	public void onIconDestroyed() {
		Log.i(TAG, "onIconDestroyed()");
		stopSelf();
	}
}