## 2023年6月
- 【🐛 bug】修复`mes`,`wcs`硬解码，长时间（30分钟）最小化窗口，导致的播放延迟。
-------
- 【💡优化】优化`hasLoaded()`方法和`load`事件，`hasLoaded()`永久返回true。业务层不需要通过判断`hasLoaded()`和监听`load`事件来调用播放器的`play()`方法
- 【💡优化】优化代码逻辑，提高健壮性。
- 【💡优化】优化`EVENTS`事件抛出，支持多个参数。
- 【💡优化】优化`wcs`使用`canvas`渲染，优先使用webgl2渲染，不支持降级到`canvas`渲染
- 【💡优化】优化打包的`browserslist`配置。
- 【💡优化】优化`checkFirstIFrame`参数在 `mse` 和 `wcs` 下面的处理逻辑。
- 【💡优化】优化http发起`fetch`请求在硬解码下面的处理逻辑。（修复user abort a request 错误情况）
-------
- 【🚀 功能】添加`isFmp4Private`参数，支持私有协议播放（支持定制私有协议）。
- 【🚀 功能】添加`isWebrtcForZLM`参数，支持`zlm`的`webrtc`播放。
- 【🚀 功能】添加 `wasm simd`解码出错拦截，当出现持续2个gop还是连续解码报错，则会触发重播机制。
- 【🚀 功能】添加`streamEnd`事件，当流播放结束(异常)的时候，会触发该事件。
- 【🚀 功能】回放(TF卡)流支持`硬解码(webcodec)`播放，需要在`playbackConfig`里面配置`useWCS`参数。
- 【🚀 功能】语音通讯支持`packetType:empty`传输裸流数据。
- 【🚀 功能】添加`updateLoadingText(text)`方法，支持更新loading文案。
- 【🚀 功能】支持 mac(16.4) `safari` 和 ios(16.4) `safari` `webcodec` 支持 `H264` 硬解码。
- 【🚀 功能】添加`isEmitSEI`参数，支持`SEI`数据抛出。
- 【🚀 功能】添加`updateIsEmitSEI`方法，来更新`isEmitSEI`参数，支持播放过程中解析`SEI`数据。
- 【🚀 功能】添加`videoSEI`事件，监听`SEI`数据。
- 【🚀 功能】添加`pauseAndNextPlayUseLastFrameShow` 参数，支持调用 `pause()` 方法之后，再次调用`play()`方法，在加载画面的时候，显示上一帧画面，而不是黑屏。
- 【🚀 功能】添加`videoElementPlayingFailed`事件，支持`video`元素播放失败的时候，降级到 `canvas` 渲染

## 2023年5月
- 【🐛 bug】修复webcodec解码config不支持的时候没有降级到wasm解码，添加`webcodecsUnsupportedConfigurationError`事件
-------
- 【💡优化】优化性能面板数据（在使用video渲染的时候，可以统计到丢帧情况）。
- 【💡优化】优化日志显示，方便更好的定位问题。
- 【💡优化】优化`error事件`抛出。
- 【💡优化】监听`wasm使用video渲染`异常（浏览器版本过低不支持该特性），降级到`canvas渲染`。
- 【💡优化】优化`heartTimeout检测机制`（之前版本存在窗口`visibility` 为 `false`的时候检测不准确）
- 【💡优化】优化`stats事件`的`pTs`字段（在断流过程中也在增加）。
- 【💡优化】添加 `new` 的时候，`container` 如果已经被初始化过了，会报错不允许重复初始化。
- 【💡优化】添加 `playback-api.md`文件，将回放流的api单独列出来。
- 【💡优化】添加 `question.md`，`question-audio`文件，将问题这类的单独列出来。
-------
- 【🚀 功能】添加`isH265`参数，来兼容如果配置了优先硬解码，如果本身浏览器不支持硬解码265的情况下，会重新降级到wasm（simd）软解码。
- 【🚀 功能】添加`getVideoInfo() `方法(在监听videoInfo事件之后调用才会有数据，否则返回null)。
- 【🚀 功能】添加`getAudioInfo()` 方法(在监听audioInfo事件之后调用才会有数据，否则返回null)。
- 【🚀 功能】添加`ptzMoreArrowShow`,`ptzStopEmitDelay`，`ptzZoomShow`，`ptzApertureShow`，`ptzFocusShow` 参数，扩充ptz指令集。
- 【🚀 功能】添加单独的音频解码器（`decoder-pro-audio.js`，`decoder-pro-audio.wasm`），减少硬解码多屏情况下内存占用情况。
- 【🚀 功能】添加`isDecoderUseCDN`参数，支持配置decoder CDN绝对地址。
- 【🚀 功能】添加纯音频播放器(`jessibuca-pro-audio-player.js`)，支持移动端（平板端）息屏和后台播放。
- 【🚀 功能】添加`supportHls265`参数，支持 `hls 265` 格式播放。可以通过设置`false`降级到稳定的`hls 264`播放版本。
- 【🚀 功能】添加`isFmp4`参数，支持 `fmp4(264/265)` 格式播放。
- 【🚀 功能】添加`isMpeg4`参数，支持 `mpeg4` 格式播放。

## 2023年4月
- 【🐛 bug】修复触发`crashLog`事件的时候，`videoInfo`和`audioInfo`数据没有采集到。
- 【🐛 bug】修复解析flv协议的时候，拼接时间戳漏了扩展时间戳位。
- 【🐛 bug】修复`mse解码`的时候，更新`stats`里面的`ts`字段错误。
- 【🐛 bug】针对于`mse use canvas render` 在自动播放的时候，会报” play() failed because the user didn't interact with the document first“ 错误，现在监听这个异常，并pause掉播放器，并抛出‘mediaSourceUseCanvasRenderPlayFailed’异常。- 【浏览器不允许没有任何用户交互的情况下，触发video标签的play方法】，交互上面需要引导客户手动触发视频播放。
- 【🐛 bug】修复手机端播放纯音频数据的时候灭屏或者后台运行就没有声音的问题。
- 【🐛 bug】修复`multi多屏模式下`，触发播放器底部操作栏close按钮，导致该窗口没法再次播放新的地址。
- 【🐛 bug】修复录播流模式下，`updatePlaybackForwardMaxRateDecodeIFrame() `方法在触发playback方法前调用没有生效的问题。
- 【🐛 bug】修复在某些情况下，频繁调用`pause-play-pause`会有失败的可能。（destroy异步化导致的）。
-------

- 【💡优化】优化录播流在切换倍率的时候，没有第一时间通知到worker线程（因为js堵塞），导致出现了延迟。
- 【💡优化】水印支持其他矩形窗和自定义HTML内容，并支持数组配置多个水印内容。
- 【💡优化】在ios手机端，播放音频数据，默认使用’script‘ 引擎。
- 【💡优化】添加参数为undefined校验，如参数有非法的则直接抛出Error异常。
- 【💡优化】优化录像流性能面板显示数据。
- 【💡优化】压缩优化图片资源，减少打包大小。
-------

- 【🚀 功能】ptz操作添加镜头拉近、拉远操作按钮，通过ptzZoomShow参数配置生效。
- 【🚀 功能】ptz操作添加左上，右上，左下，右下方向按钮，通过ptzMoreArrow参数配置生效。
- 【🚀 功能】添加webglContextLost 事件监听，并尝试恢复webgl上下文，如果失败触发重新播放（开放了webglContextLostErrorReplay参数，默认true）。
- 【🚀 功能】支持webgpu渲染canvas（需要chrome canary 版本，然后设置chrome://flags/#enable-unsafe-webgpu 打开）。
- 【🚀 功能】支持配置底部控制栏添加HTML内容。
- 【🚀 功能】支持播放过程中动态切换分辨率。
- 【🚀 功能】multi多屏支持3-1，4-1 这种不规则布局。
- 【🚀 功能】multi多屏添加supportDblclickContainerFullscreen 参数。
- 【🚀 功能】添加audioResumeState事件，当play()或audioResume()进行音频恢复或者setVolume()更新音量时回调。
- 【🚀 功能】添加websocketClose事件。
- 【🚀 功能】优化代码逻辑，提升代码健壮性。
- 【🚀 功能】更新文档。


