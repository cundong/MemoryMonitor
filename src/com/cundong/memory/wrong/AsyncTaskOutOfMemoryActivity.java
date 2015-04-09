package com.cundong.memory.wrong;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import com.cundong.memory.util.BitmapUtils;
import com.example.testmemo.R;

/**
 * AsyncTask引发的内存溢出
 * 
 * 当前情况：
 * 
 * 1.
 * 内部类BitmapWorkerTask，持有对外部AsyncTaskOutOfMemoryActivity的隐式引用
 * 
 * 2.
 * 如果我们切换横竖屏，默认就会销毁当前Activity，而这个Activity却被BitmapWorkerTask所持有
 * 
 * 于是就出现了溢出。
 * 
 * 
 * 解决办法：
 * 
 * BitmapWorkerTask内部采用弱引用保存Context引用
 * 
 */
public class AsyncTaskOutOfMemoryActivity extends Activity {
	
	private ImageView mImageView;
	
	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		this.setContentView(R.layout.activity_demo);
		
		mImageView = (ImageView) findViewById(R.id.image);
		
		BitmapWorkerTask task = new BitmapWorkerTask();
	    task.execute(R.drawable.large_bitmap);
	}
	
	class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
	    
	    private int data = 0;

	    // Decode image in background.
	    @Override
	    protected Bitmap doInBackground(Integer... params) {
	        data = params[0];
	        return BitmapUtils.decodeSampledBitmapFromResource(getResources(), data, 100, 100);
	    }

	    // Once complete, see if ImageView is still around and set bitmap.
	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
	        if (bitmap != null) {
	        	mImageView.setImageBitmap(bitmap);
	        }
	    }
	}
}