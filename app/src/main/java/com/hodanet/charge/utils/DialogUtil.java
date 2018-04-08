package com.hodanet.charge.utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hodanet.charge.R;
import com.hodanet.charge.view.CustomDialog;

/**
 *
 */

public class DialogUtil {

    public static void showDownloadHint(final Context context, String appName, final ConfirmListener listener){
        final CustomDialog dialog = new CustomDialog(context, R.style.DialogTheme);
        dialog.setContentView(R.layout.dialog_download_hint);
        TextView tvName = (TextView) dialog.findViewById(R.id.tv_name);
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        TextView tvOk = (TextView) dialog.findViewById(R.id.tv_ok);
        tvName.setText("确定打开：“"+ appName + "”？");
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(listener != null) listener.cancle();
            }
        });
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(listener != null) listener.confirm();
            }
        });
        dialog.show();

    }


    public interface ConfirmListener{
        void confirm();
        void cancle();
    }
}
