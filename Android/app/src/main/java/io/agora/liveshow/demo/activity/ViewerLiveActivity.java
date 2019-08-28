package io.agora.liveshow.demo.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import java.util.Random;

import demo.liveshow.agora.io.R;
import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;

/**
 * 观众直播页面。包含根据主播状态切换显示画面和答题功能。
 * <p>
 *  Audience live page. It includes the function of switching display screen and answering questions according to the status of the host.
 * </p>
 */
public class ViewerLiveActivity extends BaseLiveActivity {
    private TextView tvNoSurfaceNotice;
    private int mUid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mUid = new Random().nextInt(100);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
        TextView textView = findViewById(R.id.live_user_nickname_tv);
        textView.setText(getString(R.string.user) + mUid);
        findViewById(R.id.live_surfaceview).setVisibility(TextView.GONE);
        tvNoSurfaceNotice = findViewById(R.id.live_no_surfaceview_notice);
        tvNoSurfaceNotice.setVisibility(TextView.VISIBLE);
    }

    @Override
    protected int getRtcUid() {
        return mUid;
    }

    @Override
    protected void livePrepare(RtcEngine engine) {
        engine.setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        super.onUserJoined(uid, elapsed);
        if (ANCHOR_UID == uid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvNoSurfaceNotice.setText(R.string.living_no_surfaceview);
                }
            });
        }
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        super.onUserOffline(uid, reason);
        if (ANCHOR_UID == uid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.live_surfaceview).setVisibility(TextView.GONE);
                    tvNoSurfaceNotice.setVisibility(TextView.VISIBLE);
                    tvNoSurfaceNotice.setText(R.string.living_anchor_offline);
                }
            });
        }
    }

    @Override
    public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
        super.onFirstRemoteVideoDecoded(uid, width, height, elapsed);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (ANCHOR_UID == uid) {
                    findViewById(R.id.live_surfaceview).setVisibility(TextView.VISIBLE);
                    findViewById(R.id.live_no_surfaceview_notice).setVisibility(TextView.GONE);
                }
            }
        });
    }

    @Override
    protected void handleMessageReceived(boolean isChannelMsg, String uid, String message) {
        if (!TextUtils.isEmpty(message) && message.startsWith(MSG_PREFIX_QUESTION)) {
            message = message.replace(MSG_PREFIX_QUESTION, "");
            handleReceiveQuestion(message);
        }
        super.handleMessageReceived(isChannelMsg, uid, message);
    }

    private void handleReceiveQuestion(String questionInfo) {
        AlertDialog.Builder builder = new Builder(this);
        builder.setMessage(questionInfo);
        builder.setNegativeButton(getString(R.string.no), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendMsg(MSG_PREFIX_QUESTION_RESULT + RESULT_NO);
            }
        });
        builder.setPositiveButton(getString(R.string.yes), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendMsg(MSG_PREFIX_QUESTION_RESULT + RESULT_YES);
            }
        });
        builder.show();
    }

}
