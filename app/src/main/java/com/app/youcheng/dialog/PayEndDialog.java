package com.app.youcheng.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.app.youcheng.R;
import com.app.youcheng.utils.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PayEndDialog extends Dialog {
    @BindView(R.id.tvSure)
    TextView tvSure;

    private ClickListenerInterface clickListenerInterface;
    private Context context;

    public PayEndDialog(Context context) {
        super(context, R.style.myDialog);
        setCanceledOnTouchOutside(false); // dialog弹出后会点击屏幕，dialog不消失；点击物理返回键dialog消失
        setCancelable(false); // dialog弹出后会点击屏幕或物理返回键，dialog不消失
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = View.inflate(context, R.layout.dialog_pay_end, null);
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
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListenerInterface != null)
                    clickListenerInterface.doSure();
            }
        });
    }


    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    public interface ClickListenerInterface {

        void doSure();
    }


}
