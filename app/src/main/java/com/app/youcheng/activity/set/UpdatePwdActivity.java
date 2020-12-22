package com.app.youcheng.activity.set;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.utils.NetCodeUtils;
import com.app.youcheng.utils.StringUtils;
import com.app.youcheng.utils.ToastUtils;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

import static com.app.youcheng.GlobalConstant.ERROR_401;


public class UpdatePwdActivity extends BaseActivity {
    @BindView(R.id.etOldPwd)
    EditText etOldPwd;
    @BindView(R.id.etNewPwd)
    EditText etNewPwd;
    @BindView(R.id.etAgain)
    EditText etAgain;

    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.activity_update_pwd;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("修改密码");
        tvGoto.setVisibility(View.VISIBLE);
        tvGoto.setText("确定");
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @OnClick({R.id.tvGoto})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.tvGoto:
                doSave();
                break;
        }
    }

    private void doSave() {
        String oldPwd = etOldPwd.getText().toString();
        String newPwd = etNewPwd.getText().toString();
        String againPwd = etAgain.getText().toString();

        if (StringUtils.isNotEmpty(oldPwd, newPwd, againPwd)) {
            if (oldPwd.equals(newPwd)) {
                ToastUtils.showToast("新密码不能与当前密码相同");
                return;
            }

            if (!newPwd.equals(againPwd)) {
                ToastUtils.showToast("两次密码输入不一致");
                return;
            }
        } else {
            ToastUtils.showToast("请完善信息");
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("oldPassword", oldPwd);
            jsonObject.put("newPassword", newPwd);
            jsonObject.put("sureNewPassword", againPwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showLoading();
        EasyHttp.put(BaseHost.pwdUpdate)
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
                                ToastUtils.showToast("保存成功");
                                setResult(RESULT_OK);
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
