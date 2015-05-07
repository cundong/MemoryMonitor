package com.cundong.memory.demo.right;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.cundong.memory.R;

/**
 * 解决Thread引发的内存溢出
 * 
 * 解决办法：
 * 
 * 在线程内部采用弱引用保存Context引用
 * 
 */
public class OneThreadOutOfMemoryActivity extends Activity {
	
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		this.setContentView(R.layout.activity_demo);
		
		mContext = this.getApplicationContext();
		
		new MyThread().start();
	}
	
	private void releaseZip(WeakReference<Context> context){
		/* do something */
	}
	
	private class MyThread extends Thread {
		@Override
		public void run() {
			super.run();

			try {
				Thread.sleep(1000 * 60 * 2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			/* do something */
			releaseZip(new WeakReference<Context>(mContext));
		}
	}
}