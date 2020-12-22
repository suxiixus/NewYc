package com.app.youcheng.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.app.youcheng.R;
import com.app.youcheng.utils.CommonUtils;
import com.app.youcheng.utils.StringUtils;
import com.app.youcheng.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PhoneDialog extends Dialog {
    @BindView(R.id.tvSure)
    TextView tvSure;
    @BindView(R.id.tvCancel)
    TextView tvCancel;
    @BindView(R.id.etPhone)
    EditText etPhone;

    private ClickListenerInterface clickListenerInterface;
    private Context context;
    private String phoneStr;

    public PhoneDialog(Context context, String phoneStr) {
        super(context, R.style.myDialog);
        this.context = context;
        this.phoneStr = phoneStr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = View.inflate(context, R.layout.dialog_phone, null);
        setContentView(view);
        ButterKnife.bind(this, view);


        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) ((float) CommonUtils.getScreenWidth() * 0.8);
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.CENTER);

        setListener();

        if (StringUtils.isNotEmpty(phoneStr)) {
            etPhone.setText(phoneStr);
        }
    }

    private void setListener() {
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = etPhone.getText().toString();

                if (StringUtils.isNotEmpty(str)) {
                    if (clickListenerInterface != null)
                        clickListenerInterface.doSure(str);
                } else {
                    ToastUtils.showToast("请输入手机号");
                }
            }
        });
    }


    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    public interface ClickListenerInterface {

        void doSure(String s);
    }


}
