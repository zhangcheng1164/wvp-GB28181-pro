import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from "../layout/index.vue"

import console from '../components/console.vue'
import deviceList from '../components/DeviceList.vue'
import channelList from '../components/channelList.vue'
import gbRecordDetail from '../components/GBRecordDetail.vue'
import pushVideoList from '../components/PushVideoList.vue'
import streamProxyList from '../components/StreamProxyList.vue'
import map from '../components/map.vue'
import login from '../components/Login.vue'
import parentPlatformList from '../components/ParentPlatformList.vue'
import cloudRecord from '../components/CloudRecord.vue'
import mediaServerManger from '../components/MediaServerManger.vue'
import web from '../components/setting/Web.vue'
import sip from '../components/setting/Sip.vue'
import media from '../components/setting/Media.vue'
import live from '../components/live.vue'
import deviceTree from '../components/common/DeviceTree.vue'
import userManager from '../components/UserManager.vue'

import wasmPlayer from '../components/common/jessibuca.vue'
import rtcPlayer from '../components/dialog/rtcPlayer.vue'

const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

Vue.use(VueRouter)


export default new VueRouter({
  mode:'hash',
  routes: [
    {
      path: '/',
      name: 'home',
      component: Layout,
      redirect: '/console',
      children: [
        {
          path: '/console',
          component: console,
          meta: {
            activePath: '/console',
            breadcrumbs: [{ label: '控制台' }]
          }
        },
        {
          path: '/live',
          component: live,
          meta: {
            activePath: '/live',
            breadcrumbs: [{ label: '分屏监控' }]
          }
        },
        {
          path: '/deviceList',
          component: deviceList,
          meta: {
            activePath: '/deviceList',
            breadcrumbs: [{ label: '国标设备' }]
          }
        },
        {
          path: '/pushVideoList',
          component: pushVideoList,
          meta: {
            activePath: '/pushVideoList',
            breadcrumbs: [{ label: '推流列表' }]
          }
        },
        {
          path: '/streamProxyList',
          component: streamProxyList,
          meta: {
            activePath: '/streamProxyList',
            breadcrumbs: [{ label: '拉流列表' }]
          }
        },
        {
          path: '/channelList/:deviceId/:parentChannelId/',
          name: 'channelList',
          component: channelList,
          meta: {
            activePath: '/deviceList',
            breadcrumbs: [{ label: '国标设备' }, { label: '通道列表' }]
          }
        },
        {
          path: '/gbRecordDetail/:deviceId/:channelId/',
          name: 'gbRecordDetail',
          component: gbRecordDetail,
          meta: {
            activePath: '/deviceList',
            breadcrumbs: [
              { label: '国标设备' },
              { label: '通道列表' },
              { label: '国标录像' }
            ]
          }
        },
        {
          path: '/parentPlatformList/:count/:page',
          name: 'parentPlatformList',
          component: parentPlatformList,
          meta: {
            activePath: '/parentPlatformList/15/1',
            breadcrumbs: [{ label: '国标级联' }]
          }
        },
        {
          path: '/map/:deviceId/:parentChannelId/:count/:page',
          name: 'map',
          component: map,
          meta: {
            activePath: '/map',
            breadcrumbs: [{ label: '电子地图' }]
          }
        },
        {
          path: '/cloudRecord',
          name: 'cloudRecord',
          component: cloudRecord,
          meta: {
            activePath: '/cloudRecord',
            breadcrumbs: [{ label: '云端录像' }]
          }
        },
        {
          path: '/mediaServerManger',
          name: 'mediaServerManger',
          component: mediaServerManger,
          meta: {
            activePath: '/mediaServerManger',
            breadcrumbs: [{ label: '节点管理' }]
          }
        },
        {
          path: '/setting/web',
          name: 'web',
          component: web,
        },
        {
          path: '/setting/sip',
          name: 'sip',
          component: sip,
        },
        {
          path: '/setting/media',
          name: 'media',
          component: media,
        },
        {
          path: '/map',
          name: 'map',
          component: map,
          meta: {
            activePath: '/map',
            breadcrumbs: [{ label: '电子地图' }]
          }
        },
        {
          path: '/userManager',
          name: 'userManager',
          component: userManager,
          meta: {
            activePath: '/userManager',
            breadcrumbs: [{ label: '用户管理' }]
          }
        }
        ]
    },
    {
      path: '/login',
      name: '登录',
      component: login,
    },
    {
      path: '/test',
      name: 'deviceTree',
      component: deviceTree,
    },
    {
      path: '/play/wasm/:url',
      name: 'wasmPlayer',
      component: wasmPlayer,
    },
    {
      path: '/play/rtc/:url',
      name: 'rtcPlayer',
      component: rtcPlayer,
    },
  ]
})
