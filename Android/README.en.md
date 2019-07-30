# Live-Shop-Android

*English | [中文](README.md)*

This open source sample project demonstrates how to quickly integrate Agora live video SDK to realize video live broadcast, message communication and question answering functions.

The following functions are included in this sample project:

- Join and leave video calls
- Anchor - Live Audience Video
- Text message communication
- Online Speech Recognition
- Answer Interaction between Answer and Audience

## Environmental preparation

- Android Studio 3.3+
- Java JDK 1.8.0+
- Android real-time devices (some simulators may have functional deficiencies or performance problems, so it is recommended to use real-time devices)

## Run the sample program

This section mainly explains how to compile and run the instance program.

### Create Agora account and get AppId

Before compiling and starting the instance program, you need to first get an available App ID:

1. Create a developer account in [agora.io] (https://dashboard.agora.io/signin/)
2. Go to the background page and click on the ** item > item list ** menu in the left navigation bar
3. Copy the background ** App ID ** and note that it will be used when starting the application later.
4. Generate temporary ** Access Token ** (valid within 24 hours) on the project page and note that the generated Token can only be applied to the corresponding channel name.
5. Fill the AppID in "app/src/main/res/values/strings.xml"

``` xml
<string name="private_app_id"><#YOUR APP ID #></string>
<!-- Temporary Token is available at https://dashboard.agora.io -->
<!-- Before officially launching the production environment, you must deploy your own Token server -->
<!-- If your project does not open a security certificate, the following values can be left blank directly --> 
<string name="agora_access_token"><#YOUR TOKEN #></string>

```

### Integrated Agora Video SDK

Download ** Video Call + Live SDK ** at [Agora.io SDK] (https://www.agora.io/cn/download/) and decompress it. Copy the contents of ** LIBS ** directory to the project according to the following corresponding relationship.

  SDK Directory | Project Directory
  ---|---
  .jar file|**/apps/libs** folder
  **arm64-v8a** folder|**/app/src/main/jniLibs** folder
  **x86** folder|**/app/src/main/jniLibs** folder
  **armeabi-v7a** folder|**/app/src/main/jniLibs** folder

Detailed integration view:

- [Video Interactive Live](https://docs.agora.io/cn/Interactive%20Broadcast/android_video?Platform=Android)
- [Real-time Message](https://docs.agora.io/cn/Real-time-Messaging/RTM_Quickstarts_android?Platform=Android)

### Start the application

Open the project with Android Studio, connect the device, compile and run it.

You can also use `Gradle'to compile and run directly.

## Contact us

- For the complete API documentation, see [Document Center](https://docs.agora.io/cn/)
- If you encounter problems in integration, you can ask questions in the [developer community](https://dev.agora.io/cn/).
- If you have pre-sale consultation questions, you can call 400 632 6626 or join the official Q group 1274 2516 to ask questions.
- If you need after-sales technical support, you can submit a work order at [Agora Dashboard](https://dashboard.agora.io)
- If you find a bug in the sample code, you are welcome to submit issue

## Code permission

The MIT License (MIT)