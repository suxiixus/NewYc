package com.app.youcheng.activity.login;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.entity.EventBean;
import com.app.youcheng.entity.User;
import com.app.youcheng.entity.UserInfo;
import com.app.youcheng.utils.EventBusUtils;
import com.app.youcheng.utils.NetCodeUtils;
import com.app.youcheng.utils.StringUtils;
import com.app.youcheng.utils.ToastUtils;
import com.google.gson.reflect.TypeToken;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import io.reactivex.disposables.Disposable;

import static com.app.youcheng.GlobalConstant.ERROR_401;


public class LoginActivity extends BaseActivity {
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etPwd)
    EditText etPwd;

    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @OnClick({R.id.tvLogin, R.id.tvSign, R.id.tvNoPwd})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.tvLogin:
                doLogin();
                break;
            case R.id.tvSign:
                showActivity(SignActivity.class, null);
                break;
            case R.id.tvNoPwd:
                showActivity(ForgetPwdActivity.class, null);
                break;
        }
    }

    private void doLogin() {
        final String name = etName.getText().toString();
        final String pwd = etPwd.getText().toString();
        if (StringUtils.isEmpty(name)) {
            ToastUtils.showToast("请输入手机号");
            return;
        }

        if (StringUtils.isEmpty(pwd)) {
            ToastUtils.showToast("请输入密码");
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", name);
            jsonObject.put("password", pwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showLoading();
        EasyHttp.post(BaseHost.loginUrl)
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
                                ToastUtils.showToast("登录成功");

                                String data = object.getString("data");

                                User user = new User();
                                user.setUsername(name);
                                user.setPwd(pwd);
                                if (StringUtils.isNotEmpty(data)) {
                                    user.setToken(data);
                                }

                                MyApplication.getApplication().setCurrentUser(user);
                                MyApplication.getApplication().setHeaders(data);
                                EventBusUtils.postEvent(new EventBean(0));
                                setResult(RESULT_OK);
                                getInfo();
                            } else {
                                NetCodeUtils.checkedErrorCode(object.optString("code"), object.optString("msg"));
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

    private void getInfo() {
        if (MyApplication.getApplication().isLogin()) {
            showLoading();
            EasyHttp.get(BaseHost.infoUrl)
                    .execute(new SimpleCallBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            hideLoading();

                            try {
                                JSONObject object = new JSONObject(s);
                                int code = object.optInt("code");
                                if (code == GlobalConstant.OK) {
                                    UserInfo userInfo = gson.fromJson(object.optString("data"), new TypeToken<UserInfo>() {
                                    }.getType());

                                    if (userInfo != null) {
                                        User user = MyApplication.getApplication().getCurrentUser();
                                        if (user != null) {
                                            user.setUserInfo(userInfo);
                                            MyApplication.getApplication().setCurrentUser(user);
                                        }

                                        JPushInterface.setAlias(LoginActivity.this, 0, userInfo.getEnterpriseId());
                                    }

                                    finish();
                                } else {
                                    if (code == ERROR_401) {
                                        //登录失效
                                        MyApplication.getApplication().doLogout();
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
