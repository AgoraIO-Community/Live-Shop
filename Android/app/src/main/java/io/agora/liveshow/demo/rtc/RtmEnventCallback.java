package io.agora.liveshow.demo.rtc;

import android.text.TextUtils;
import android.util.Log;

import io.agora.rtm.RtmChannelListener;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmMessage;
import io.agora.rtm.RtmStatusCode.ConnectionState;

public class RtmEnventCallback implements RtmClientListener, RtmChannelListener {
    private final String TAG = "RtmEnventCallback";
    private String mChannelId;
    private OnRtmMessageListener mListener;

    public RtmEnventCallback(String channelId, OnRtmMessageListener listener) {
        mChannelId = channelId;
        mListener = listener;
    }

    @Override
    public void onConnectionStateChanged(int state, int i1) {
        // 1 disconnected, 2 connecting, 3 connected, 4 reconnecting, 5 aborted
        Log.e(TAG, "onConnectionStateChanged : " + state + ", " + i1);
        if (ConnectionState.CONNECTION_STATE_CONNECTED == state && null != mListener) {
            mListener.onRtmConnected();
        }
    }

    @Override
    public void onMessageReceived(RtmMessage rtmMessage, String uid) {
        // 点对点信息回调
        Log.e(TAG, "onMessageReceived");
        if (null != mListener && null != rtmMessage) {
            mListener.onMessageReceived(false, uid, rtmMessage.getText());
        }
    }

    @Override
    public void onTokenExpired() {
        Log.e(TAG, "onTokenExpired");
    }


    @Override
    public void onMessageReceived(RtmMessage rtmMessage, RtmChannelMember rtmChannelMember) {
        if (null == rtmMessage || null == rtmChannelMember || !TextUtils.equals(mChannelId, rtmChannelMember.getChannelId())) {
            return;
        }
        Log.e(TAG, "onMessageReceived : " + rtmChannelMember.getUserId() + " - " + rtmMessage.getText());
        if (null != mListener) {
            mListener.onMessageReceived(true, rtmChannelMember.getUserId(), rtmMessage.getText());
        }
    }

    @Override
    public void onMemberJoined(RtmChannelMember rtmChannelMember) {
        Log.e(TAG, "onMemberJoined : " + rtmChannelMember.getUserId() + ", " + rtmChannelMember.getChannelId());
    }

    @Override
    public void onMemberLeft(RtmChannelMember rtmChannelMember) {
        Log.e(TAG, "onMemberLeft : " + rtmChannelMember.getUserId() + ", " + rtmChannelMember.getChannelId());
    }

    public interface OnRtmMessageListener {
        void onMessageReceived(boolean isChannelMsg, String uid, String message);

        void onRtmConnected();
    }

}
