package com.app.youcheng.activity.login;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import cn.iwgang.countdownview.CountdownView;
import io.reactivex.disposables.Disposable;

import static com.app.youcheng.GlobalConstant.ERROR_401;


public class ForgetPwdActivity extends BaseActivity {
    @BindView(R.id.countdownView)
    CountdownView countdownView;
    @BindView(R.id.tvSend)
    TextView tvSend;
    @BindView(R.id.tvMiao)
    TextView tvMiao;
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etSms)
    EditText etSms;
    @BindView(R.id.etNewPwd)
    EditText etNewPwd;
    @BindView(R.id.etAgain)
    EditText etAgain;

    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.activity_forget_pwd;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("忘记密码");
        tvGoto.setVisibility(View.VISIBLE);
        tvGoto.setText("确定");
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @OnClick({R.id.tvGoto, R.id.tvSend})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.tvSend:
                String phone = etPhone.getText().toString();
                if (StringUtils.isNotEmpty(phone)) {
                    if (phone.length() == 11) {
                        sendSms(phone);
                    } else {
                        ToastUtils.showToast("请输入正确的手机号码");
                    }
                } else {
                    ToastUtils.showToast("请输入新手机号码");
                }
                break;

            case R.id.tvGoto:
                doSave();
                break;
        }
    }

    @Override
    protected void setListener() {
        super.setListener();

        countdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                setNoSendStatus();
            }
        });
    }

    private void setSendStatus() {
        countdownView.setVisibility(View.VISIBLE);
        tvMiao.setVisibility(View.VISIBLE);
        tvSend.setVisibility(View.GONE);
        countdownView.start(60 * 1000);
    }

    private void setNoSendStatus() {
        countdownView.setVisibility(View.GONE);
        tvMiao.setVisibility(View.GONE);
        tvSend.setVisibility(View.VISIBLE);
    }

    private void sendSms(String phone) {
        showLoading();
        EasyHttp.get(BaseHost.getSmsCodeUrl + phone)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        hideLoading();

                        try {
                            JSONObject object = new JSONObject(s);
                            int code = object.optInt("code");
                            if (code == GlobalConstant.OK) {
                                setSendStatus();
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

    private void doSave() {
        String phoneNumber = etPhone.getText().toString();
        String newPwd = etNewPwd.getText().toString();
        String again = etAgain.getText().toString();
        String smsCode = etSms.getText().toString();

        if (StringUtils.isNotEmpty(phoneNumber, newPwd, again, smsCode)) {

        } else {
            ToastUtils.showToast("请完善信息");
            return;
        }

        if (!StringUtils.isLegalPwd(newPwd)) {
            ToastUtils.showToast("请输入6～12位密码");
            return;
        }

        if (!newPwd.equals(again)) {
            ToastUtils.showToast("两次密码不一致");
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phoneNumber", phoneNumber);
            jsonObject.put("newPassword", newPwd);
            jsonObject.put("sureNewPassword", again);
            jsonObject.put("smsCode", smsCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showLoading();
        EasyHttp.put(BaseHost.forgetPwd)
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
                                ToastUtils.showToast("修改成功");
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
