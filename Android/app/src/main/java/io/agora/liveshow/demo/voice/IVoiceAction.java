package io.agora.liveshow.demo.voice;

import android.app.Activity;
import android.content.Context;

public interface IVoiceAction {
    
    /** init voice enviroment */
    void init(Context context);
    
    /** change voice tranform language config */
    void updateLanguage(Context context);
    
    /**
     * start asr transfrom
     *
     * @param activity this current activity
     * @param listener the listener for transform result callback
     */
    void startAsrTransform(Activity activity, OnVoiceActionListener listener);
    
    /** to stop asr transform */
    void stopAsrTransform();
    
    void release();
    
    /** when voice transform request permission, should call this method in {@link Activity#onRequestPermissionsResult(int, String[], int[])}*/
    void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults);
    
    interface OnVoiceActionListener {
        
        void onAsrTranformResult(String result);
        
    }
    
}
