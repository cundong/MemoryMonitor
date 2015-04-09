package com.cundong.memory;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.testmemo.R;

/**
 * 
 */
public class OutOfMemoryListActivity extends Activity {

	private TextView mTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_outofmemory_list);

		mTextView = (TextView) findViewById(R.id.desc);
		
		new MyThread().start();
	}

	private class MyThread extends Thread {
		
		int i=0;
		
		@Override
		public void run() {
			super.run();
			
			while(true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				runOnUiThread( new Runnable(){

					@Override
					public void run() {
						mTextView.setText("text:" + i++);
					}
				});
			}
		}
	}
}