package com.app.youcheng.activity.login;


import android.os.Bundle;
import android.view.View;

import com.app.youcheng.R;
import com.app.youcheng.base.BaseActivity;
import com.zhouyou.http.EasyHttp;

import butterknife.OnClick;
import io.reactivex.disposables.Disposable;


public class SignSuccessActivity extends BaseActivity {

    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.activity_sign_success;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @OnClick({R.id.tvSure})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.tvSure:
                finish();
                break;
        }
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
