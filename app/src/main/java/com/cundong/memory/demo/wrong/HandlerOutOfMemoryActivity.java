package com.cundong.memory.demo.wrong;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cundong.memory.R;

/**
 * Handler引发的内存溢出
 * 
 * 1.
 * 当一个Android应用启动的时候，会自动创建一个供主线程使用的Looper实例，这个Looper实例负责一个一个的处理消息队列中的消息对象，它的生命周期和当前应用
 * 的生命周期是一样的。
 * 
 * 2.
 * 当Handler在主线程中初始化之后，发送一个消息(target为当前Handler)至消息队列，这个消息对象就已经包含了Handler实例的引用，只有这样，Looper在处理这条消息的时候，才可以
 * 调用Handler的handlerMessage(Message)来完成消息的处理。
 * 
 * 3.非静态的内部类和匿名内部类，都会隐式的持有一个外部类的引用。
 * 
 * 由于这3个原因。
 * 
 * 当Activity finish掉，被延迟的消息会存在消息队列中10分钟，这个消息中又包含了Handler的引用，Handler是一个匿名内部类，又隐式的持有外部Activity的引用，导致
 * 其无法回收，进一步导致Activity持有的很多资源都无法回收，也就是内存泄露了。
 * 
 * 正确示例：com.cundong.memory.right.HandlerOutOfMemoryActivity
 * 
 */
public class HandlerOutOfMemoryActivity extends Activity {
	
	private final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			/* ... */
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_outofmemory_list);

		// Post a message and delay its execution for 10 minutes.
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() { 
				
				/* ... */
			}
		}, 1000 * 60 * 10);
		
		finish();
	}
}