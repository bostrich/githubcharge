package com.hodanet.charge.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.hodanet.charge.R;
import com.hodanet.charge.view.FullScreenVideoView;


public class VideoActivity extends Activity {
    public static final String URL_ADDRESS = "url";
    private LinearLayout layout_bottom;
    private RelativeLayout layout_top;
    private ImageView back;
    private SeekBar seekbar;
    private ImageView btn_play;
    private FullScreenVideoView videoView;
    private RelativeLayout layout_loading;
    private ImageView loading_anim;
    private String url;
    private ImageView close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);


        initViews();
        initData();
    }

    private void initData() {
        url = getIntent().getStringExtra(URL_ADDRESS);
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(Uri.parse(url));
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                layout_loading.setVisibility(View.GONE);
                loading_anim.clearAnimation();
                videoView.start();
            }
        });

//        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                finish();
//            }
//        });

    }

    private void initViews() {
//        layout_bottom = (LinearLayout) findViewById(R.id.layout_bottom);
        layout_top = (RelativeLayout) findViewById(R.id.layout_top);
        back = (ImageView) findViewById(R.id.back);
//        seekbar = (SeekBar) findViewById(R.id.seekbar);
//        btn_play = (ImageView) findViewById(R.id.btn_play);
        videoView = (FullScreenVideoView) findViewById(R.id.videoview);
        layout_loading = (RelativeLayout) findViewById(R.id.loading);
        loading_anim = (ImageView) findViewById(R.id.loading_anim);
        //旋转动画
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(-1);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        loading_anim.startAnimation(rotateAnimation);
        close = (ImageView) findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            videoView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            videoView.requestLayout();
//        }else {
//
//        }
    }

    @Override
    protected void onResume() {
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onResume();
    }

    @Override
    protected void onPause() {
        videoView.stopPlayback();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
