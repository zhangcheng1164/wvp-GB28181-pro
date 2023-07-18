<template>
  <el-container>
    <el-header
      style="padding: 0; font-size: 12px; height: 50px; background: #fff; display: flex;"
    >
      <div
        style="background: #001529; width: 200px; color: #fff; line-height: 50px; font-size: 24px; font-weight: bold;"
      >
        <img :src="dashLogo" style="width: 90%; margin-top: 9px;"/>
      </div>
      <div
        style="border-bottom: 1px solid #eee; width: calc(100vw - 200px); display: flex; align-items: center; padding-left: 20px;"
      >
        <el-breadcrumb separator="/">
          <el-breadcrumb-item
            :to="{ path: breadcrumb.path }"
            v-for="breadcrumb of breadcrumbs"
            :key="breadcrumb.label"
          >
            {{ breadcrumb.label }}
          </el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </el-header>

    <el-container style="height: calc(100vh - 50px)">
      <el-aside width="198px" style="background-color: #001529;">
        <ui-header />
      </el-aside>
      <el-main style="padding: 0 !important">
        <transition name="fade">
          <router-view></router-view>
        </transition>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import uiHeader from "./UiHeader.vue";
import dashLogo from "../assets/dash_logo.png";

export default {
  name: "index",
  components: {
    uiHeader,
  },
  data() {
    return {
      breadcrumbs: [],
      dashLogo,
    };
  },
  mounted() {
    this.breadcrumbs = this.$route.meta.breadcrumbs || [];
  },
  watch: {
    $route() {
      this.breadcrumbs = this.$route.meta.breadcrumbs || [];
    }
  }
};
</script>
<style>
body{
  font-family: sans-serif;
}
/*定义标题栏*/
.page-header {
  background-color: #FFFFFF;
  margin-bottom: 1rem;
  padding: 0.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-weight: bold;
  text-align: left;
}

.page-header-btn {
  text-align: right;
}
</style>
<style scoped>
.fade-enter {
  visibility: hidden;
  opacity: 0;
}

.fade-leave-to {
  display: none;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity .5s ease;
}

.fade-enter-to,
.fade-leave {
  visibility: visible;
  opacity: 1;
}
</style>
