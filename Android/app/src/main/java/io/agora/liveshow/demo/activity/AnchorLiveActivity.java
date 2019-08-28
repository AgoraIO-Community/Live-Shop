package io.agora.liveshow.demo.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import demo.liveshow.agora.io.R;
import io.agora.liveshow.demo.voice.IVoiceAction.OnVoiceActionListener;
import io.agora.liveshow.demo.voice.VoiceActionFactory;
import io.agora.liveshow.demo.widget.CircleImageView;
import io.agora.liveshow.demo.widget.VoiceInputCheckDialog;
import io.agora.liveshow.demo.widget.VoiceInputDialog;
import io.agora.liveshow.demo.widget.VoiceInputDialog.OnVoiceEventListener;
import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;

/**
 * 主播直播页面。包含发送语音题目和观众答题统计功能。
 * <p>
 *  Host live page. Includes the function of sending voice questions and audiences'answer statistics.
 * </p>
 */
public class AnchorLiveActivity extends BaseLiveActivity {
    private final int WAIT_TIME = 15 * 1000;
    private final int MSG_ANALYZE_RESULT = 100;
    private VoiceInputDialog mInputDialog;
    private String mPreQuestionInfo;
    private int mYesNum;
    private int mNoNum;
    private boolean mIsWaitingResult = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ANALYZE_RESULT:
                    startAnalyzeQuestionResult();
                    break;
            }
        }
    };

    @Override
    protected void initView() {
        super.initView();
        Bundle data = getIntent().getExtras();
        if (null != data) {
            CircleImageView imageView = findViewById(R.id.live_user_headimg_iv);
            Glide.with(this).load(data.getString("user_headimg")).into(imageView);
            TextView textView = findViewById(R.id.live_user_nickname_tv);
            textView.setText(data.getString("user_name"));
        }
        View questView = findViewById(R.id.live_quest_entrance_iv);
        questView.setVisibility(View.VISIBLE);
        questView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsWaitingResult) {
                    showToast(R.string.toast_question_isnot_end);
                    return;
                }
                showVoiceInputDialog();
            }
        });
    }

    @Override
    protected int getRtcUid() {
        return ANCHOR_UID;
    }

    @Override
    protected void livePrepare(RtcEngine engine) {
        engine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
    }

    private void showVoiceInputDialog() {
        if (null == mInputDialog) {
            mInputDialog = new VoiceInputDialog(this);
            mInputDialog.setOnVoiceEventListener(new OnVoiceEventListener() {
                @Override
                public void onStart() {
                    mRtcEngine.disableAudio();
                    VoiceActionFactory.getDefVoiceAction().startAsrTransform(mContext, mVoiceActionListener);
                }

                @Override
                public void onStop() {
                    VoiceActionFactory.getDefVoiceAction().stopAsrTransform();
                    mRtcEngine.enableAudio();
                }
            });
        }
        mInputDialog.show();
    }
    
    private OnVoiceActionListener mVoiceActionListener = new OnVoiceActionListener() {
        @Override
        public void onAsrTranformResult(final String result) {
            if (TextUtils.isEmpty(result)) {
                showToast(R.string.toast_no_asr_input);
                return;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mInputDialog.dismiss();
                    doubleCheckInputMsg(result);
                }
            });
        }
    };

    private void doubleCheckInputMsg(String inputMsg) {
        final VoiceInputCheckDialog checkDialog = new VoiceInputCheckDialog(this);
        checkDialog.setMessage(inputMsg);
        checkDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                String finalMsg = checkDialog.getFinalQuestionInfo();
                if (TextUtils.isEmpty(finalMsg)) {
                    showToast(R.string.toast_question_is_empty);
                } else {
                    mPreQuestionInfo = finalMsg;
                    sendMsg(MSG_PREFIX_QUESTION + finalMsg);
                    mIsWaitingResult = true;
                    mYesNum = mNoNum = 0;
                    mHandler.sendEmptyMessageDelayed(MSG_ANALYZE_RESULT, WAIT_TIME);
                }
            }
        });
        checkDialog.show();
    }

    private void startAnalyzeQuestionResult() {
        mIsWaitingResult = false;
        String result = String.format(getString(R.string.msg_question_analysis_result), mPreQuestionInfo, (mYesNum + mNoNum), mYesNum, mNoNum);
        sendMsg(MSG_PREFIX_DIRECT_DISPLAY + result);
    }

    @Override
    protected void handleMessageReceived(boolean isChannelMsg, String uid, String message) {
        if (!TextUtils.isEmpty(message) && message.startsWith(MSG_PREFIX_QUESTION_RESULT)) {
            message = message.replace(MSG_PREFIX_QUESTION_RESULT, "");
            if (RESULT_YES.equalsIgnoreCase(message)) {
                mYesNum++;
            } else {
                mNoNum++;
            }
            return;
        }
        super.handleMessageReceived(isChannelMsg, uid, message);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        VoiceActionFactory.getDefVoiceAction().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VoiceActionFactory.getDefVoiceAction().release();
        mHandler.removeMessages(MSG_ANALYZE_RESULT);
    }
}
