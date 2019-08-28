package io.agora.liveshow.demo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;

import demo.liveshow.agora.io.R;
import io.agora.liveshow.demo.data.ChatMessage;
import io.agora.liveshow.demo.rtc.OnRtcEnventCallback;
import io.agora.liveshow.demo.rtc.RtcEngineManager;
import io.agora.liveshow.demo.rtc.RtmClientManager;
import io.agora.liveshow.demo.rtc.RtmEnventCallback;
import io.agora.liveshow.demo.rtc.RtmEnventCallback.OnRtmMessageListener;
import io.agora.liveshow.demo.widget.MessageContainer;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmChannel;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmMessage;

/**
 * 直播基础类页面，封装了直播基础功能和消息通信功能
 * <p>
 * Live Basic Class Page, encapsulating Live Basic Function and Message Communication Function
 * </p>
 */
public abstract class BaseLiveActivity extends AgoraBaseActivity implements OnRtcEnventCallback, OnRtmMessageListener {
    protected final String MSG_PREFIX_QUESTION = "AgoraQuestion:";
    protected final String MSG_PREFIX_QUESTION_RESULT = "AgoraQuestionResult:";
    protected final String MSG_PREFIX_DIRECT_DISPLAY = "AgoraDirectDisplay:";
    protected final String RESULT_NO = "yes";
    protected final String RESULT_YES = "no";
    private final String CHANNEL_ID = "liveTest"; // test channel id
    protected final int ANCHOR_UID = Integer.MAX_VALUE;
    protected String TAG = getClass().getSimpleName() + "RtcEngine";
    private EditText etChatMsg;

    protected RtcEngine mRtcEngine;
    protected RtmClient mRtmClient;
    protected RtmChannel mRtmChannel;
    private MessageContainer mMsgContainer;
    private RtmEnventCallback mRtmEventListener;
    private boolean mIsMsgChannelEnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullscreenStausBar();
        setContentView(R.layout.activity_living);
        initView();
        initRtcEngine();
        initRtmClient();
    }

    protected void initView() {
        mMsgContainer = new MessageContainer((RecyclerView) findViewById(R.id.live_msg_recycler_view));
        etChatMsg = findViewById(R.id.live_msg_et);
        findViewById(R.id.live_msg_send_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                doSendMsg();
            }
        });
    }

    protected abstract int getRtcUid();

    protected abstract void livePrepare(RtcEngine engine);

    private void initRtcEngine() {
        mRtcEngine = RtcEngineManager.getInstance().getRtcEngine();
        RtcEngineManager.getInstance().setOnRtcEventCallback(this);
        livePrepare(mRtcEngine);

        SurfaceView surface = RtcEngine.CreateRendererView(this);
        ((FrameLayout) findViewById(R.id.live_surfaceview)).addView(surface);

        if (isAnchor()) {
            mRtcEngine.enableLocalAudio(true);
            mRtcEngine.setupLocalVideo(new VideoCanvas(surface, VideoCanvas.RENDER_MODE_HIDDEN, ANCHOR_UID));
            mRtcEngine.startPreview();
        } else {
            mRtcEngine.enableLocalAudio(false);
            mRtcEngine.setupRemoteVideo(new VideoCanvas(surface, VideoCanvas.RENDER_MODE_HIDDEN, ANCHOR_UID));
        }

        mRtcEngine.joinChannel("", CHANNEL_ID, "", getRtcUid());
    }

    private void initRtmClient() {
        mRtmClient = RtmClientManager.getInstance().getRtmClient();
        mRtmClient.login("", String.valueOf(getRtcUid()), new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "rtmClient login success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.e(TAG, "rtmClient login fail : " + errorInfo);
            }
        });
        mRtmEventListener = new RtmEnventCallback(CHANNEL_ID, this);
        checkChannelEnable();
        if (null == mRtmClient) {
            showToast(R.string.toast_create_channel_error);
        }
    }

    @Override
    public void onRtmConnected() {
        if (isFinishing() || !checkChannelEnable() || mIsMsgChannelEnable) {
            return;
        }
        mRtmChannel.join(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mIsMsgChannelEnable = true;
                Log.e(TAG, "rtmClient join channel success");
                showToast(R.string.toast_msg_service_ready);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                mIsMsgChannelEnable = false;
                Log.e(TAG, "rtmClient join channel fail : " + errorInfo);
            }
        });
    }

    private boolean checkChannelEnable() {
        if (null == mRtmChannel) {
            mRtmChannel = mRtmClient.createChannel(CHANNEL_ID, mRtmEventListener);
            RtmClientManager.getInstance().setRtmClientListener(mRtmEventListener);
            return false;
        }
        return true;
    }

    private void doSendMsg() {
        if (!checkChannelEnable()) {
            showToast(R.string.toast_msg_service_error_and_retry);
            return;
        }
        String msg = etChatMsg.getText().toString();
        if (TextUtils.isEmpty(msg)) {
            showToast(R.string.toast_msg_is_empty);
            return;
        }
        sendMsg(msg, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                hideSoftKeyboard(etChatMsg);
                Log.e(TAG, "sendMessage onSuccess");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.e(TAG, "sendMessage onFailure : " + errorInfo);
            }
        });
        etChatMsg.setText("");
    }

    protected void sendMsg(String msg) {
        sendMsg(msg, null);
    }

    protected void sendMsg(String msg, ResultCallback<Void> callback) {
        RtmMessage rtmMessage = mRtmClient.createMessage();
        rtmMessage.setText(msg);
        if (null == callback) {
            callback = mDefMsgSendCallback;
        }
        mRtmChannel.sendMessage(rtmMessage, callback);
        if (!TextUtils.isEmpty(msg) && msg.startsWith(MSG_PREFIX_DIRECT_DISPLAY)) {
            mMsgContainer.addMessage(new ChatMessage("", msg.replace(MSG_PREFIX_DIRECT_DISPLAY, "")));
        } else {
            String finalMsg = msg.replace(MSG_PREFIX_QUESTION, "").replace(MSG_PREFIX_QUESTION_RESULT, "");
            mMsgContainer.addMessage(new ChatMessage(getUserName(getRtcUid()), finalMsg));
        }
    }

    private ResultCallback<Void> mDefMsgSendCallback = new ResultCallback<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            Log.e(TAG, "sendMessage onSuccess");
        }

        @Override
        public void onFailure(ErrorInfo errorInfo) {
            Log.e(TAG, "sendMessage onFailure : " + errorInfo);
        }
    };

    @Override
    public void onUserJoined(final int uid, final int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMsgContainer.addMessage(new ChatMessage("", getUserName(uid) + getString(R.string.online)));
            }
        });
    }

    @Override
    public void onUserOffline(final int uid, int reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMsgContainer.addMessage(new ChatMessage("", getUserName(uid) + getString(R.string.offline)));
            }
        });
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {}

    @Override
    public final void onMessageReceived(final boolean isChannelMsg, final String uid, final String message) {
        if (isFinishing() || TextUtils.isEmpty(message) || TextUtils.isEmpty(uid)) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handleMessageReceived(isChannelMsg, uid, message);
            }
        });
    }

    protected void handleMessageReceived(boolean isChannelMsg, String uid, String message) {
        if (message.startsWith(MSG_PREFIX_QUESTION_RESULT)) {
            return;
        }
        if (message.startsWith(MSG_PREFIX_DIRECT_DISPLAY)) {
            mMsgContainer.addMessage(new ChatMessage("", message.replace(MSG_PREFIX_DIRECT_DISPLAY, "")));
        } else {
            try {
                mMsgContainer.addMessage(new ChatMessage(getUserName(Integer.parseInt(uid)), message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getUserName(int uid) {
        if (uid == getRtcUid()) {
            return getString(R.string.me);
        } else if (uid == ANCHOR_UID) {
            return getString(R.string.anchor);
        }
        return getString(R.string.user) + uid;
    }

    private boolean isAnchor() {
        return ANCHOR_UID == getRtcUid();
    }

    @Override
    public void finish() {
        super.finish();
        if (isAnchor()) {
            mRtcEngine.setupLocalVideo(null);
            mRtcEngine.stopPreview();
        } else {
            mRtcEngine.setupRemoteVideo(null);
        }
        mRtcEngine.leaveChannel();
        RtcEngineManager.getInstance().setOnRtcEventCallback(null);

        if (null != mRtmChannel) {
            mRtmChannel.release();
        }
        RtmClientManager.getInstance().setRtmClientListener(null);
    }

}
