package io.agora.liveshow.demo.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Locale;

import demo.liveshow.agora.io.R;
import io.agora.liveshow.demo.voice.VoiceActionFactory;

/**
 * 角色选择页面。该页面可以切换主播和观众角色，也支持语言修改。
 */
public class RoleSelectActivity extends AgoraBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isEn = getSharedPreferences("app_spf", Context.MODE_PRIVATE).getBoolean("is_en", false);
        if(!isEn){
            isEn = Locale.ENGLISH.getLanguage().equals(Locale.getDefault().getLanguage());
        }
        changeLocale(isEn ? Locale.ENGLISH : Locale.CHINA);
        requestPermission();
    }

    public void iAmLiveAnchor(View view) {
        startActivity(new Intent(this, LivePrepareActivity.class));
    }

    public void iAmLiveViewer(View view) {
        startActivity(new Intent(this, LiveRoomsActivity.class));
    }

    public void changeToChineseEnv(View view){
        changeLocale(Locale.CHINA);
    }

    public void changeToEnglishEnv(View view){
        changeLocale(Locale.ENGLISH);
    }

    private void changeLocale(Locale locale){
        Log.e("RoleSelect", "Current locale : " + Locale.getDefault().getCountry() + " - " + Locale.getDefault().getLanguage());
        Configuration config = getResources().getConfiguration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        setContentView(R.layout.activity_role_select);
        getSharedPreferences("app_spf", Context.MODE_PRIVATE).edit().putBoolean("is_en", Locale.ENGLISH == locale).commit();
        VoiceActionFactory.getDefVoiceAction().updateLanguage(this);
    }

    private String[] mNeedPermissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private void requestPermission() {
        if (VERSION.SDK_INT >= 23) {
            // android 6.0
            ArrayList<String> toApplyList = new ArrayList<String>();
            for (String perm : mNeedPermissions) {
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                    toApplyList.add(perm);
                }
            }
            String tmpList[] = new String[toApplyList.size()];
            if (!toApplyList.isEmpty()) {
                ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 0);
            }
        }
    }
    
    @Override
    public void finish() {
        super.finish();
        try {
            System.exit(0);
        }catch (Exception e){}
    }
}
