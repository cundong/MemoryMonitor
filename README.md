# MemoryMonitor 

一个给开发者使用的Android App内存清理、监控工具。

可以获取当前手机的内存使用比率，可用内存大小，检查一个APP是否存在内存泄漏。

## 内存清理

类似360卫士的 **加速球**，获取系统已用内存比率、可用内存大小，一键清理。

可以用于测试自己开发的Activity、Fragment健壮性，模拟Activity、Fragment被回收的场景，测试自己的程序是否完好的保存、恢复当前场景。

## 内存监控

Android系统中的内存和Linux系统一样，存在着大量的共享内存。每个APP占内存会有私有和公共的两部分：ShareDirty、PrivateDirty。

我们一般使用Pss值来判断一个App是否需要优化。

Pss的定义是：
Proportional Set Size实际使用的物理内存（比例分配共享库占用的内存），即：自身应用占有的内存 + 共享内存中比例分配到单个应用身上的内存。

每隔1秒，获取一次某个App的所占用的Total Pss，通过实时查看Total Pss的值来监控应用运行时所占的内存的大小。

## 截图



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
