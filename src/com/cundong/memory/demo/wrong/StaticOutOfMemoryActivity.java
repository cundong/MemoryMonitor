package com.cundong.memory.demo.wrong;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;

import com.cundong.memory.R;

/**
 * static变量引发的内存溢出
 * 
 * 如果一个变量为static变量，它就属于整个类，而不是类的具体实例，所以static变量的生命周期是特别的长，如果static变量引用了一些资源耗费过多
 * 的实例，例如Context，就有内存溢出的危险。
 * 
 * 
 * Google开发者博客，给出了一个例子：http://android-developers.blogspot.jp/2009/01/avoiding-memory-leaks.html
 * 专门介绍长时间的引用Context导致内存溢出的情况。
 * 
 * 这种情况：
 * 
 * 静态的sBackground变量，虽然没有显式的持有Context的引用，但是：
 * 
 * 当我们执行view.setBackgroundDrawable(Drawable drawable);之后。
 * 
 * Drawable会将View设置为一个回调（通过setCallback()方法），所以就会存在这么一个隐式的引用链：Drawable持有View，View持有Context
 * 
 * sBackground是静态的，生命周期特别的长，就会导致了Context的溢出。
 * 
 * 解决办法：
 * 
 * 1.不用activity的context 而是用Application的Context
 * 
 * 2.在onDestroy()方法中，解除Activity与Drawable的绑定关系,从而去除Drawable对Activity的引用，使Context能够被回收
 * 
 */
public class StaticOutOfMemoryActivity extends Activity {
	
	private static Drawable sBackground;

	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);

		TextView textView = new TextView(this);
		textView.setText("Leaks are bad");
		
		if (sBackground == null) {
			sBackground = getResources().getDrawable(R.drawable.large_bitmap);
		}
		
		textView.setBackgroundDrawable(sBackground);
		
		setContentView(textView);
	}
}