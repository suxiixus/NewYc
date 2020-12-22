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

import butterknife.BindView;
import butterknife.ButterKnife;


public class CardTipDialog extends Dialog {
    @BindView(R.id.tvSure)
    TextView tvSure;
    @BindView(R.id.tvCancel)
    TextView tvCancel;
    @BindView(R.id.tvTitle)
    TextView tvTitle;

    private ClickListenerInterface clickListenerInterface;
    private Context context;
    private String title;

    public CardTipDialog(Context context, String title) {
        super(context, R.style.myDialog);
        this.context = context;
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = View.inflate(context, R.layout.dialog_card_tip, null);
        setContentView(view);
        ButterKnife.bind(this, view);


        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) ((float) CommonUtils.getScreenWidth() * 0.8);
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.CENTER);

        setListener();

        if (StringUtils.isNotEmpty(title)) {
            tvTitle.setText(title);
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
