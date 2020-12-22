package com.app.youcheng.activity.home;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.activity.login.SignSuccessActivity;
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


public class DanganActivity extends BaseActivity {
    @BindView(R.id.etFirmName)
    EditText etFirmName;
    @BindView(R.id.etFirmNum)
    EditText etFirmNum;
    //    @BindView(R.id.etMan)
//    EditText etMan;
    @BindView(R.id.etPhone)
    EditText etPhone;

    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.activity_dangan;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @OnClick({R.id.tvSend, R.id.tvSearch})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.tvSend:
                doSend();
                break;
            case R.id.tvSearch:
                String firmName = etFirmName.getText().toString();
                if (StringUtils.isNotEmpty(firmName)) {
                    getFileName(firmName);
                } else {
                    ToastUtils.showToast("请输入企业全称");
                }
                break;
        }
    }

    private void getFileName(String name) {
        if (MyApplication.getApplication().isLogin()) {
            showLoading();
            EasyHttp.get(BaseHost.getFileName + name)
                    .execute(new SimpleCallBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            hideLoading();
                            try {
                                JSONObject object = new JSONObject(s);
                                int code = object.optInt("code");
                                if (code == GlobalConstant.OK) {
                                    String data = object.optString("data");
                                    if (StringUtils.isNotEmpty(data)) {
                                        etFirmNum.setText(data);
                                    } else {
                                        ToastUtils.showToast("未查询到企业代码");
                                    }
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
    }

    private void doSend() {
        String firmName = etFirmName.getText().toString();
        String firmNum = etFirmNum.getText().toString();
//        String man = etMan.getText().toString();
        String phone = etPhone.getText().toString();

        if (StringUtils.isNotEmpty(firmName, firmNum, phone)) {

        } else {
            ToastUtils.showToast("请完善信息");
            return;
        }


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("enterpriseCode", firmNum);
            jsonObject.put("enterpriseName", firmName);
            jsonObject.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showLoading();
        EasyHttp.post(BaseHost.fileUrl)
                .headers("Content-Type", "application/json")
                .upJson(jsonObject.toString())
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        closeAll();

                        try {
                            JSONObject object = new JSONObject(s);
                            int code = object.optInt("code");
                            if (code == GlobalConstant.OK) {
                                showActivity(SignSuccessActivity.class, null);
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
                        closeAll();
                        NetCodeUtils.checkedErrorCode(e.getCode(), e.getMessage());
                    }
                });
    }

    private void closeAll() {
        hideLoading();
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
