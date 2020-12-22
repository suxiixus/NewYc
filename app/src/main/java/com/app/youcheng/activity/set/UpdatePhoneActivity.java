package com.app.youcheng.activity.set;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.entity.User;
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


public class UpdatePhoneActivity extends BaseActivity {
    @BindView(R.id.countdownView)
    CountdownView countdownView;
    @BindView(R.id.tvSend)
    TextView tvSend;
    @BindView(R.id.tvMiao)
    TextView tvMiao;
    @BindView(R.id.etOldPhone)
    EditText etOldPhone;
    @BindView(R.id.etNewPhone)
    EditText etNewPhone;
    @BindView(R.id.etSms)
    EditText etSms;

    private String oldTel;
    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.activity_update_phone;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("更换手机号");
        tvGoto.setVisibility(View.VISIBLE);
        tvGoto.setText("确定");
    }

    @Override
    protected void initData() {
        super.initData();

        User user = MyApplication.getApplication().getCurrentUser();
        if (user != null && StringUtils.isNotEmpty(user.getUserInfo().getCompanyTel())) {
            oldTel = user.getUserInfo().getCompanyTel();
        }
    }

    @OnClick({R.id.tvGoto, R.id.tvSend})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.tvSend:
                String oldPhone = etOldPhone.getText().toString();
                if (StringUtils.isEmpty(oldPhone)) {
                    ToastUtils.showToast("请输入旧手机号码");
                } else {
//                    if (oldPhone.equals(oldTel)) {
                    String phone = etNewPhone.getText().toString();
                    if (StringUtils.isNotEmpty(phone)) {
                        if (phone.length() == 11) {
                            if (!oldPhone.equals(phone)) {
                                sendSms(phone);
                            } else {
                                ToastUtils.showToast("新手机号不能与旧手机号相同");
                            }
                        } else {
                            ToastUtils.showToast("请输入正确的手机号码");
                        }
                    } else {
                        ToastUtils.showToast("请输入新手机号码");
                    }
//                    } else {
//                        ToastUtils.showToast("旧手机号码错误");
//                    }
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
        EasyHttp.get(BaseHost.getSmsCodeNeedUrl + phone)
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
        String oldPhone = etOldPhone.getText().toString();
        String newPhone = etNewPhone.getText().toString();
        String smsCode = etSms.getText().toString();

        if (StringUtils.isNotEmpty(oldPhone, newPhone, smsCode)) {

        } else {
            ToastUtils.showToast("请完善信息");
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("oldPhone", oldPhone);
            jsonObject.put("newPhone", newPhone);
            jsonObject.put("smsCode", smsCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showLoading();
        EasyHttp.put(BaseHost.phoneUpdate)
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
