package com.app.youcheng.activity.login;


import android.os.Bundle;

import com.app.youcheng.R;
import com.app.youcheng.base.BaseActivity;
import com.zhouyou.http.EasyHttp;

import io.reactivex.disposables.Disposable;


public class UserMsgActivity extends BaseActivity {

    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.activity_user_msg;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("用户服务协议");
    }

    @Override
    protected void initData() {
        super.initData();

    }

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
