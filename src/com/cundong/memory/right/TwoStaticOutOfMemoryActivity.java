package com.cundong.memory.right;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;

import com.cundong.memory.ViewUtils;
import com.example.testmemo.R;

/**
 * 解决static变量引发的内存溢出
 * 
 * 2.在 onDestroy() 方法中，解除 Activity 和 biamap（drawble）的绑定关系,从而去除bitmap对activity 引用，让系统适时的去回收
 * 
 */
public class TwoStaticOutOfMemoryActivity extends Activity {
	
	private static Drawable sBackground;

	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);

		TextView label = new TextView(this);
		label.setText("Leaks are bad");
		
		if (sBackground == null) {
			sBackground = getResources().getDrawable(R.drawable.large_bitmap);
		}
		
		label.setBackgroundDrawable(sBackground);
		
		setContentView(label);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
			
		ViewUtils.unbindDrawables(findViewById(android.R.id.content));
		
		System.gc();
	}
}