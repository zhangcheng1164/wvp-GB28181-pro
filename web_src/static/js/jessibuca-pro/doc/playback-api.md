# 播放器（回放流/录像流） API

录像流特有的方法和可监听的事件。

## 参数

### playbackForwardMaxRateDecodeIFrame

- **类型**：`number`
- **默认值**：`4`
- **用法**：录像流播放的时候，当倍率达到多少之后，直接只解码I帧数据。

> 默认是达到4倍率的时候，就直接只解码I帧数据。

### playbackConfig

- **类型**：`object`
- **默认值**：`{}`
- **参数**：TF卡流（录像流）播放配置
    - `{array} playList` 底部UI 24小时高亮时间端，如在该时间段内，可触发点击事件
        - `{number} start`  开始时间戳 例如 1653841634 或者 16538416340000
        - `{number} end`  结束时间戳 例如 1653843420 或者 16538434200000
        - `{string|number|array|object} more`  自定义扩展数据，会在点击事件回调中返回
    - `{number} fps`  默认值`25` 渲染FPS(如果不设置，播放器会根据流数据计算出fps),
    - `{number} isCacheBeforeDecodeForFpsRender` 默认值`false` 是否在解码前缓存数据
    - `{boolean} showRateBtn`   默认值 `true` 是否显示倍速播放按钮
    - `{array} rateConfig`   默认值 `[]` 倍速播放配置
        - `{number} value`  倍速播放倍数
        - `{string} label`  倍速播放按钮显示的文字
    - `{boolean} showControl`   默认值 `true` 是否显示底部UI 24小时控制条
    - `{boolean} uiUsePlaybackPause` 默认值 `false`  ui上面是否使用 playbackPause 方法
    - `{boolean } isUseFpsRender` 默认值 `false` 是否使用固定的fps渲染，如果设置的fps小于流推过来的，会造成内存堆积甚至溢出
    - `{boolean } isUseLocalCalculateTime` 默认值 `false` 是否使用本地时间来计算playback时间
    - `{number} localOneFrameTimestamp`  默认值`40` 一帧 40ms, isUseLocalCalculateTime 为 true
      生效。（不适合高倍率I帧渲染的场景,当切换成只解码I帧的时候需要更新这个时间戳）。
  - `{boolean} useWCS` 默认值`false` 使用wcs硬解码播放。

- **用法**： TF卡流时间端配置

例如

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
    ],
    fps: 25,
    showControl: true,
    uiUsePlaybackPause: true,
    isCacheBeforeDecodeForFpsRender: false,
    isUseFpsRender: false,
    isUseLocalCalculateTime: false,
    localOneFrameTimestamp: 40
})
```

> 其他不在时间段的区域，显示是表示没有数据段。不绑定点击时间回调事件。其他在时间区域内的区间点击是有事件响应的。

> isCacheBeforeDecodeForFpsRender 为 true 时，会在解码前缓存数据，针对于有些特殊的流，在原本播放器只需要一倍率播放的情况下，
> 但是服务器端推流是2倍或者以上，这种建议设置为true，防止解码之后缓存，导致内存溢出。

## 方法
### playback(url,options)

- **参数**：
    - `{string} url`
    - `{object} options` 同 `playbackConfig`配置参数

- **返回**：
    - `{Promise}`
- **用法**： 播放录像流视频

### forward(rate)

- **参数**：
    - `{number} rate`

- **返回**：
    - `{Promise}`
- **用法**： 快放 1倍，2倍，4倍，8倍,16倍,32倍,支持范围 0.1 - 32

### playbackForward(rate)

等同于 `forward(rate)` 方法

> 如果配置了`rateConfig`参数，UI也会同步跟着修改倍数显示。

### normal()

- **返回**：
    - `{Promise}`
- **用法**：快放->恢复

```js
jessibuca.normal().then(() => {
    console.log('normal')
}).catch((err) => {
    console.log(err)
})
```

### playbackNormal()

等同于 `normal` 方法

### playbackPause(isPause)

- **参数**：
    - `{boolean} isPause` 默认：`false`
- **返回**：
    - `{Promise}`

- **用法**： 录像流暂停，只是停止渲染画面，继续接收流数据，不触发超时机制

如果 `playbackPause(true)` 等同于调用`pause()` 方法

### playbackResume()

- **返回**：
    - `{Promise}`

- **用法**： 录像流暂停->恢复播放

### updatePlaybackForwardMaxRateDecodeIFrame(rate)

- **参数**：
    - `{number} rate` 支持范围 1 - 8 整数
- **返回**：
    - `{void}`
      更新TF卡流只解码i帧播放倍率，支持playback()之前调用。

### setPlaybackStartTime(timestamp)

- **参数**：
    - `{number} timestamp` 时间戳，针对于24H的时间戳，
- **返回**：
    - `{void}`

请求完服务器端seek之后，把seek之后的时间传递给播放器，用于UI上面展示更新之后的时间。

如果是通过点击UI上面的时间，会得到时分秒，然后转换成时间戳`const timestamp = new Date().setHours(hour,min,second,0)`，然后调用`setPlaybackStartTime(timestamp)`方法。

> 会存在一种场景，就是seek之后，流媒体服务器端还有一小戳的seek之前的数据推给前端，因为前端24H UI 上面的时间戳是根据流里面的时间戳做`当对值`得到的。
> 所以如果立马触发`setPlaybackStartTime`方法的话，会就有可能因为还是seek之前的时间戳，等seek之后的时间戳到来的时候，UI上面的时间会直接跳的不对。

解决方案：延迟些时间去触发`setPlaybackStartTime`方法。大概500ms左右。

```js
setTimeout(() => {
    jessibucaPro.setPlaybackStartTime(timestamp)
}, 500)
```

### getPlaybackCurrentRate()

- **返回**：
    - `{number}`

获取当前TF卡流播放的倍率。

### playbackClearCacheBuffer()

- **返回**：
    - `{void}`

清除缓存的数据，用于seek之后，清除之前的数据。

### updatePlaybackLocalOneFrameTimestamp(timestamp)

- **参数**：
    - `{number} timestamp`
- **返回**：
    - `{void}`

更新`localOneFrameTimestamp` 字段

> 当播放器以倍率播放的时候，如果超过了`playbackForwardMaxRateDecodeIFrame`
> 设置的倍率，这个时候只会解码i帧的数据。就会导致`localOneFrameTimestamp`字段不准确。

> 所以当切换倍率超过限制之后，就需要触发这个方法，去更新`localOneFrameTimestamp`字段。

### isPlaybackPause()

- **返回值**：`boolean`
- **用法**： 返回是否正在回放暂停中状态。

```js
var result = jessibucaPro.isPlaybackPause()
```

## 事件

### playbackSeek

当点击播放器上面的时间进度条，响应的事件

数据结构如下。

```json
{
    "hour": 1,
    "min": 2,
    "second": 0,
    "more": "自定义扩展数据"
}

```

### playbackStats

录像流的 stats数据,1s回调一次

```json
{
    "fps": "",
    "rate": "",
    "start": "",
    "end": "",
    "timestamp": "",
    "dataTimestamp": "",
    "audioBufferSize": "",
    "videoBufferSize": "",
    "ts": ""
}
```

### playbackTimestamp

录像流的当前播放的时间,1s回调一次

```json
{
    "hour": "",
    "min": "",
    "second": "",
    "ts": ""
}
```

> 默认的时间是从`00:00:00`开始的，如果配置了`playbackList`,则会从`playbackList`的第一个开始

### playbackPauseOrResume

录像流的ui配置了 playbackPause 方法之后，当触发 playbackPause方法，会触发事件，方便业务层做与服务器端通讯

```js
jessibucaPro.on('playbackPauseOrResume', (value) => {
    if (value === true) {
        // pause 播放 -> 暂停
    } else {
        //  resume 暂停 -> 播放
    }
})
```

### playbackPreRateChange

录像流的ui配置了`rateConfig` 之后 ，当在ui上面选择了倍率之后，会触发`playbackPreRateChange`事件，方便业务层做与服务器端通讯。

```js

jessibucaPro.on('playbackPreRateChange', (value) => {
    // value 为当前的倍速
    // 可以与服务器端发送倍率的请求。
    // 然后更新播放器的倍率显示
})
```

### playbackRateChange

当播放器的倍率发生变化的时候，会触发`playbackRateChange`事件。


```js

jessibucaPro.on('playbackRateChange', (value) => {

})

