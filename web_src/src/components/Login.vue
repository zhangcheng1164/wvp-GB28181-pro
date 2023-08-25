<template>
  <div class="login_box">
    <div class="login_l_img"><img :src="loginImg" /></div>
    <div class="login">
      <div class="login_logo">
        <a href="#"><img :src="loginLogo"/></a>
      </div>
      <div>
        <input
          name="username"
          type="text"
          placeholder="用户名"
          v-model="username"
        />
        <input
          name="password"
          type="password"
          id="password"
          v-model="password"
          placeholder="密码"
        />
        <input value="登录" style="width:100%;" type="button" @click="login" />
      </div>
    </div>
  </div>
</template>

<script>
import crypto from 'crypto'
import userService from "./service/UserService";
import loginImg from "../assets/login_img.png";
import loginLogo from "../assets/login_logo.png";

export default {
  name: 'Login',
  data(){
    return {
      isLoging: false,
      showPassword: false,
      loginLoading: false,
      username: '',
      password: '',
      loginLogo,
      loginImg
    };
  },
  created(){
    var that = this;
    document.onkeydown = function(e) {
      var key = window.event.keyCode;
      if (key == 13) {
        that.login();
      }
    }
  },
  methods:{

    //登录逻辑
  	login(){
  		if(this.username!='' && this.password!=''){
        this.toLogin();
      }
    },

    //登录请求
  	toLogin(){
      //需要想后端发送的登录参数
      let loginParam = {
        username: this.username,
  			password: crypto.createHash('md5').update(this.password, "utf8").digest('hex')
  		}
      var that = this;
      //设置在登录状态
      this.isLoging = true;
      let timeoutTask = setTimeout(()=>{
        that.$message.error("登录超时");
        that.isLoging = false;
      }, 1000)

      this.$axios({
      	method: 'get',
        url:"/api/user/login",
        params: loginParam
      }).then(function (res) {
        window.clearTimeout(timeoutTask)
          console.log(res);
          console.log("登录成功");
          if (res.data.code === 0 ) {
            userService.setUser(res.data.data)
            //登录成功后
            that.cancelEnterkeyDefaultAction();
            that.$router.push('/');
          }else{
            that.isLoging = false;
            that.$message({
              showClose: true,
                  message: '登录失败，用户名或密码错误',
                  type: 'error'
            });
          }
      }).catch(function (error) {
        console.log(error)
        window.clearTimeout(timeoutTask)
          that.$message.error(error.response.data.msg);
          that.isLoging = false;
        });
    },
    cancelEnterkeyDefaultAction: function() {
      document.onkeydown = function(e) {
        var key = window.event.keyCode;
        if (key == 13) {
          return false;
        }
      };
    }
  }
};
</script>
<style scoped>
* {
  font: 13px/1.5 "微软雅黑";
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
  -box-sizing: border-box;
  padding: 0;
  margin: 0;
  list-style: none;
  box-sizing: border-box;
}
a {
  color: #27a9e3;
  text-decoration: none;
  cursor: pointer;
}
img {
  border: none;
}

.login_box {
  width: 1100px;
  margin: 0px auto;
  position: relative;
  top: calc(50vh - 250px);
}
.login_box .login_l_img {
  float: left;
  width: 432px;
  height: 440px;
  margin-left: 50px;
}
.login_box .login_l_img img {
  width: 500px;
  height: 440px;
}
.login {
  height: 360px;
  width: 400px;
  padding: 50px;
  padding-top: 15px;
  background-color: #ffffff;
  border-radius: 6px;
  box-sizing: border-box;
  float: right;
  margin-right: 50px;
  position: relative;
  margin-top: 50px;
}
.login_logo {
  text-align: center;
  line-height: 110px;
}
.login_name {
  width: 100%;
  float: left;
  text-align: center;
  margin-top: 20px;
}
.login_name p {
  width: 100%;
  text-align: center;
  font-size: 18px;
  color: #444;
  padding: 10px 0 20px;
}
.login_logo img {
  width: 260px;
  display: inline-block;
  vertical-align: middle;
}
input[type="text"],
input[type="file"],
input[type="password"],
input[type="email"],
select {
  border: 1px solid #dcdee0;
  vertical-align: middle;
  border-radius: 3px;
  height: 50px;
  padding: 0px 16px;
  font-size: 14px;
  color: #555555;
  outline: none;
  width: 100%;
  margin-bottom: 15px;
  line-height: 50px;
  color: #888;
}
input[type="text"]:focus,
input[type="file"]:focus,
input[type="password"]:focus,
input[type="email"]:focus,
select:focus {
  border: 1px solid #27a9e3;
}
input[type="submit"],
input[type="button"] {
  display: inline-block;
  vertical-align: middle;
  padding: 12px 24px;
  margin: 0px;
  font-size: 16px;
  line-height: 24px;
  text-align: center;
  white-space: nowrap;
  vertical-align: middle;
  cursor: pointer;
  color: #ffffff;
  background-color: #27a9e3;
  border-radius: 3px;
  border: none;
  -webkit-appearance: none;
  outline: none;
  width: 100%;
}
</style>
