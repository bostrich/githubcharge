package com.hodanet.charge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hodanet.charge.activity.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
