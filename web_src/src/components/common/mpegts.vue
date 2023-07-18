<template>
  <div style="" ref="mainContainer">
      <video name="videoElement" class="centeredVideo" controls autoplay ref="videoElement" style="width: 100%; height: 100%;">
          Your browser is too old which doesn't support HTML5 video.
      </video>
  </div>
</template>

<script>
import mpegts from 'mpegts.js';

export default {
  name: 'mpegts',
  data() {
    return {
    };
  },
  props: ['videoUrl'],
  created() {
    this.player = null;
  },
  mounted() {
    this.$nextTick(() => {
      this.updatePlayerDomSize()
      window.onresize = () => {
        this.updatePlayerDomSize()
      }

      if (this.videoUrl) {
        console.log("初始化时的地址为: " + this.videoUrl)
        this.play(this.videoUrl)
      }
    })
  },
  watch: {
    videoUrl(newData, oldData) {
      console.log('--watch video url--', newData);
      this.play(newData)
    },
    immediate: true
  },
  methods: {
    updatePlayerDomSize() {
      let dom = this.$refs.mainContainer;
      let width = dom.parentNode.clientWidth
      let height = (9 / 16) * width

      const clientHeight = Math.min(document.body.clientHeight, document.documentElement.clientHeight)
      if (height > clientHeight) {
        height = clientHeight
        width = (16 / 9) * height
      }

      dom.style.width = width + 'px';
      dom.style.height = height + "px";
    },
    play: function (url) {
      console.log('isSupported: ' + mpegts.isSupported());

      var mediaDataSource = {
          // type: 'flv',
          type: 'mse',
          isLive: true,
          withCredentials: false,
          liveBufferLatencyChasing: true,
          // url: 'http://192.168.151.25:80/rtp/37010100001180000001_37010100001310000001.live.flv',
          url,
      };

      console.log('MediaDataSource', mediaDataSource);

      var element = this.$refs.videoElement;
      // var element = document.getElementsByName('videoElement')[0];
      if (this.player != null) {
          this.player.unload();
          this.player.detachMediaElement();
          this.player.destroy();
          this.player = null;
      }

      this.player = mpegts.createPlayer(mediaDataSource, {
          enableWorker: true,
          lazyLoadMaxDuration: 3 * 60,
          seekType: 'range',
          liveBufferLatencyChasing: true,
          deferLoadAfterSourceOpen: false, // 浏览器后端运行，依旧播放视频
      });
      this.player.attachMediaElement(element);
      this.player.load();
    },
  },
  destroyed() {
    if (this.player != null) {
      console.log('---destory---', this.videoUrl);
      this.player.unload();
      this.player.detachMediaElement();
      this.player.destroy();
      this.player = null;
    }
  },
}
</script>

<style>
.buttons-box {
  width: 100%;
  height: 28px;
  background-color: rgba(43, 51, 63, 0.7);
  position: absolute;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  left: 0;
  bottom: 0;
  user-select: none;
  z-index: 10;
}

.jessibuca-btn {
  width: 20px;
  color: rgb(255, 255, 255);
  line-height: 27px;
  margin: 0px 10px;
  padding: 0px 2px;
  cursor: pointer;
  text-align: center;
  font-size: 0.8rem !important;
}

.buttons-box-right {
  position: absolute;
  right: 0;
}
</style>
