package com.hodanet.charge.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hodanet.charge.R;
import com.hodanet.charge.fragment.PicFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PicActivity extends BaseActivity {

    @BindView(R.id.fl_content)
    FrameLayout flContent;

    @BindView(R.id.ll_back)
    LinearLayout llBack;

    private PicFragment picFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic);
        ButterKnife.bind(this);
        picFragment = new PicFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fl_content, picFragment).show(picFragment).commit();
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
