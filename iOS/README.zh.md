# iOS Agora Live Shop Demo

*[English](README.md) | 中文*

这个开源示例项目演示了如何快速集成Agora视频直播SDK和实时消息SDK，实现视频直播和聊天问答等功能

在这个示例项目中包含了以下功能：

- 加入频道和离开频道；
- 开启和观看直播；
- 发送和接收频道消息；
- 直播问答互动；
- 语音转换文字；

## 环境准备

- XCode 10.0 +
- iOS 真机设备
- 不支持模拟器

## 运行示例程序

这个段落主要讲解了如何编译和运行实例程序。

### 创建Agora账号并获取AppId

在编译和启动实例程序前，您需要首先获取一个可用的App ID:
1. 在[agora.io](https://dashboard.agora.io/signin/)创建一个开发者账号
2. 前往后台页面，点击左部导航栏的 **项目 > 项目列表** 菜单
3. 复制后台的 **App ID** 并备注，稍后启动应用时会用到它
4. 前往[zhiyin.sogou.com](https://zhiyin.sogou.com/)获取 **App ID** 和 **AppKey**

5. 打开 `AgoraLiveShop.xcworkspace`，将 AppID 和 AppKey 填写进 `KeyCenter.swift`

    ```
    static let AgoraAppId =  <#Your Agora AppId#>
    static let SogouAppId = <#Your Sogou AppId#>
    static let SogouAppKey = <#Your Sogou AppKey#>
    ```

### 集成 Agora SDK

在 [Agora.io SDK](https://www.agora.io/cn/blog/download/) 下载 **视频通话/视频直播 SDK** 和 **实时消息 SDK** ，解压后将其中**libs**文件夹下的
  - AgoraRtcEngineKit.framework
  - AgoraRtmKit.framework
三个文件复制到本项目的 `AgoraLiveShop` 文件夹下。
最后使用 XCode 打开 `AgoraLiveShop.xcworkspace`，连接 iPhone／iPad 测试设备，设置有效的开发者签名后即可运行。


## 联系我们

- 完整的 API 文档见 [文档中心](https://docs.agora.io/cn/)
- 如果在集成中遇到问题, 你可以到 [开发者社区](https://dev.agora.io/cn/) 提问
- 如果有售前咨询问题, 可以拨打 400 632 6626，或加入官方Q群 12742516 提问
- 如果需要售后技术支持, 你可以在 [Agora Dashboard](https://dashboard.agora.io) 提交工单

## 代码许可

The MIT License (MIT)
