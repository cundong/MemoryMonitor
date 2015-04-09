package com.cundong.memory.right;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;

import com.example.testmemo.R;

/**
 * 解决static变量引发的内存溢出
 * 
 * 1.不用activity的context 而是用application的context
 * 
 */
public class OneStaticOutOfMemoryActivity extends Activity {
	
	private static Drawable sBackground;

	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);

		TextView label = new TextView(this.getApplication());
		label.setText("Leaks are bad");
		
		if (sBackground == null) {
			sBackground = getResources().getDrawable(R.drawable.large_bitmap);
		}
		
		label.setBackgroundDrawable(sBackground);
		
		setContentView(label);
	}
}