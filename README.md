# MemoryMonitor

> **历史项目**：这是早期 Android 的内存调试示例，保留源码供学习和查阅。不建议把它用作现代 Android 的跨应用监控或内存清理工具。

## 现代 Android 的限制

- **跨应用 PSS**：从 Android 10（API 29）开始，普通应用调用 [`ActivityManager.getProcessMemoryInfo()`](https://developer.android.com/reference/android/app/ActivityManager#getProcessMemoryInfo(int%5B%5D)) 时，只能读取与调用方同 UID 的进程数据，系统也限制采样频率。旧示例不能在现代设备上按秒读取任意应用的 PSS。
- **清理其他应用**：从 Android 14（API 34）开始，[`killBackgroundProcesses()`](https://developer.android.com/about/versions/14/behavior-changes-all#killbackgroundprocesses) 只能结束本应用的后台进程。手动结束进程不等同于系统在内存压力下回收进程，也不会给设备「加速」。

## 现在怎么调试

- **观察内存占用**：连接测试设备或模拟器，把下面的包名换成被测应用的包名，在同一操作前后取样。

  ```sh
  adb shell dumpsys meminfo com.example.app
  ```

  PSS（Proportional Set Size）包含进程独占的内存和按比例分摊的共享内存。它适合看占用趋势；一次升高或没有回落，都不能单独证明泄漏或断定会 OOM。怀疑 Java/Kotlin 对象被意外持有时，用 [Android Studio Profiler 的堆转储](https://developer.android.com/studio/profile/capture-heap-dump) 检查对象及其引用链。参见 [`dumpsys meminfo` 指南](https://developer.android.com/tools/dumpsys#meminfo)。
- **验证进程重建后的状态恢复**：在测试设备上打开被测页面，按 Home 切到后台，再执行 `adb shell am kill com.example.app`，从最近任务返回。确认进程确实重建后，再检查页面状态是否恢复。这是进程重建测试，不是在模拟真实内存压力。参见 [ADB Activity Manager 命令](https://developer.android.com/tools/adb#am)和 [Activity 状态恢复](https://developer.android.com/guide/components/activities/activity-lifecycle#saras)。

## 历史内存示例

以下保留原项目的示例，供理解引用关系和历史写法。旧版运行时描述、固定堆大小和主动调用 `System.gc()` 不是现代 Android 的调优建议。

可能会导致内存溢出的情况有以下几种：

### 对静态变量的错误使用 

如果一个变量为static变量，它就属于整个类，而不是类的具体实例，所以static变量的生命周期是特别的长，如果static变量引用了一些资源耗费过多的实例，例如Context，就有内存溢出的危险。

[Google开发者博客，给出了一个例子](http://android-developers.blogspot.jp/2009/01/avoiding-memory-leaks.html)，专门介绍长时间引用Context导致内存溢出的情况。

示例代码：

```java
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
```

这种情况下，静态的sBackground变量，虽然没有显式的持有Context的引用，但当我们执行`view.setBackgroundDrawable(Drawable drawable);`的时候，drawable 对象会将当前view设置为一个回调，通过 `View.setCallback(this)` 方法。  

具体可见View类的源码：  
```
public void setBackgroundDrawable(Drawable background) {
	//...
	
	if (mBackground == null || mBackground.getMinimumHeight() != background.getMinimumHeight() ||
                mBackground.getMinimumWidth() != background.getMinimumWidth()) {
                requestLayout = true;
        }

        background.setCallback(this);
            if (background.isStateful()) {
                background.setState(getDrawableState());
        }
        
        background.setVisible(getVisibility() == VISIBLE, false);
        mBackground = background;
            
	//...
}
```
`background.setCallback(this);` 代码块就是我们说的设置回调。

所以，这种情况就会存在这么一个隐式的引用链：Drawable持有View，而View持有Context，sBackground 是静态的，生命周期特别的长，于是就会导致了Context的溢出。

解决办法：  

1.不用activity的context 而是用Application的Context；  

```java
private static Drawable sBackground;

@Override
protected void onCreate(Bundle state) {
	super.onCreate(state);

	TextView textView = new TextView(this.getApplication());
	textView.setText("Leaks are bad");
		
	if (sBackground == null) {
		sBackground = getResources().getDrawable(R.drawable.large_bitmap);
	}
		
	textView.setBackgroundDrawable(sBackground);
	
	setContentView(textView);
}
```
2.在onDestroy()方法中，解除Activity与Drawable的绑定关系,从而去除Drawable对Activity的引用，使Context能够被回收； 

```java
@Override
protected void onDestroy() {
	super.onDestroy();
			
	ViewUtils.unbindDrawables(findViewById(android.R.id.content));
		
	System.gc();
}

public static void unbindDrawables(View view) {
	if (view.getBackground() != null) {
		view.getBackground().setCallback(null);
	}
	
	if (view instanceof ViewGroup) {
		for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
			unbindDrawables(((ViewGroup) view).getChildAt(i));
		}
		((ViewGroup) view).removeAllViews();
	}
}
```

### 长周期内部类、匿名内部类长时间持有外部类引用导致相关资源无法释放

长周期内部类、匿名内部类，如Handler，Thread，AsyncTask等。

HandlerOutOfMemoryActivity所示的是Handler引发的内存溢出。

ThreadOutOfMemoryActivity所示的是Thread引发的内存溢出。

AsyncTaskOutOfMemoryActivity所示的时AsyncTask引发的内存溢出。

### Bitmap导致的内存溢出

一般是因为尝试加载过大的图片到内存，或者是内存中已经存在的过多的图片，从而导致内存溢出。

### 数据库Cursor未关闭

正常情况下，如果查询得到的数据量较小时不会有内存问题，而且虚拟机能够保证Cusor最终会被释放掉，如果Cursor的数据量特表大，特别是如果里面有Blob信息时，应该保证Cursor占用的内存被及时的释放掉，而不是等待GC来处理。

### 单例模式引用Context导致的内存泄露

如果在某个Activity中使用 `Singleton instance = Singleton.getInstance(this);` 就会造成该Activity一直被 `Singleton` 引用着，不能释放。这时候，正确的做法是使用 `getApplicationContext()` 来替代 `Activity的Context` ，这样就能避免内存泄露。

### 代码中一些细节

>* 尽量使用9path
>* Adapter要使用convertView
>* 各种监听，广播等，注册的同时要记得取消注册
>* 使用完对象要及时销毁，能使用局部变量的不要使用全局变量，功能用完成后要去掉对他的引用
>* 切勿在循环调用的地方去产生对象，比如在getview()里new OnClicklistener(),这样的话，拖动的时候会new大量的对象出来。
>* 使用Android推荐的数据结构，比如HashMap替换为SparseArray，避免使用枚举类型（在Android平台，枚举类型的内存消耗是Static常量的的2倍）
>* 使用lint工具优化工程
>* 字符串拼接使用StringBuilder或者StringBuffer
>* 尽量使用静态匿名内部类，如果需要对外部类的引用，使用弱引用
>* for循环的使用
用
`final int size = array.length; for(int i = 0; i< size;i++)`
来替代：
`for(int i =0;i < array.length;i++) `

最后，整理了一些开发中可能会导致内存溢出的场景，放在com.cundong.memory.demo.wrong中，并且给出了优化方法，放在com.cundong.memory.demo.right中。

## 4.截图

![截屏][1]

[1]: https://raw.githubusercontent.com/cundong/MemoryMonitor/master/screenshot/app.png
