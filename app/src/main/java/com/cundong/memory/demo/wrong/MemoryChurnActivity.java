package com.cundong.memory.demo.wrong;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cundong.memory.R;
import com.cundong.memory.util.MemoryUtil;

/**
 * 内存抖动
 *
 * 在短时间内大量的对象被创建又马上被释放，瞬间产生大量的对象会严重占用Young Generation的内存区域，当达到阀值，剩余空间不够的时候，
 * 会触发GC从而导致刚产生的对象又很快被回收。即使每次分配的对象占用了很少的内存，但是他们叠加在一起会增加Heap的压力，从而触发更多其他类型的GC。
 *
 * 这个操作有可能会影响到帧率，并使得用户感知到性能问题。
 *
 * Created by cundong on 2015/12/28.
 */
public class MemoryChurnActivity extends Activity {

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_test);

        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();

                        for( int i=0; i<100; i++) {
                            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.large_img);

                            int rowBytes = bmp.getRowBytes();
                            int height = bmp.getHeight();

                            long memSize = rowBytes * height;
                            Log.d("@Cundong", "memSize =" + memSize + "B =" + memSize * 1.0 / 1024 / 1024 +" M");
                            Log.d("@Cundong", "getUsedPercentValue:" + MemoryUtil.getUsedPercentValue());
                        }

                        /**
                        java.lang.OutOfMemoryError: Failed to allocate a 47 byte allocation with 0 free bytes and 3GB until OOM
                        at android.graphics.BitmapFactory.nativeDecodeAsset(Native Method)
                        at android.graphics.BitmapFactory.decodeStream(BitmapFactory.java:609)
                        at android.graphics.BitmapFactory.decodeResourceStream(BitmapFactory.java:444)
                        at android.graphics.BitmapFactory.decodeResource(BitmapFactory.java:467)
                        at android.graphics.BitmapFactory.decodeResource(BitmapFactory.java:497)
                        at com.cundong.memory.demo.wrong.MemoryChurnActivity$1$1.run(MemoryChurnActivity.java:42)
                         */
                    }
                } .start();
            }
        });
    }
}