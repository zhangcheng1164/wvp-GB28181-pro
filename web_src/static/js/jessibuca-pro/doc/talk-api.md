# 语音通讯 API

## JessibucaProTalk(options)

options 支持的参数有：

### encType

- **类型**：`string`
- **默认值**：`g711a`
- **用法**：

语音编码类型，支持`g711a`和`g711u`和`pcm`，默认是`g711a`

> g711a/g711u 格式的 8k采样率，16位精度，单声道


> 当格式选为g711格式的，如果采样率和，采用精度不是 `8k采样率，16位精度` 则会抛出警告。


### packetType

- **类型**：`string`
- **默认值**：`rtp`
- **用法**：

语音包类型，支持`rtp`，`empty`，默认是`rtp`


> rtp 封装格式是 `[2]`+`[12]`+`[裸数据]`

`[2]`: 2字节的rtp头(音频裸数据长度+12个字节的rtp扩展头)

`[12]`: 12字节的rtp扩展头

`[裸数据]`: 语音裸数据

> empty 封装格式是 `[裸数据]`


> `rtp` 仅支持`g711a`和`g711u`格式的封装。

> `empty` 仅支持所有格式的封装。


### rtpSsrc

- **类型**：`string`

- **用法**：

rtp包的ssrc，10位

### numberChannels

- **类型**：`number`
- **默认值**：`1`
- **用法**：

采样通道

### sampleRate

- **类型**：`number`
- **默认值**：`8000`
- **用法**：
  采样率


> g711a/g711u 格式的 `8`k采样率，`16位`精度，单声道

> pcm裸流数据，支持任意格式的采样率

### sampleBitsWidth

- **类型**：`number`
- **默认值**：`16`
- **用法**：
  采样精度，支持`8` 、`16` 、`32`


> g711a/g711u 格式的 `8k`采样率，`16`位精度，单声道

> pcm裸流数据，支持任意格式的精度


### debug

- **类型**：`boolean`
- **默认值**：false
- **用法**：
  是否开启debug模式

### debugLevel

- **类型**：`string`
- **默认值**：`warn`
- **用法**：

debug模式下的日志级别，支持`debug`、`warn`，默认是`warn`

> error 的信息默认就是会输出出来的。

### testMicrophone

- **类型**：`boolean`
- **默认值**：false
- **用法**：
  是否开启测试麦克风,不连接ws

### engine

- **类型**：`string`
- **默认值**：`worklet`
- **用法**：

语音引擎，支持`worklet`和`script`，默认是`worklet`

### checkGetUserMediaTimeout

- **类型**：`boolean`
- **默认值**：false
- **用法**：
  是否开启检测getUserMedia超时

### getUserMediaTimeout

- **类型**：`number`
- **默认值**：`10 * 1000`
- **用法**：

getUserMedia超时时间，单位ms

### saveRtpToFile

- **类型**：`boolean`
- **默认值**：false
- **用法**：
  是否保存成文件

> 仅供测试下载`rtp`文件使用，不要在生产环境使用，不然会在客户端内存里面保存大量的文件，导致内存溢出。

## 方法

### startTalk(wsUrl, options)

- **参数**：
    - `{string} wsUrl`
    - `{object} options`
- **用法**： 开启语音
- **返回值**：`Promise`

> options 参数通初始化 new JessibucaProTalk(options) 时的参数一致

### stopTalk()

- **用法**： 关闭语音
- **返回值**：`Promise`

### getTalkVolume()

- **用法**： 获取语音音量
- **返回值**：`Promise(number)`

> 返回值是一个0-100的数字，表示当前语音音量

### setTalkVolume(volume)

- **用法**： 设置语音音量
- **返回值**：`Promise`
- **参数**：
    - `{number} volume` 0-100的数字，表示当前语音音量

## 事件

### talkStreamClose

语音通讯websocket的 close 事件

> 内部会调用`stopTalk()`方法

### talkStreamError

语音通讯websocket的 error 事件

> 内部会调用`stopTalk()`方法


### talkStreamInactive

语音通讯，切换音频输入源。

> 内部会调用`stopTalk()`方法


### talkGetUserMediaTimeout

检测到getUserMedia超时时间超过`getUserMediaTimeout` 之后触发。

> 内部会调用`stopTalk()`方法


### talkFailedAndStop

语音通讯失败，停止语音通讯。
