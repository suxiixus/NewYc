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
import butterknife.OnClick;


public class PhotoDialog extends Dialog {
    @BindView(R.id.tvCamera)
    TextView tvCamera;
    @BindView(R.id.tvPhoto)
    TextView tvPhoto;
    @BindView(R.id.tvCancel)
    TextView tvCancel;

    private ClickListenerInterface clickListenerInterface;
    private Context context;

    public PhotoDialog(Context context) {
        super(context, R.style.myDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(context, R.layout.dialog_photo, null);
        setContentView(view);
        ButterKnife.bind(this, view);

        initCreate();
    }

    private void initCreate() {
        // 设置弹窗的宽和高
        Window dialogWindow = getWindow();
        assert dialogWindow != null;
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.width = CommonUtils.getScreenWidth();
        dialogWindow.setAttributes(params);
    }

    @OnClick({R.id.tvCamera, R.id.tvPhoto, R.id.tvCancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvCamera:
                if (clickListenerInterface != null)
                    clickListenerInterface.doCamera();
                break;
            case R.id.tvPhoto:
                if (clickListenerInterface != null)
                    clickListenerInterface.doPhoto();
                break;
            case R.id.tvCancel:
                dismiss();
                break;
        }
    }

    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    public interface ClickListenerInterface {

        void doCamera();

        void doPhoto();
    }


}
