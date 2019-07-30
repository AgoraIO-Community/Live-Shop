package io.agora.liveshow.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import io.agora.liveshow.demo.utils.ImageSelectHelper;
import io.agora.liveshow.demo.utils.ImageSelectHelper.OnImageSelectedListener;
import me.gujun.android.taggroup.TagGroup;

import demo.liveshow.agora.io.R;

/**
 * 直播准备页面。包含主播昵称、头像、直播封面、标签等元素设置
 */
public class LivePrepareActivity extends AgoraBaseActivity {
    private EditText etNickname;
    private ImageView ivHeadimg;
    private ImageView ivLiveCover;
    private TagGroup tagGroup;

    private String mHeadImgUrl;
    private String mCoverImgUrl;
    private ImageSelectHelper mImgSelectHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_prepare);
        ((TextView) findViewById(R.id.page_title_tv)).setText(R.string.page_live_prepare);
        etNickname = findViewById(R.id.live_prepare_anchor_nickname_tv);
        ivHeadimg = findViewById(R.id.live_prepare_anchor_headimg_iv);
        ivLiveCover = findViewById(R.id.live_prepare_cover_iv);
        tagGroup = findViewById(R.id.live_prepare_anchor_tags_view);

        mImgSelectHelper = new ImageSelectHelper(this);
    }

    public void doChangeHeadimg(View view) {
        mImgSelectHelper.startSelectImage(new OnImageSelectedListener() {
            @Override
            public void onImageSelected(String sourcePath, String cropPath) {
                mHeadImgUrl = sourcePath;
                Glide.with(mContext).load(sourcePath).into(ivHeadimg);
            }

            @Override
            public void onCanceled() {}
        });
    }

    public void doChangeLiveCover(View view) {
        mImgSelectHelper.startSelectImage(new OnImageSelectedListener() {
            @Override
            public void onImageSelected(String sourcePath, String cropPath) {
                mCoverImgUrl = sourcePath;
                Glide.with(mContext).load(sourcePath).into(ivLiveCover);
            }

            @Override
            public void onCanceled() {}
        });
    }

    public void doStartLive(View view) {
        String nickname = etNickname.getText().toString();
        if (TextUtils.isEmpty(nickname)) {
            showToast(R.string.toast_nickname_is_empty);
            return;
        }

        if (TextUtils.isEmpty(mHeadImgUrl)) {
            showToast(R.string.toast_headimg_is_empty);
            return;
        }

        if (TextUtils.isEmpty(mCoverImgUrl)) {
            showToast(R.string.toast_cover_is_empty);
            return;
        }

        String[] tags = tagGroup.getTags();
        Intent intent = new Intent(this, AnchorLiveActivity.class);
        Bundle data = new Bundle();
        data.putString("user_name", nickname);
        data.putString("user_headimg", mHeadImgUrl);
        data.putStringArray("user_tags", tags);
        intent.putExtras(data);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mImgSelectHelper.onActivityResult(requestCode, resultCode, data);
    }

}
