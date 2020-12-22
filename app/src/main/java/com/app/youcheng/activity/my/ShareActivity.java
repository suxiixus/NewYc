package com.app.youcheng.activity.my;


import android.os.Bundle;
import android.widget.TextView;

import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.entity.User;
import com.app.youcheng.utils.StringUtils;
import com.zhouyou.http.EasyHttp;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;


public class ShareActivity extends BaseActivity {
    @BindView(R.id.tvCode)
    TextView tvCode;

    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.activity_my_share;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("邀请企业注册");
    }

    @Override
    protected void initData() {
        super.initData();

        User user = MyApplication.getApplication().getCurrentUser();
        if (user != null && user.getUserInfo() != null) {
            String code = user.getUserInfo().getShareCode();
            if (StringUtils.isNotEmpty(code)) {
                tvCode.setText("邀请码：" + code);
            }
        } else {
            tvCode.setText("登录后获取您的邀请码");
        }
    }

//    @OnClick({R.id.tvLogout})
//    @Override
//    protected void setOnClickListener(View v) {
//        super.setOnClickListener(v);
//
//        switch (v.getId()) {
//            case R.id.tvLogout:
//
//                break;
//        }
//    }

    @Override
    public void onBackPressed() {
        if (loadingDialog.isShowing()) {
            hideLoading();
            disAll();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disAll();
    }

    private void disAll() {
        if (disposable1 != null) {
            EasyHttp.cancelSubscription(disposable1);
        }
    }


}
