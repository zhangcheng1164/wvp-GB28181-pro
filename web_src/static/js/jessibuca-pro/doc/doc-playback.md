
## 录像流技术方案

目前录像流支持
1. wasm（simd）软解码 + canvas（webgl/webgpu）渲染
2. wcs硬解码+ canvas（webgl）渲染



## 录像流集成建议

涉及到录像流的 api 有：

- playback
- playbackPause
- pause
- playbackResume
- forward
- normal
- updatePlaybackForwardMaxRateDecodeIFrame
- setPlaybackStartTime
- getPlaybackCurrentRate
- playbackClearCacheBuffer
- isPlaybackPause
- updatePlaybackLocalOneFrameTimestamp

涉及到录像流的 事件 有：

- playbackSeek
- playbackStats
- playbackTimestamp
- playbackPauseOrResume
- playbackPreRateChange
- playbackRateChange

具体集成的demo 可以看 `playback-demo.html`文件

调用的是`playback`方法。

```js
jessibuca.playback(url, {
    // config
})
```

### 24小时UI配置

录像流支持配置24小时组件(支持放大和缩小)和倍率按钮

对于配置参数

```js
jessibucaPro.playbackConfig('url', {
    playList: [
        {
            start: 16538416340000,
            end: 16538434200000
        },
        {
            start: 1653881963,// 会自动补齐最后四个0
            end: 1653885397 // 会自动补齐最后四个0
        }
    ]
})
```

> 未匹配到的时间，在ui上面是不可触发点击事件的。

### 24小时UI点击事件（seek）

当点击了ui上面的时间，会触发`playbackSeek`事件，可以监听这个事件，来进行业务逻辑处理。

```js
jessibucaPro.on('playbackSeek', (data) => {
    // data 为对象
    /**
     * {
        "hour": 1,
        "min": 2,
        "second": 0
        }
     */

    // 拿到时间，去调用服务器端的seek接口
})
```

业务代码可以根据回调的数据，来进行通知服务器端进行seek 操作。

当服务器端更新完了流，可以调用`setPlaybackStartTime`方法，来更新ui的时间。

总体流程： 点击ui获取时间->调用服务器端seek接口->服务器端更新流->调用`setPlaybackStartTime`方法更新ui时间

> setPlaybackStartTime的参数是时间戳，单位是秒。指的是当天的时间戳。播放器拿到这个时候，会转换成 时 分 秒， 然后会更新UI
> 上面的当前时间。

例如 需要更新的时间是3:10:10

```js
// 3:10:10
const time = new Date().setHours(3, 10, 10, 0)
jessibucaPro.setPlaybackStartTime(time)
```

> ui 也支持更新当前的进度时间

### 24小时UI配置（更新）开始时间

在录播流播放模式下，底部的开始时间是从0点开始的。

所以，如果需要配置开始时间，可以调用`setPlaybackStartTime`方法。

```js

// 3:10:10
const time = new Date().setHours(3, 10, 10, 0)
jessibucaPro.setPlaybackStartTime(time)
```

这样配置了之后，UI上面的开始时间就是从3:10:10开始一秒一秒的走。

> 同样的在触发`seek`事件的时候，需要在ui上面更新开始时间，也是调用这个方法。

### 暂停（不关闭流）

> 会存在业务上面的录像流暂停，但是不关闭流的需求，这个时候可以调用播放器的`playbackPause()`方法，来暂停播放。

> 暂停的时候会把缓存的数据清空。所以如果不清除缓存可以设置参数`isPlaybackPauseClearCache:false`。

1. 调用服务器端暂停接口
2. 调用播放器的`playbackPause()`方法，画面暂停
3. 调用服务器端恢复接口
4. 调用播放器的`playbackResume()`方法，画面播放

### 暂停（关闭流）

可以调用 `playbackPause(true)` 或者 `pause()` 方法。

### 倍率播放

1. 首先调用服务器端接口，开启倍率播放
2. 调用播放器的`forward(rate)`方法
3. 关闭倍率播放，调用服务器端接口
4. 调用播放器的`forward(1)`或者`normal()`方法

### 倍率播放（配置了rateConfig 参数）

1. 首先选择UI上面的倍率按钮，配合`playbackPreRateChange`事件，拿到倍率，
2. 调用服务器端接口，开启倍率播放。
3. 调用播放器的`forward(rate)`方法。

### seek操作（配合24小时UI）

1. 点击24小时UI，监听`playbackSeek`获取时间
2. 调用服务器端seek接口
3. 服务器端更新流
4. 调用`setPlaybackStartTime`方法，更新ui时间

### 解码前缓存数据

> 会存在有些流，一倍率的时候，但是服务器端推送的流是2倍的，这个时候就需要播放器兼容这种特殊的流，只解码所需的1倍率的数据。

可以通过在调用playback方法播放的时候，config配置参数里面配置`isCacheBeforeDecodeForFpsRender` 参数

```js
jessibucaPro.playback(url, {
    isCacheBeforeDecodeForFpsRender: true
})
```

### 监听当前缓存情况（需要通知服务器端暂停推流）

目前播放器的缓存数据有，`待解码`，`解码后待渲染` 两块缓存数据，单位帧。

> 待解码缓存数据，是指，从服务器端获取到的数据，还没有解码的数据。
>
> 解码后待渲染缓存数据，是指，解码后的数据，还没有渲染的数据。


可以通过监听`stats`事件，来获取当前的缓存数据。

`playbackVideoBuffer` 指的是`视频待渲染`帧。

`demuxBuffer` 指的是`视频待渲染`帧。

可以通过`stats` 事件里面的fps 来计算出每一帧的时间，然后计算出缓存的时间。

假设fps 为25，那么每一帧的时间就是 1000 / 25 = 40ms。

如果`playbackVideoBuffer` 为100，那么缓存的时间就是 100 * 40 = 4000ms = 4s。如果`demuxBuffer` 为100，那么缓存的时间就是 100

* 40 = 4000ms = 4s。 那么一起的缓存时长就是 4 + 4 = 8s

> stats 事件里面已经添加了 `playbackCacheDataDuration`值。

### 固定倍率播放

> 会存在一些流，服务器端的录像流的推过来的fps不是定码率的，是波动性比较大的那种，这个时候就需要客户端去按照固定倍率去播放。


则需要配置使用固定fps进行渲染，并配置 fps 值。默认值是 `25`

```
playbackConfig:{
    isUseFpsRender:true,
    fps: '', // fps值
}
```

可能会存在fps 设置的不对的情况，所以播放器也会去校正这个参数。

> 播放器也会根据流的时间戳来计算出准确的`fps`大小，并更新到`fps`参数上面去。

### 使用播放器本地时间戳回调时间（从0 开始那种）

> 存在一些流，流里面的时间戳不是从 0 开始的，这个时候，如果录像流想要从0 开始回调时间。


这个时候就需要配置 `playbackConfig` 参数

```
playbackConfig:{
    isUseLocalCalculateTime:true, // 是否使用本地时间来计算playback时间
    localOneFrameTimestamp:40 // 一帧的间隔时间戳 40ms  100 / 25 = 40ms
}
```

业务层通过监听`playbackTime`，`playbackTimestamp` 事件，可以监听到当前播放的时间。

### 当前录像流的播放时间

可以监听`playbackTimestamp`事件，来获取当前播放的时间。

```js
jessibucaPro.on('playbackTimestamp', (data) => {
    // data 为对象
    /**
     * {
        "hour": 1,
        "min": 2,
        "second": 0,
        "ts": "" // 时间戳
        }
     */
})
```

### 配置I帧解码

> 过滤掉其他帧，只解码I帧，可以提高播放性能。

当倍率达到播放器性能瓶颈的时候，就需要配置I帧解码，来提高播放性能。

可以通过调用`updatePlaybackForwardMaxRateDecodeIFrame`方法，来配置I帧解码。

> 播放器默认I帧解码的倍率是4倍。

```js
// 当倍率达到3倍的时候，就触发i帧解码。
jessibucaPro.updatePlaybackForwardMaxRateDecodeIFrame(3)
```

> 支持在播放前后调用，

### 播放结束

因为播放器播放的是流，所以播放器并不知道这个流的结束时间是多少。

> 播放器唯一能做的就是监听超时。

需要业务层自己结合后端接口来实现。
