# Live-Shop-Android

*[English](README.en.md) | 中文*

本开源示例项目演示了如何快速集成Agora视频直播SDK，实现视频直播、消息通信、答题功能。

在这个示例项目中包含了以下功能：

- 加入和离开视频通话
- 主播-观众视频直播
- 文本消息通信
- 在线语音识别
- 主播-观众答题互动

## 环境准备

- Android Studio 3.3 +
- Java JDK 1.8.0 +
- Android真机设备(部分模拟器会存在功能缺失或者性能问题，所以推荐使用真机)

## 运行示例程序

这个段落主要讲解了如何编译和运行实例程序。

### 创建Agora账号并获取AppId

在编译和启动实例程序前，您需要首先获取一个可用的App ID:
1. 在[agora.io](https://dashboard.agora.io/signin/)创建一个开发者账号
2. 前往后台页面，点击左部导航栏的 **项目 > 项目列表** 菜单
3. 复制后台的 **App ID** 并备注，稍后启动应用时会用到它
4. 在项目页面生成临时 **Access Token** (24小时内有效)并备注，注意生成的Token只能适用于对应的频道名。
5. 将 AppID 填写进 "app/src/main/res/values/strings.xml"
  ``` xml
  <string name="private_app_id"><#YOUR APP ID#></string>
  <!-- 临时Token 可以在 https://dashboard.agora.io 获取 -->
  <!-- 在正式上线生产环境前，你必须部署你自己的Token服务器 -->
  <!-- 如果你的项目没有打开安全证书，下面的值可以直接留空 -->
  <string name="agora_access_token"><#YOUR TOKEN#></string>
 ```

### 创建搜狗知音帐号并获取Appid、AppKey
在编译和启动实例程序前，您需要首先获取可用AppId、AppKey:
1. 在[zhiyin.sogou](https://zhiyin.sogou.com/login)创建一个开发者账号
2. 帐号登录成功后，点击左部导航栏的 **控制台** 菜单
3. 在控制台页面点击 **创建应用**
4. 应用创建成功后，点击应用查看 **基本信息** ，复制 **AppID** **Appkey** 并修改配置io.agora.liveshow.demo.voice.SogouVoiceChannel
``` java
    // 搜狗知音平台appid
    private static final String APP_ID = "";
    // 搜狗知音平台appkey
    private static final String APP_KEY = "";
```

### 集成 Agora 视频 SDK

在 [Agora.io SDK](https://www.agora.io/cn/download/) 下载 **视频通话 + 直播 SDK**并解压，按以下对应关系将 **libs** 目录的内容复制到项目内。
      
  SDK目录|项目目录
  ---|---
  .jar file|**/apps/libs** folder
  **arm64-v8a** folder|**/app/src/main/jniLibs** folder
  **x86** folder|**/app/src/main/jniLibs** folder
  **armeabi-v7a** folder|**/app/src/main/jniLibs** folder

详细集成方式查看：      
- [视频互动直播](https://docs.agora.io/cn/Interactive%20Broadcast/android_video?platform=Android)
- [实时消息](https://docs.agora.io/cn/Real-time-Messaging/RTM_Quickstarts_android?platform=Android)

### 启动应用程序

用 Android Studio 打开该项目，连上设备，编译并运行。

也可以使用 `Gradle` 直接编译运行。

## 联系我们

- 完整的 API 文档见 [文档中心](https://docs.agora.io/cn/)
- 如果在集成中遇到问题, 你可以到 [开发者社区](https://dev.agora.io/cn/) 提问
- 如果有售前咨询问题, 可以拨打 400 632 6626，或加入官方Q群 12742516 提问
- 如果需要售后技术支持, 你可以在 [Agora Dashboard](https://dashboard.agora.io) 提交工单
- 如果发现了示例代码的 bug, 欢迎提交 issue

## 代码许可

The MIT License (MIT)