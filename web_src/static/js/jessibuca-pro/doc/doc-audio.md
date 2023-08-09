## 断网重连

只需要监听`playFailedAndPaused`事件，当播放器播放失败或者暂停的时候，重新调用`play`方法即可

```js
jessibucaPro.on('playFailedAndPaused', () => {
    jessibucaPro.play('url');
})
```
