package io.agora.liveshow.demo.voice;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sogou.audiosource.AudioRecordDataProviderFactory;
import com.sogou.audiosource.DefaultAudioSource;
import com.sogou.audiosource.IAudioSource;
import com.sogou.audiosource.IAudioSourceListener;
import com.sogou.sogouspeech.EventListener;
import com.sogou.sogouspeech.SogoSpeech;
import com.sogou.sogouspeech.SogoSpeechSettings;
import com.sogou.sogouspeech.ZhiyinInitInfo;
import com.sogou.sogouspeech.paramconstants.LanguageCode.ASRLanguageCode;
import com.sogou.sogouspeech.paramconstants.SpeechConstants;
import com.sogou.sogouspeech.paramconstants.SpeechConstants.Message;
import com.sogou.sogouspeech.paramconstants.SpeechConstants.Parameter;

import java.util.ArrayList;

/**
 * 搜狗知音在线ASR识别帮组类
 * 搜狗知音SDK集成文档 https://docs.zhiyin.sogou.com/docs/asr/sdk
 */
public class SogouVoiceChannel implements IVoiceAction {
    private static final String TAG = "SogouAsrHelper";
    // 以下信息从知音平台申请获得
    private static final String BASE_URL = "api.zhiyin.sogou.com";
    private static final String APP_ID = "1NPvp3GfhQdK1H91uIpn4gjdyvX";
    private static final String APP_KEY = "01IMEivsrQFDyPffhMVMEfeUODoYcd67E8YqhnqHWTohO6hSvqMkNKUkq4Q66Aab63D33EL9qlRQPTGzfqpfVA==";
    private static final String UUID = "testUUID";

    private SogoSpeech mSogouSpeech;
    private DefaultAudioSource mAudioSource;
    private OnVoiceActionListener mListener;
    private boolean mIsCallbacked = false;
    private boolean mIsTransfroming = false;

    private EventListener mSpeechEventListener = new EventListener() {
        @Override
        public void onEvent(String eventName, String param, byte[] data, int offset, int length, Object extra) {
            Log.e(TAG, "EventListener onEvent : " + eventName + ", " + param + ", " + (null == data ? "" : new String(data)));
            if (TextUtils.equals(SpeechConstants.Message.MSG_ASR_ONLINE_LAST_RESULT, eventName)) {
                mIsCallbacked = true;
                if (null != mListener) {
                    mListener.onAsrTranformResult(param);
                }
                stopAsrTransform();
            } else if (TextUtils.equals(Message.MSG_ASR_ONLINE_COMPLETED, eventName)) {
                if (!mIsTransfroming && !mIsCallbacked && null != mListener) {
                    mListener.onAsrTranformResult("");
                }
            }
        }

        @Override
        public void onError(String errorDomain, int errorCode, String errorDescription, Object extra) {
            Log.e(TAG, String.format("EventListener onError : %s, %d , %s , %s", errorDomain, errorCode, errorDescription, extra));
            // 9002 用户主动取消
            if (9002 != errorCode && null != mListener) {
                mIsCallbacked = true;
                mListener.onAsrTranformResult("");
            }
            stopAsrTransform();
        }
    };

    private IAudioSourceListener mAudioSourceListener = new IAudioSourceListener() {
        @Override
        public void onBegin(IAudioSource iAudioSource) {
            Log.e(TAG, "AudioSource onBegin");
            mIsCallbacked = false;
            mSogouSpeech.send(SpeechConstants.Command.ASR_ONLINE_START, "", null, 0, 0);
        }

        @Override
        public void onNewData(IAudioSource audioSource, Object dataArray, long packIndex, long sampleIndex, int flag) {
            final short[] data = (short[]) dataArray;
            mSogouSpeech.send(SpeechConstants.Command.ASR_ONLINE_RECOGIZE, "", data, (int) packIndex, 0);
        }

        @Override
        public void onEnd(IAudioSource audioSource, int status, Exception e, long sampleCount) {
            Log.e(TAG, "AudioSource onEnd");
            mSogouSpeech.send(SpeechConstants.Command.ASR_ONLINE_STOP, "", null, 0, 0);
        }
    };

    public void init(Context context) {
        ZhiyinInitInfo.Builder builder = new ZhiyinInitInfo.Builder();
        ZhiyinInitInfo initInfo = builder.setBaseUrl(BASE_URL).setUuid(UUID).setAppid(APP_ID).setAppkey(APP_KEY).create();
        SogoSpeech.initZhiyinInfo(context, initInfo);

        SogoSpeechSettings settings = SogoSpeechSettings.shareInstance();
        settings.setProperty(SpeechConstants.Parameter.ASR_ONLINE_AUDIO_CODING_INT, 1);
        settings.setProperty(SpeechConstants.Parameter.ASR_ONLINE_VAD_ENABLE_BOOLEAN, false);
        settings.setProperty(SpeechConstants.Parameter.ASR_ONLINE_VAD_LONGMODE_BOOLEAN, true);
        updateLanguage(context);

        mSogouSpeech = new SogoSpeech(context);
        mSogouSpeech.registerListener(mSpeechEventListener);

        mAudioSource = new DefaultAudioSource(new AudioRecordDataProviderFactory(context));
        mAudioSource.addAudioSourceListener(mAudioSourceListener);
    }

    @Override
    public void updateLanguage(Context context){
        boolean isEn = context.getSharedPreferences("app_spf", Context.MODE_PRIVATE).getBoolean("is_en", false);
        Log.e(TAG, "updateLanguage is en : " + isEn);
        SogoSpeechSettings settings = SogoSpeechSettings.shareInstance();
        settings.setProperty(Parameter.ASR_ONLINE_LANGUAGE_STRING, isEn ? ASRLanguageCode.ENGLISH : ASRLanguageCode.CHINESE);
    }
    
    @Override
    public void startAsrTransform(Activity activity, OnVoiceActionListener listener) {
        if (!checkPermission(activity)) {
            return;
        }
        mListener = listener;
        mIsTransfroming = true;
        mSogouSpeech.send(SpeechConstants.Command.ASR_ONLINE_CREATE, null, null, 0, 0);
        new Thread(mAudioSource, "audioRecordSource").start();
    }
    
    @Override
    public void stopAsrTransform() {
        mIsTransfroming = false;
        if (null != mAudioSource) {
            mAudioSource.stop();
        }
    }
    
    @Override
    public void release() {
        if (mIsTransfroming){
            stopAsrTransform();
        }
        mListener = null;
    }

    private String[] mNeedPermissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private boolean checkPermission(Activity activity) {
        if (VERSION.SDK_INT >= 23) {
            // android 6.0
            ArrayList<String> toApplyList = new ArrayList<String>();
            for (String perm : mNeedPermissions) {
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity, perm)) {
                    toApplyList.add(perm);
                }
            }
            String tmpList[] = new String[toApplyList.size()];
            if (!toApplyList.isEmpty()) {
                ActivityCompat.requestPermissions(activity, toApplyList.toArray(tmpList), 0);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        if (null != permissions && null != grantResults && permissions.length == grantResults.length) {
            ArrayList<String> toApplyList = new ArrayList<String>();
            for (int i = 0; i < permissions.length; i++) {
                if (PackageManager.PERMISSION_GRANTED != grantResults[i]) {
                    toApplyList.add(permissions[i]);
                }
            }
            String tmpList[] = new String[toApplyList.size()];
            if (!toApplyList.isEmpty()) {
                Toast.makeText(activity, "Demo需要如下权限", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(activity, toApplyList.toArray(tmpList), 0);
            } else {
                startAsrTransform(activity, mListener);
            }
        }
    }

}
