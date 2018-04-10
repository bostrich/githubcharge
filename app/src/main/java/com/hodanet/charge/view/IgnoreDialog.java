package com.hodanet.charge.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.hodanet.charge.R;
import com.hodanet.charge.info.power_optimize.AppInfo;
import com.hodanet.charge.utils.ScreenUtil;
import com.hodanet.charge.utils.SpUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */
public class IgnoreDialog extends Dialog {
    @BindView(R.id.iv_icon)
    ImageView mIvIcon;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.iv_close)
    ImageView mIvClose;
    @BindView(R.id.btn_ignore)
    Button mBtnIgnore;

    public IgnoreDialog(final Context context, final AppInfo appInfo, final ComfirmListener listener) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_protect, null);
        ButterKnife.bind(this,view);

        mIvIcon.setImageDrawable(appInfo.getAppIcon());
        mTvName.setText(appInfo.getAppLabel());

        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mBtnIgnore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpUtil.saveBooleanData(context,appInfo.getPkgName(),true);
                if(listener != null) listener.click();
                dismiss();
            }
        });


        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉title，在有的手机dialog顶部会出现空白
        this.setContentView(view);
        // 设置弹出窗体的宽和高
        this.setCanceledOnTouchOutside(false);
        Window window=getWindow();
        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (ScreenUtil.getScreenHeight(context) * 0.4); // 高度设置
        p.width = (int) (ScreenUtil.getScreenWidth(context) * 0.9); // 宽度设置
        window.setAttributes(p);
    }

    public interface ComfirmListener{
        void click();
    }

}
