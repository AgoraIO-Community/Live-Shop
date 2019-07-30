package io.agora.liveshow.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import demo.liveshow.agora.io.R;

/**
 * 直播房间页面
 */
public class LiveRoomsActivity extends AgoraBaseActivity {
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_room);
        ((TextView) findViewById(R.id.page_title_tv)).setText(R.string.page_live_recommend);
    }
    
    public void toWatchLive(View v) {
        startActivity(new Intent(this, ViewerLiveActivity.class));
    }
    
}
