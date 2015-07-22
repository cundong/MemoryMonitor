package com.cundong.memory.demo.right;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.cundong.memory.R;

/**
 * 解决Thread引发的内存溢出
 * 
 * 1.MyThread改为静态内部类
 */
public class TwoThreadOutOfMemoryActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		this.setContentView(R.layout.activity_demo);
		
		new MyThread(this).start();
	}
	
	private static void releaseZip(Context context){
		/* do something */
	}
	
	private static class MyThread extends Thread {
		
		private final WeakReference<TwoThreadOutOfMemoryActivity> mActivity;

		public MyThread(TwoThreadOutOfMemoryActivity activity) {
			mActivity = new WeakReference<TwoThreadOutOfMemoryActivity>(activity);
		}
		
		@Override
		public void run() {
			super.run();

			try {
				Thread.sleep(1000 * 60 * 2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			/* do something */
			releaseZip(mActivity.get());
		}
	}
}