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
import com.app.youcheng.utils.StringUtils;
import com.app.youcheng.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BillApplyDialog extends Dialog {
    @BindView(R.id.tvSure)
    TextView tvSure;
    @BindView(R.id.etStr)
    TextView etStr;

    private ClickListenerInterface clickListenerInterface;
    private Context context;

    public BillApplyDialog(Context context) {
        super(context, R.style.myDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = View.inflate(context, R.layout.dialog_bill_apply, null);
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
                String str = etStr.getText().toString();

                if (StringUtils.isNotEmpty(str)) {
                    if (clickListenerInterface != null)
                        clickListenerInterface.doSure(str);
                } else {
                    ToastUtils.showToast("请输入申诉原因");
                }
            }
        });
    }


    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    public interface ClickListenerInterface {

        void doSure(String str);
    }


}
