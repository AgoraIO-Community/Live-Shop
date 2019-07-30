package io.agora.liveshow.demo.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import demo.liveshow.agora.io.R;
import io.agora.liveshow.demo.widget.AudioRecordButton.OnVoiceBtnListener;

public class VoiceInputDialog extends Dialog implements OnVoiceBtnListener {
    private final int MSG_ID_CHANGE_PROGRESS = 100;
    private View layoutRecording;
    private View layoutError;
    private ImageView ivProgress;
    private AudioRecordButton btnRecord;

    private OnVoiceEventListener mListener;
    private int mCurrProgress;
    private boolean mIsAutoChangeProgress;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (MSG_ID_CHANGE_PROGRESS == msg.what) {
                mCurrProgress++;
                if (mCurrProgress > 7) {
                    mCurrProgress = 1;
                }
                changeProgress(mCurrProgress);
                sendEmptyMessageDelayed(MSG_ID_CHANGE_PROGRESS, 500);
            }
        }
    };

    public VoiceInputDialog(Context context) {
        super(context, R.style.DialogTheme);
    }

    public void setOnVoiceEventListener(OnVoiceEventListener listener) {
        mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_voice_input);
        layoutRecording = findViewById(R.id.dialog_voice_inputing_layout);
        layoutError = findViewById(R.id.dialog_voice_error_layout);
        ivProgress = findViewById(R.id.dialog_voice_inputing_progress_iv);
        btnRecord = findViewById(R.id.dialog_voice_record_btn);
        btnRecord.setOnVoiceBtnListener(this);
    }

    @Override
    public void recording() {
        layoutRecording.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
        if (null != mListener) {
            mListener.onStart();
        }
        if (!mIsAutoChangeProgress) {
            mIsAutoChangeProgress = true;
            mHandler.sendEmptyMessage(MSG_ID_CHANGE_PROGRESS);
        }
    }

    @Override
    public void wantToCancel() {
        layoutError.setVisibility(View.VISIBLE);
        layoutRecording.setVisibility(View.GONE);
        btnRecord.setText(R.string.voice_input_to_end);
    }

    @Override
    public void reset() {
        mIsAutoChangeProgress = false;
        layoutRecording.setVisibility(View.VISIBLE);
        ivProgress.setImageResource(R.drawable.ic_voice_input_1);
        layoutError.setVisibility(View.GONE);
        btnRecord.setText(R.string.voice_input_to_start);
        mHandler.removeMessages(MSG_ID_CHANGE_PROGRESS);
    }

    @Override
    public void canceled() {
        if (null != mListener) {
            mListener.onStop();
        }
    }

    @Override
    public void finished() {
        canceled();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private void changeProgress(int progress) {
        int resId = getContext().getResources().getIdentifier("ic_voice_input_" + progress, "drawable", getContext().getPackageName());
        ivProgress.setImageResource(resId);
    }

    public interface OnVoiceEventListener {
        void onStart();

        void onStop();
    }
}
