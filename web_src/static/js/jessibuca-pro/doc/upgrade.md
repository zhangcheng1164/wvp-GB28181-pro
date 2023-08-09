# 开源版 升级到 Pro 版本

## 替换文件

需要将原本开源版的

- jessibuca.js
- decoder.js
- decoder.wasm

替换成

- jessibuca-pro.js（web-player-pro.js） 或 jessibuca-pro-multi.js（web-player-pro-multi.js）   // 主文件(需要通过script标签引入)

- decoder-pro.js  //  worker解码器(这个库无需引入)
- decoder-pro.wasm // worker解码器胶水文件(这个库无需引入)

- decoder-pro-simd.js // worker-simd解码器(这个库无需引入)
- decoder-pro-simd.wasm // worker-simd解码器胶水文件(这个库无需引入)

- decoder-pro-hard.js // worker硬解码解封装+audio解码(这个库无需引入)
- decoder-pro-audio.js // worker音频解码器(这个库无需引入)
- decoder-pro-audio.wasm // worker音频解码器胶水文件(这个库无需引入)

> 如果有多屏需求，可以将`jessibuca-pro.js`替换成`jessibuca-pro-multi.js`。

> `jessibuca-pro.js` 与`jessibuca-pro-multi.js`只需要引用一个即可（不需要两个同时引用）。

> `decoder-pro-simd.js`是simd解码器（适用于高分辨率解码）

## 替换方法

将原本 `new Jessibuca()` 的地方替换成 `new JessibucaPro()`

```js
// 原本的
const jessibuca = new Jessibuca({
    // ...
})
```

```js
// 替换成
const jessibucaPro = new JessibucaPro({
    // ...
})
```
```js
// 替换成 多屏
const jessibucaProMulti = new JessibucaProMulti({
    // ...
})
```


需要将原本开源版的 `destroy()` 从同步方法，修改为异步方法

```js
// 原本的
jessibuca.destroy()
```

```js

// 替换成
await jessibucaPro.destroy()
// 或者
jessibucaPro.destroy().then(() => {
    // ...
})
```

将原本的判断 `hasLoaded()` 和监听`load`事件

```js
// 原本的
if (jessibuca.hasLoaded()) {
    // ...
    jessibuca.play('url');
} else {
    jessibuca.on(Jessibuca.EVENTS.load, () => {
        // ...
        jessibuca.play('url');
    })
}
```

```js
// 替换成
jessibuca.play('url');
```



### vue 里面

在组件里面的`beforeDestroy`里面调用`destroy`方法

```js

// 原本的
beforeDestroy() {
    this.jessibuca.destroy()
}

// 替换成
async beforeDestroy() {
    await this.jessibucaPro.destroy()
}
```


### react 里面

在组件里面的`componentWillUnmount`里面调用`destroy`方法

```js

// 原来的
componentWillUnmount() {
    this.jessibuca.destroy()
}

// 替换成
async componentWillUnmount() {
    await this.jessibucaPro.destroy()
}

```


## 服务器配置wasm支持

最新版本的chrome（version >= 112）貌似对于wasm 格式返回 `application/otct-stream` 的文件会报错，需要服务器配置支持


### nginx
#### 方法一：（推荐）
在 `mime.types` 文件中添加

```nginx
application/wasm wasm;
```

#### 方法二：

```nginx
location ~* \.wasm$ {
    add_header Content-Type application/wasm;
}
```

### IIS

在 `web.config` 文件中添加

```xml
<staticContent>
    <mimeMap fileExtension=".wasm" mimeType="application/wasm" />
</staticContent>
```

### apache

在 `httpd.conf` 文件中添加

```apache
AddType application/wasm .wasm
```



