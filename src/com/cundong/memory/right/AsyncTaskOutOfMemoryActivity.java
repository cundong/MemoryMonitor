package com.cundong.memory.right;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import com.cundong.memory.util.BitmapUtils;
import com.example.testmemo.R;

/**
 * AsyncTask引发的内存溢出解决办法
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
		
		BitmapWorkerTask task = new BitmapWorkerTask(mImageView);
	    task.execute(R.drawable.large_bitmap);
	}
	
	class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
	    private final WeakReference<ImageView> imageViewReference;
	    private int data = 0;

	    public BitmapWorkerTask(ImageView imageView) {
	        // Use a WeakReference to ensure the ImageView can be garbage collected
	        imageViewReference = new WeakReference<ImageView>(imageView);
	    }

	    // Decode image in background.
	    @Override
	    protected Bitmap doInBackground(Integer... params) {
	        data = params[0];
	        return BitmapUtils.decodeSampledBitmapFromResource(getResources(), data, 100, 100);
	    }

	    // Once complete, see if ImageView is still around and set bitmap.
	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
	        if (imageViewReference != null && bitmap != null) {
	            final ImageView imageView = imageViewReference.get();
	            if (imageView != null) {
	                imageView.setImageBitmap(bitmap);
	            }
	        }
	    }
	}
}