package io.agora.liveshow.demo.rtc;

import android.content.Context;
import android.util.Log;

import demo.liveshow.agora.io.R;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

public class RtcEngineManager {
    private static RtcEngineManager sInstance;

    public static RtcEngineManager getInstance() {
        if (null == sInstance) {
            sInstance = new RtcEngineManager();
        }
        return sInstance;
    }

    public static void destory() {
        RtcEngine.destroy();
        sInstance = null;
    }

    private RtcEngine mRtcEngine;
    private OnRtcEnventCallback mEventCallback;

    private RtcEngineManager() {}

    public void init(Context context) {
        try {
            mRtcEngine = RtcEngine.create(context, context.getString(R.string.private_app_id), mRtcEventHandler);
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
            mRtcEngine.enableAudio();
            mRtcEngine.enableVideo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RtcEngine getRtcEngine() {
        return mRtcEngine;
    }

    public void setOnRtcEventCallback(OnRtcEnventCallback callback) {
        mEventCallback = callback;
    }

    private IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        private String EVENT_TAG = "RtcEngine";

        @Override
        public void onError(int err) {
            super.onError(err);
            Log.e(EVENT_TAG, "onError : " + err);
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            Log.e(EVENT_TAG, "onJoinChannelSuccess : " + channel + ", uid = " + uid);
        }

        @Override
        public void onRejoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onRejoinChannelSuccess(channel, uid, elapsed);
            Log.e(EVENT_TAG, "onRejoinChannelSuccess : " + channel + ", uid = " + uid);
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            super.onLeaveChannel(stats);
            Log.e(EVENT_TAG, "onLeaveChannel : " + stats.toString());
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            Log.e(EVENT_TAG, "onUserJoined : " + uid + ", " + elapsed);
            if (null != mEventCallback) {
                mEventCallback.onUserJoined(uid, elapsed);
            }
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
            Log.e(EVENT_TAG, "onUserOffline : " + uid + ", " + reason);
            if (null != mEventCallback) {
                mEventCallback.onUserOffline(uid, reason);
            }
        }

        @Override
        public void onConnectionStateChanged(int state, int reason) {
            super.onConnectionStateChanged(state, reason);
            Log.e(EVENT_TAG, "onConnectionStateChanged : " + state + ", " + reason);
        }

        @Override
        public void onNetworkTypeChanged(int type) {
            super.onNetworkTypeChanged(type);
            Log.e(EVENT_TAG, "onNetworkTypeChanged : " + type);
        }

        @Override
        public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
            super.onFirstRemoteVideoDecoded(uid, width, height, elapsed);
            Log.e(EVENT_TAG, "onFirstRemoteVideoDecoded : " + uid);
            if (null != mEventCallback) {
                mEventCallback.onFirstRemoteVideoDecoded(uid, width, height, elapsed);
            }
        }
    };

}
