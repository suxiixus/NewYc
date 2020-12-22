package com.app.youcheng.activity.set;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.dialog.DeleteUserDialog;
import com.app.youcheng.utils.NetCodeUtils;
import com.app.youcheng.utils.ToastUtils;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

import static com.app.youcheng.GlobalConstant.ERROR_401;


public class SetSafeActivity extends BaseActivity {

    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.activity_set_safe;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("账户和安全");
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @OnClick({R.id.llPhone, R.id.llPwd, R.id.tvDelete})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.llPhone:
                showActivity(UpdatePhoneActivity.class, null, 0);
                break;
            case R.id.llPwd:
                showActivity(UpdatePwdActivity.class, null, 0);
                break;
            case R.id.tvDelete:
                showDeleteUserDialog();
                break;
        }
    }

    private void showDeleteUserDialog() {
        final DeleteUserDialog deleteUserDialog = new DeleteUserDialog(this);
        deleteUserDialog.setClicklistener(new DeleteUserDialog.ClickListenerInterface() {
            @Override
            public void doSure(String s) {
                deleteUserDialog.dismiss();
                doDelete(s);
            }
        });
        deleteUserDialog.show();
    }

    private void doDelete(String reason) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("reason", reason);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showLoading();
        EasyHttp.post(BaseHost.cancelAccount)
                .headers("Content-Type", "application/json")
                .upJson(jsonObject.toString())
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        hideLoading();

                        try {
                            JSONObject object = new JSONObject(s);
                            int code = object.optInt("code");
                            if (code == GlobalConstant.OK) {
                                ToastUtils.showToast("已经提交注销申请");
                                finish();
                            } else {
                                if (code == ERROR_401) {
                                    //登录失效
                                    MyApplication.getApplication().doLogout();
                                    finish();
                                } else {
                                    NetCodeUtils.checkedErrorCode(object.optString("code"), object.optString("msg"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ApiException e) {
                        hideLoading();
                        NetCodeUtils.checkedErrorCode(e.getCode(), e.getMessage());
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                setResult(RESULT_OK);
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
