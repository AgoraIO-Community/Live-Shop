# iOS Agora Live Shop Demo

*English | [中文](README.zh.md)*

The Agora Live Shop Demo App is an open-source demo that will help you get both video live broadcast and message chat integrated directly into your iOS applications using the Agora Video SDK and Real-time Messaging SDK.

With this sample app, you can:

- Join / leave channel
- Broadcast / watch video
- Send / receive message
- Ask / answer Question
- Convert speech to text

## Prerequisites

- Xcode 10.0+
- Physical iOS device (iPhone or iPad)
- iOS simulator is NOT supported

## Quick Start

This section shows you how to prepare, build, and run the sample application.

### Obtain an App ID

To build and run the sample application, get an App ID:
1. Create a developer account at [agora.io](https://dashboard.agora.io/signin/). Once you finish the signup process, you will be redirected to the Dashboard.
2. Navigate in the Dashboard tree on the left to **Projects** > **Project List**.
3. Save the **App ID** from the Dashboard for later use.
4. Go to [zhiyin.sogou.com](https://zhiyin.sogou.com/) get the **App ID** and **AppKey**

5. Open `AgoraLiveShop.xcworkspace` and edit the `KeyCenter.swift` file. Update with your app ID and Keys.

    ```
    static let AgoraAppId =  <#Your Agora AppId#>
    static let SogouAppId = <#Your Sogou AppId#>
    static let SogouAppKey = <#Your Sogou AppKey#>
    ```

### Integrate the Agora SDK

1. Download the [Agora Video SDK and Real-time Messaging SDK](https://www.agora.io/en/download/). Unzip the downloaded SDK package and copy the following files from the SDK `libs` folder into the sample application `AgoraLiveShop` folder.
    - `AograRtcEngineKit.framework`
    - `AgoraRtmKit.framework`

2. Connect your iPhone or iPad device and run the project. Ensure a valid provisioning profile is applied or your project will not run.


## Resources

- You can find full API document at [Document Center](https://docs.agora.io/en/)

## License

The MIT License (MIT)
