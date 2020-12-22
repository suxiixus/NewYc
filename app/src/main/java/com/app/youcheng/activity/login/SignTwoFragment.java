package com.app.youcheng.activity.login;

import android.view.KeyEvent;
import android.view.View;

import com.app.youcheng.R;
import com.app.youcheng.base.BaseFragment;
import com.zhouyou.http.EasyHttp;

import io.reactivex.disposables.Disposable;


public class SignTwoFragment extends BaseFragment {

    private Disposable disposable1;

    public static SignTwoFragment getInstance() {
        SignTwoFragment fragment = new SignTwoFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sign_two;
    }

    @Override
    protected void initData() {
        super.initData();
        llTitle.setVisibility(View.GONE);
    }

    @Override
    protected void loadData() {
        super.loadData();

    }

    @Override
    protected String getmTag() {
        return getClass().getSimpleName();
    }

    @Override
    protected void setListener() {
        super.setListener();

    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_BACK) {
                    if (loadingDialog.isShowing()) {
                        hideLoading();
                        disAll();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disAll();
    }

    private void disAll() {
        if (disposable1 != null) {
            EasyHttp.cancelSubscription(disposable1);
        }
    }


}
