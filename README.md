
AndroidMP3Recorder
==================






## 说明 

fork自 https://github.com/GavinCT/AndroidMP3Recorder,原项目已停止更新

原项目实现思路讲解：[Android MP3录音实现](http://www.cnblogs.com/ct2011/p/4080193.html)

## 原项目特点

* 边录边转码,录完就是mp3格式,没有额外转码时间
* 录制过程中暂停,已录制的那段音频是可以播放的

## 新增特点:

* 方法调用顺序没有坑,随便怎么调,不会崩,不会出现状态错乱问题

* 增加6.0以下系统的录音权限判断,没有录音权限时,内部会发出一个没有权限的event,自行处理(比如,可以弹窗提示,让用户去"权限管理"界面打开权限)

* 增加了很完善的回调

  ,以事件驱动模型的思路来解决回调问题:  

  ```
  比如:  mp3Recorder.resume()  --> 内部暂停--> 内部调用到callback.onResume()
  ```

之所以这么设计,是因为mp3Recorder.resume()可能在service中调用,而回调更新UI经常在activity界面上

# 使用

## 全局初始化

```
Mp3RecorderUtil.init(Context context ,boolean isDebug)
```

## 对象初始化设置

> 经常是 在service中new Mp3Recorder() ,然后通过binder传递到activity中,在activity中进行callback设置

```
mRecorder = new Mp3Recorder();
mRecorder.setOutputFile(path)
        .setMaxDuration(30)//录音最大时长,达到后自动停止录音.
        .setCallback(callback);
```

## 操作方法

> 不会抛异常,内部已维护录制状态,调用顺序颠倒也不会有任何问题.

```
	mRecorder.start();
    mRecorder.pause();
    mRecorder.resume();
    mRecorder.stop(Mp3Recorder.ACTION_STOP_ONLY);//还有ACTION_STOP_AND_NEXT,ACTION_RESET  就是一个标记而已
    mRecorder.reset();
```

## 回调

```
public interface Callback {
    void onStart();
    void onPause();
    void onResume();
    void onStop(int action);
    void onReset();

    void onRecording(double duration,double volume);//已经录制的时间,实时音量分贝值
    void onMaxDurationReached();
}
```

## 权限

6.0及以上示例:
> 注意,6.0以下的系统,RxPermissions返回的都是有权限,拿不到真实的情况,所以要用下面的AudioNoPermissionEvent事件来判断.

```
new RxPermissions(this)
        .request(Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        .subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if(aBoolean){
                    init();
                }else {
                    toast("权限被拒绝了");
                     init();
                }
            }
        });
```

> 6.0以下时,各家rom对权限的管理不统一.这里直接深入音频录制流中进行判断,并在没有权限时用eventbus来post一个AudioNoPermissionEvent.调用者只要接收并处理就行. 这样就规避了各家rom的不同.



> AudioNoPermissionEvent 接收示例:

```
@Subscribe(threadMode = ThreadMode.MAIN)
public void onMessageEvent(AudioNoPermissionEvent event) {
    toast("没有录音权限,无法录音,赶紧去设置吧");
}
```

> 说明: 小米和三星手机,没有权限时,马上能够收到AudioNoPermissionEvent.



> 华为手机没有权限时,没有任何地方能够捕捉到,只能通过下面的方式来发出:



调用audioRecord.startRecording()之前,用handler发出一个延时n秒的runnable,在真正进入录制的时候,取消这个runnable.如果这个runnable最终执行了,那么说明在给定的n秒内,没有获取到权限.

n的取值:第一次调用mp3recorder.start()时,为10s,再次调用时,为1s

 ![huaweipermission](huaweipermission.png)





## 集成到项目中

## 注意
 如果是直接拷贝代码放到你的model中,因为涉及到jni,那么库中的包名路径一个字都不能改,必须全路径拷贝,否则会报找不到so文件的错误.

## gradle

**Step 1.** Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```
    allprojects {
        repositories {
            ...
            maven { url "https://jitpack.io" }
        }
    }
```

**Step 2.** Add the dependency

```
    dependencies {
            compile 'com.github.hss01248:AndroidMP3Recorder:lastest release'
    }
```

lastest release:https://github.com/hss01248/AndroidMP3Recorder/releases



> 原说明文档:

# 3. 关于音量部分的解释
音量的计算，来自于 [三星开发者文档-Displaying Sound Volume in Real-Time While Recording](http://developer.samsung.com/technical-doc/view.do?v=T000000086)    
里面对于音量的最大值设置为了4000，而我实际测验中发现大部分声音不超过2000，所以就在代码中暂时设置为2000。 
这方面没有找到相关资料，如果有人知道理论值之类的，请联系我(chentong.think@gmail.com) (原库作者)完善此库，谢谢。

# 4. 关于so库的声明
so库本身没有任何限制，但受限于Android NDK的支持
- arm armv7 支持Android 1.5 (API Level 3)及以上版本
- x86支持Android 2.3 (API Level 9)及以上版本

# 5. 常见问题声明

## 使用so中的部分

本库提供了 arm mips x86 等多种so，如果您只需要其中的几种，可以在gradle中添加下面的语法：

```groovy
productFlavors {
  arm {
    ndk {
      abiFilters "armeabi-v7a", "armeabi"
    }
  }
  x86 {
    ndk {
      abiFilter "x86"
    }
  }
}
```

具体的选择策略,参见:

[jni中arm64-v8a，armeabi-v7a,armeabi文件夹的意义和用法](http://blog.csdn.net/hss01248/article/details/51505531)

以上会在arm中接入armv7 arm包，最新的64位v8不会放入。 同时没有提供mips的flavor，也保证了没有mips的so。但最新的1.5.0插件不支持这种写法，且新版的ndk还处于试验阶段，所以一般使用了上述写法会报错，报错中给出了提示，即在gradle.properties中添加

```
android.useDeprecatedNdk=true
```

即可正常使用

## 遇到了 java.lang.UnsatisfiedLinkError错误

这种情况一般是so不全导致的。

以app使用了百度地图sdk为例：

假如百度地图只提供了arm 的so ， 您使用了本库后会有arm armv7 armv8等多种库，这样打包后会产生armeabi、armeabi-v7a、armeabi-v8a等多个文件夹，但百度地图在armv7 v8下并没有so，这样就会引发`java.lang.UnsatisfiedLinkError: Couldn't load BaiduMapSDK_v3_2_0_15 from loader`错误。

解决办法有两种：

- 联系其他库的提供者补全
- 如果不行的话，可以利用上面提到的abiFilters来过滤掉本库的so，这样只提供arm一般是可以兼容的。


# 6. License

    Copyright 2014 GavinCT

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    # todo 
    音量数据的实时传出(用户绘制波形),
    参考  https://github.com/CarGuo/RecordWave
