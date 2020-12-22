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


public class DeleteUserDialog extends Dialog {
    @BindView(R.id.tvSure)
    TextView tvSure;
    @BindView(R.id.tvCancel)
    TextView tvCancel;
    @BindView(R.id.etMsg)
    EditText etMsg;

    private ClickListenerInterface clickListenerInterface;
    private Context context;

    public DeleteUserDialog(Context context) {
        super(context, R.style.myDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = View.inflate(context, R.layout.dialog_delete_user, null);
        setContentView(view);
        ButterKnife.bind(this, view);


        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) ((float) CommonUtils.getScreenWidth() * 0.8);
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.CENTER);

        setListener();
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
                String str = etMsg.getText().toString();

                if (StringUtils.isNotEmpty(str)) {
                    if (clickListenerInterface != null)
                        clickListenerInterface.doSure(str);
                } else {
                    ToastUtils.showToast("输入注销原因");
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
