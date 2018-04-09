package com.hodanet.charge.fragment.found;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.hodanet.charge.R;
import com.syezon.component.adapterview.ApkAdapterView;
import com.syezon.component.adapterview.BaseAdapterView;
import com.syezon.component.adview.ApkAd;
import com.syezon.component.bean.FoundBean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 *
 */
public class FoundAppFragment extends Fragment implements BaseAdapterView.AdListener{


    @BindView(R.id.rl_content)
    RelativeLayout rlContent;
    Unbinder unbinder;

    public FoundAppFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_found_app, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        new ApkAd(rlContent, new ApkAdapterView(getContext(), this));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void click(FoundBean foundBean, View view) {

    }

    @Override
    public void show(FoundBean foundBean) {

    }
}
