# MemoryMonitor 

一个给开发者使用的Android App内存清理、监控工具。

可以获取当前手机的内存使用比率，可用内存大小，检查一个APP是否存在内存泄漏。

## 内存清理

类似360卫士的 **加速球**，获取系统已用内存比率、可用内存大小，一键清理。

可以用于测试自己开发的Activity、Fragment健壮性，模拟Activity、Fragment被回收的场景，测试自己的程序是否完好的保存、恢复当前场景。

比如：打开你开发的某个Activity、Fragment，切到后台，清理一次内存，在将其切回前台后，看会不会出现空指针异常，以及程序状态是否被恢复。

## 内存监控

Android系统中的内存和Linux系统一样，存在着大量的共享内存。每个APP占内存会有私有和公共的两部分，我们可以通过App的Pss值，可以获取到这两部分内存。

Pss（Proportional Set Size）：实际使用的物理内存，即：自身应用占有的内存+共享内存中比例分配给这个应用的内存。

通过改程序，每隔1秒，获取一次被监控App的Total Pss值。

使用某个功能（可能会导致OOM的那些都要试试），查看Pss是否飙升，或者使用过许久都没有降低。

如果使用后飙升并且长时间都降不下来，那就说明肯定会导致OOM（对象使用过之后还被引用着未释放），如果使用之后Total Pss飙升，但是使用过之后能降下来，也可能会导致OOM，我们还是需要去一点一点排查是什么原因导致的。

如果使用后飙升并且长时间都降不下来，我们就需要使用MAT来进一步分析问题所在。

## 内存优化

Android的虚拟机是基于寄存器的Dalvik，它的最大堆大小一般比较小（最低端的设备16M，后来出的设备变成了24M，48M等等），因此我们所能利用的内存空间是有限的。如果我们使用内存占用超过了一定的限额后就会出现OutOfMemory的错误。

可能会导致内存溢出的情况有以下几种：

>* 对静态变量的错误使用 

如果一个变量为static变量，它就属于整个类，而不是类的具体实例，所以static变量的生命周期是特别的长，如果static变量引用了一些资源耗费过多的实例，例如Context，就有内存溢出的危险。

Google开发者博客，给出了一个例子：http://android-developers.blogspot.jp/2009/01/avoiding-memory-leaks.html
专门介绍长时间的引用Context导致内存溢出的情况。

这种情况：

静态的sBackground变量，虽然没有显式的持有Context的引用，但是：
当我们执行view.setBackgroundDrawable(Drawable drawable);之后。
Drawable会将View设置为一个回调（通过setCallback()方法），所以就会存在这么一个隐式的引用链：Drawable持有View，View持有Context
sBackground是静态的，生命周期特别的长，就会导致了Context的溢出。

解决办法：
1.不用activity的context 而是用Application的Context；（）
2.在onDestroy()方法中，解除Activity与Drawable的绑定关系,从而去除Drawable对Activity的引用，使Context能够被回收；（）

>* 长周期内部类、匿名内部类长时间持有外部类引用导致相关资源无法释放

长周期内部类、匿名内部类，如Handler，Thread，AsyncTask等。

HandlerOutOfMemoryActivity所示的是Handler引发的内存溢出。

ThreadOutOfMemoryActivity所示的是Thread引发的内存溢出。

AsyncTaskOutOfMemoryActivity所示的时AsyncTask引发的内存溢出。

>* Bitmap导致的内存溢出

一般是因为尝试加载过大的图片到内存，或者是内存中已经存在的过多的图片，从而导致内存溢出。

>* 数据库Cursor未关闭
 正常情况下，如果查询得到的数据量较小时不会有内存问题，而且虚拟机能够保证Cusor最终会被释放掉，如果Cursor的数据量特表大，特别是如果里面有Blob信息时，应该保证Cursor占用的内存被及时的释放掉，而不是等待GC来处理。
	
>* 代码中一些细节

1.尽量使用9path
2.Adapter要使用convertView
3.各种监听，广播等，注册后忘记取消
4.使用完对象要及时销毁，能使用局部变量的不要使用全局变量，功能用完成后要去掉对他的引用
5.切勿在循环调用的地方去产生对象，比如很多人不会注意的在getview里new onclicklistener(),这样的方式拖动的次数越多那么就会产生越多的对象。
6.使用Android推荐的数据结构，比如HashMap替换为SparseArray，避免使用枚举类型（在Android平台，枚举类型的内存消耗是Static常量的的2倍）
7.使用lint工具优化工程
8.字符串拼接使用StringBuilder或者StringBuffer
9.尽量使用静态匿名内部类，如果需要对外部类的引用，使用弱引用
10.for循环的时候
final int size = array.length;
for(int i = 0; i< size;i++)
来替代：
for(int i =0;i < array.length;i++) 

我整理了一些开发中可能会导致内存溢出的情况，放在com.cundong.memory.wrong中，并且给出了优化方法，放在com.cundong.memory.right中。

## License

    Copyright 2015 Cundong

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
