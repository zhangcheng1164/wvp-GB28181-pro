
# 纯音频播放器 API

## JessibucaProAudio(options)


options 支持的参数有：

### decoder

- **类型**：`string`
- **默认值**：`decoder-pro-audio-player.js`
- **用法**：

worker地址

> 默认引用的是根目录下面的decoder-pro-audio-player.js文件 ，decoder-pro-audio-player.js 与 decoder-pro-audio.wasm文件必须是放在同一个目录下面。


### debug

同 [JessibucaProVideo](./api.md#debug)

### debugLevel

同 [JessibucaProVideo](./api.md#debuglevel（pro）)

### isFlv

同 [JessibucaProVideo](./api.md#isflv)

### isNotMute

同 [JessibucaProVideo](./api.md#isnotmute)
### videoBuffer

同 [JessibucaProVideo](./api.md#videobuffer)
### loadingTimeout

同 [JessibucaProVideo](./api.md#loadingtimeout)
### heartTimeout

同 [JessibucaProVideo](./api.md#hearttimeout)
### loadingTimeoutReplay
同 [JessibucaProVideo](./api.md#loadingtimeoutreplay)

### heartTimeoutReplay

同 [JessibucaProVideo](./api.md#hearttimeoutreplay)

### loadingTimeoutReplayTimes

同 [JessibucaProVideo](./api.md#loadingtimeoutreplaytimes)

### heartTimeoutReplayTimes

- **类型**：`number`
- **默认值**：`3`
- **用法**： heartTimeoutReplay 重试次数

同 [JessibucaProVideo](./api.md#hearttimeoutreplaytimes)

### weiXinInAndroidAudioBufferSize

同 [JessibucaProVideo](./api.md#weixininandroidaudiobuffersize)

### audioEngine

同 [JessibucaProVideo](./api.md#audioengine)

### supportLockScreenPlayAudio

同 [JessibucaProVideo](./api.md#supportlockscreenplayaudio)

### isDecoderUseCDN

同 [JessibucaProVideo](./api.md#isdecoderusecdn)

## 事件


### timeUpdate

同 [JessibucaProVideo](./api.md#timeupdate)

### audioInfo

同 [JessibucaProVideo](./api.md#audioinfo)


### error

错误信息

目前已有的错误信息：

- jessibucaPro.ERROR.playError ;播放错误，url 为空的时候，调用play方法
- jessibucaPro.ERROR.fetchError ;http 请求失败
- jessibucaPro.ERROR.websocketError; websocket 请求失败
- jessibucaPro.ERROR.audioChannelError ;音频通道错误

```js

jessibucaPro.on("error", function (error) {
    if (error === jessibucaPro.ERROR.fetchError) {
        //
    } else if (error === jessibucaPro.ERROR.webcodecsH265NotSupport) {
        //
    }
    console.log('error:', error)
})
```

### kBps

同 [JessibucaProVideo](./api.md#kbps)

### loadingTimeout

同 [JessibucaProVideo](./api.md#loadingtimeout)
### loadingTimeoutRetryEnd

同 [JessibucaProVideo](./api.md#loadingtimeoutretryend)

### delayTimeout

同 [JessibucaProVideo](./api.md#delaytimeout)

### delayTimeoutRetryEnd

同 [JessibucaProVideo](./api.md#delaytimeoutretryend)
### play

同 [JessibucaProVideo](./api.md#play)
### pause
同 [JessibucaProVideo](./api.md#pause)
### mute
同 [JessibucaProVideo](./api.md#mute)
### stats
同 [JessibucaProVideo](./api.md#stats)
### crashLog

触发播放器重播的时候，会抛出错误信息，方便业务层做错误上报

主要收集的数据有

- url： 播放地址
- type: 错误的类型
- error： 错误信息
- playType ： 播放类型
- demuxType：解封装类型
- audioInfo : 音频信息 {encType,channels,sampleRate}
- audioEngine ：音频引擎

```js
jessibucaPro.on('crashLog', (data) => {
    console.log('crashLog is', data);
})
```

### websocketOpen

同 [JessibucaProVideo](./api.md#websocketopen)
### websocketClose
同 [JessibucaProVideo](./api.md#websocketclose)

### playFailedAndPaused

同 [JessibucaProVideo](./api.md#playfailedandpaused)

### audioResumeState

同 [JessibucaProVideo](./api.md#audioresumestate)
## 方法

### play([url])
- **参数**：
    - `{string} url`

- **返回**：
    - `{Promise}`
- **用法**： 播放音频

```js

jessibucaPro.play('url').then(() => {
    console.log('play success')
}).catch((e) => {
    console.log('play error', e)
})
//
jessibucaPro.play().then(() => {
    console.log('play success')
}).catch((e) => {
    console.log('play error', e)
})
```

### setDebug(flag)
同 [JessibucaProVideo](./api.md#setdebugflag)
### updateDebugLevel(level)
同 [JessibucaProVideo](./api.md#updatedebuglevellevel)
### mute()
同 [JessibucaProVideo](./api.md#mute)

### cancelMute()
同 [JessibucaProVideo](./api.md#cancelmute)
### pause()
同 [JessibucaProVideo](./api.md#pause)

### setVolume(value)
同 [JessibucaProVideo](./api.md#setvolumevalue)

### getVolume()
同 [JessibucaProVideo](./api.md#getvolume)
### audioResume()
同 [JessibucaProVideo](./api.md#audioresume)
