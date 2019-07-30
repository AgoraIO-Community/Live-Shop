package io.agora.liveshow.demo.rtc;

import android.content.Context;
import android.util.Log;

import demo.liveshow.agora.io.R;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmMessage;

public class RtmClientManager {
    private static final String TAG = "RtmClientManager";
    private static RtmClientManager sInstance;

    public static RtmClientManager getInstance(){
        if (null == sInstance){
            sInstance = new RtmClientManager();
        }
        return sInstance;
    }

    public static void destory(){
        getInstance().getRtmClient().release();
    }

    private RtmClientManager(){}

    private RtmClient mRtmClient;
    private int mConnectState;
    private RtmClientListener mClientListener;

    public void init(Context context){
        RtmClientListener listener = new RtmClientListener() {
            @Override
            public void onConnectionStateChanged(int state, int i1) {
                mConnectState = state;
                Log.e(TAG, "onConnectionStateChanged : " + state);
                if (null != mClientListener){
                    mClientListener.onConnectionStateChanged(state, i1);
                }
            }

            @Override
            public void onMessageReceived(RtmMessage rtmMessage, String uid) {
                Log.e(TAG, "onMessageReceived");
                if (null != mClientListener){
                    mClientListener.onMessageReceived(rtmMessage, uid);
                }
            }

            @Override
            public void onTokenExpired() {
                Log.e(TAG, "onTokenExpired");
                if (null != mClientListener){
                    mClientListener.onTokenExpired();
                }
            }
        };
        try {
            mRtmClient = RtmClient.createInstance(context, context.getString(R.string.private_app_id), listener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public RtmClient getRtmClient(){
        return mRtmClient;
    }

    public void setRtmClientListener(RtmClientListener listener){
        mClientListener = listener;
        if (null != listener){
            listener.onConnectionStateChanged(mConnectState, 0);
        }
    }

}
