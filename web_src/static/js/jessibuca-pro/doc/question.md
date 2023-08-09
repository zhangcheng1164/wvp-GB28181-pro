## 关于视频录制的一些问题

### 录制的mp4格式的视频，会有加快倍率播放的现象

因为目前`mp4格式`的录制，是按照固定`25FPS`来录制的。所以如果直播流的fps 只有20，那么录制出来的视频在播放的时候，就会有加快倍率的现象。

### 录制的mp4格式的视频，为啥不支持音频数据。

目前mp4格式的文件，只支持AAC格式封装的音频。但是目前的直播流，音频一般都是G711格式的，所以目前mp4格式的录制，不支持音频数据。

> 虽然也有一些直播流，音频是AAC格式的，但是目前pro的音频解码是放wasm里面做的。所以处理起来会麻烦很多。所以暂不支持音频数据。



## 播放器内部的样式发生变形或者class 丢失

可能得原因：

- 播放器样式被其他样式覆盖了,检查下是否有全局样式覆盖了播放器的样式（播放器内部会有`video`,`canvas`标签）。
- container 设置了双向绑定，导致class丢失。

推荐的 vue 写法

```vue
<template>
    <div class="wrap">
      <!--  不要绑定任何的 :class :style 样式  -->
        <div ref="container" ></div>
    </div>
</template>
<script>
export default {
    name: 'App',
    mounted() {
        // 通过 ref 获取到 dom 节点
        const dom = this.$refs['container']
        // 通过 dom 节点获取到 player 实例
        const player = new window.JessibucaPro({
            container: dom,
        })
    }
}
</script>
```

推荐的 react 写法

```jsx
import React, { useEffect, useRef } from 'react'

export default function App() {
    const container = useRef(null)
    useEffect(() => {
        const player = new window.JessibucaPro({
            container: container.current,
        })
    }, [])
    return (
        <div className="wrap">
            <div ref={container}></div>
        </div>
    )
}

```

## 当container窗口发生变化的时候，播放器如何自适应

播放器提供一个`resize()` 方法。当外界窗口发生变化的时候，调用该方法即可。

```js
const player = new window.JessibucaPro({
    container: dom,
})



// 当container的宽高发生变化的时候，调用resize方法
player.resize();
```

## 关于播放器内部自定义DOM

业务需要有自己的dom在播放器内部，例如

```html
<div id="container"></div>
```

当初始化播放器，传递 `container` 参数的时候，播放器会在 `container` 内部创建播放器的`DOM`

```html
<div id="container">
    // 播放器自己初始化的dom元素
    <canvas></canvas>
    <video></video>
</div>
```

如果业务需在播放器内部有自己的`DOM`，可以直接在`container`内部创建，播放器会自动识别并不会覆盖。


```html
<div id="container">
    // 播放器自己初始化的dom元素
    <canvas></canvas>
    <video></video>
    // 业务的dom
    <div class="your-dom">业务自己的dom</div>
</div>

```


小结： 所以在初始化播放器的时候，业务可以通过在container内部创建自己的dom。

```html

<div id="container">
    // 业务的dom
    <div class="your-dom">业务自己的dom</div>
</div>
```

待播放器初始化的时候，播放器只会在container内部创建自己的dom，不会覆盖业务自己的dom。

> 调用播放器destroy() 方法的时候，播放器内部也只会销毁掉播放器创建的dom，不会销毁业务自己创建的dom。



## 出现页面崩溃之后，问题定位


### 是否开启了`devTools`

首先检查下是否开启了`devTools`。

因为devtools 会打印日志，日志本身就会占用很多内存，也会导致浏览器崩溃。

### 检查崩溃日志

通过`chrome://crashes/`查看崩溃日志

### 如果是必先的

可以通过

<img src="/image/crash-1.png">

<img src="/image/crash-2.png">

观察下内存是否一致增加。


## 安卓webview 下面的一些问题

> 在项目中，会有在webview嵌入的网页中播放视频的需求，会在部分手机上出现白屏或有声音无画面等问题，并且存在全屏按钮点击无效果的问题。


借鉴：https://www.cnblogs.com/hwb04160011/p/13960585.html

### 播放视频白屏、无画面问题解决
   原因是WebView播放视频时可能需要硬件加速才可以正常播放视频，关闭硬件加速可能在部分手机上出现白屏、无画面、无法暂停等问题。解决方法就是开启硬件加速：

在AndroidManifest.xml的application中声明HardwareAccelerate属性
在AndroidManifest.xml的对应activity中声明HardwareAccelerate属性
在使用WebView的代码前添加如下代码：

```java
window.setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
```
### 视频不能全屏问题解决

原因是WebView的视频全屏事件是需要客户端完成后续逻辑的，网页点击全屏按钮会触发WebChromeClient的onShowCustomView方法，全屏后缩回来会触发onHideCustomView方法，在这两个方法中我们要对视频进行一个全屏放大的处理。

在我们的WebView使用之前需要添加的代码如下：

```java
    var fullscreenContainer: FrameLayout? = null
    var customViewCallback: WebChromeClient.CustomViewCallback? = null
    val mWebChromeClient = object : WebChromeClient() {
        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
            super.onShowCustomView(view, callback)
            showCustomView(view, callback)
        }

        override fun onHideCustomView() {
            super.onHideCustomView()
            hideCustomView()
        }
    }

    /**
     * 显示自定义控件
     */
    private fun showCustomView(view: View?, callback: WebChromeClient.CustomViewCallback?) {
        if (fullscreenContainer != null) {
            callback?.onCustomViewHidden()
            return
        }

        fullscreenContainer = FrameLayout(context).apply { setBackgroundColor(Color.BLACK) }
        customViewCallback = callback
        fullscreenContainer?.addView(view)
        val decorView = (context as? Activity)?.window?.decorView as? FrameLayout
        (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        decorView?.addView(fullscreenContainer)
    }

    /**
     * 隐藏自定义控件
     */
    private fun hideCustomView() {
        if (fullscreenContainer == null) {
            return
        }

        val decorView = (context as? Activity)?.window?.decorView as? FrameLayout
        (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        fullscreenContainer?.removeAllViews()
        decorView?.removeView(fullscreenContainer)
        fullscreenContainer = null
        customViewCallback?.onCustomViewHidden()
        customViewCallback = null
    }

```

最终在WebView使用时，为WebView设置WebChromeClient：

```java
    webView.webChromeClient = mWebChromeClient
```

## 打包需要支持es5

目前的打包配置是只支持es6的

如果需要支持es5

修改 `package.json`

```json
{
    "browserslist": [
        "last 1 version",
        "> 1%"
    ]
}
```
