package com.app.youcheng.activity.my;


import android.os.Bundle;

import com.app.youcheng.R;
import com.app.youcheng.base.BaseActivity;
import com.zhouyou.http.EasyHttp;

import io.reactivex.disposables.Disposable;


public class RankMsgActivity extends BaseActivity {

    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.activity_rank_msg;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("分值规则");
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
