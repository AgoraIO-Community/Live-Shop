package io.agora.liveshow.demo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import io.agora.liveshow.demo.rtc.RtcEngineManager;
import io.agora.liveshow.demo.rtc.RtmClientManager;
import io.agora.liveshow.demo.voice.VoiceActionFactory;

public class DemoApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        VoiceActionFactory.getDefVoiceAction().init(this);
        RtcEngineManager.getInstance().init(this);
        RtmClientManager.getInstance().init(this);
    }
    
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RtcEngineManager.destory();
        RtmClientManager.destory();
    }

}
