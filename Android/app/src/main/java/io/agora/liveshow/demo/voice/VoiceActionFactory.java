package io.agora.liveshow.demo.voice;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

public class VoiceActionFactory {
    
    @StringDef({VoiceChannel.CHANNEL_SOGOU})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VoiceChannel {
        String CHANNEL_SOGOU = "sogou";
    }
    
    private static Map<String, IVoiceAction> sVocieActionMap;
    
    public static IVoiceAction getDefVoiceAction() {
        return getVoiceAction(VoiceChannel.CHANNEL_SOGOU);
    }
    
    public static IVoiceAction getVoiceAction(@VoiceChannel String channel) {
        if (null == sVocieActionMap) {
            sVocieActionMap = new HashMap<>();
        }
        IVoiceAction action = sVocieActionMap.get(channel);
        if (null == action) {
            if (VoiceChannel.CHANNEL_SOGOU.equals(channel)) {
                action = new SogouVoiceChannel();
            }
            sVocieActionMap.put(channel, action);
        }
        return action;
    }
    
}
