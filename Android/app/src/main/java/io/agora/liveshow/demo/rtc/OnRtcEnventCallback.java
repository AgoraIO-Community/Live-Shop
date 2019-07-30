package io.agora.liveshow.demo.rtc;

public interface OnRtcEnventCallback {

    void onUserJoined(int uid, int elapsed);

    void onUserOffline(int uid, int reason);

    void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed);

}
