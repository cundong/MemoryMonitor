package com.cundong.memory.wrong;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.example.testmemo.R;

/**
 * Thread引发的内存溢出
 * 
 * 当前情况：
 * 
 * 1.
 * 内部类MyThread，持有对外部ThreadOutOfMemoryActivity的隐式引用
 * 
 * 2.
 * 如果我们切换横竖屏，默认就会销毁当前Activity，而这个Activity却被MyThread所持有
 * 
 * 于是就出现了溢出。
 * 
 * 
 * 解决办法：
 * 
 * 1.MyThread改为静态内部类
 * 
 * 2.在线程内部采用弱引用保存Context引用
 * 
 */
public class ThreadOutOfMemoryActivity extends Activity {
	
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