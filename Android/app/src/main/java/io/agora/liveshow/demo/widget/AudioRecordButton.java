package io.agora.liveshow.demo.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import demo.liveshow.agora.io.R;

public class AudioRecordButton extends AppCompatButton {
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;
    private static final int DISTANCE_Y_CANCEL = 50;

    private OnVoiceBtnListener mListener;
    private int mCurrentState = STATE_NORMAL;
    private boolean isRecording = false;
    private boolean mReady;

    public AudioRecordButton(Context context) {
        this(context, null);
    }

    public AudioRecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mReady = true;
                return false;
            }
        });
    }

    public void setOnVoiceBtnListener(OnVoiceBtnListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    if (wantToCancel(x, y)) {
                        changeState(STATE_WANT_TO_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!mReady) {
                    reset();
                    return super.onTouchEvent(event);
                }
                if (mCurrentState == STATE_RECORDING) {
                    if (null != mListener) {
                        mListener.finished();
                    }
                } else if (null != mListener) {
                    mListener.canceled();
                }
                reset();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void reset() {
        isRecording = false;
        changeState(STATE_NORMAL);
        mReady = false;
        if (null != mListener) {
            mListener.reset();
        }
    }

    private boolean wantToCancel(int x, int y) {
        if (x < 0 || x > getWidth()) {
            return true;
        }
        if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }
        return false;
    }

    private void changeState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (mCurrentState) {
                case STATE_NORMAL:
                    setText(R.string.voice_input_to_start);
                    break;
                case STATE_RECORDING:
                    setText(R.string.voice_input_to_end);
                    isRecording = true;
                    if (isRecording && null != mListener) {
                        mListener.recording();
                    }
                    break;
                case STATE_WANT_TO_CANCEL:
                    setText(R.string.voice_input_error_cancel);
                    if (null != mListener) {
                        mListener.wantToCancel();
                    }
                    break;
            }
        }
    }

    @Override
    public boolean onPreDraw() {
        return false;
    }

    public interface OnVoiceBtnListener {
        void recording();

        void wantToCancel();

        void canceled();

        void finished();

        void reset();
    }

}
