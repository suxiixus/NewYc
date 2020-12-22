package com.app.youcheng.activity.set;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.entity.SmsBean;
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
import io.reactivex.disposables.Disposable;

import static com.app.youcheng.GlobalConstant.ERROR_401;


public class SetOpenActivity extends BaseActivity {
    @BindView(R.id.iv1)
    ImageView iv1;
    @BindView(R.id.iv2)
    ImageView iv2;

    private int showBillAmountMoney = 0;
    private int showSendEnterpriseName = 0;
    private String systemId;

    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.activity_set_open;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("公开设置");
        tvGoto.setVisibility(View.VISIBLE);
        tvGoto.setText("保存");
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @Override
    protected void loadData() {
        super.loadData();
        getMsg();
    }

    @OnClick({R.id.tvGoto, R.id.iv1, R.id.iv2})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.tvGoto:
                doSave();
                break;
            case R.id.iv1:
                showSendEnterpriseName = getIntV(showSendEnterpriseName);
                setStatus();
                break;
            case R.id.iv2:
                showBillAmountMoney = getIntV(showBillAmountMoney);
                setStatus();
                break;
        }
    }

    private int getIntV(int aa) {
        if (aa == 0) {
            aa = 1;
        } else {
            aa = 0;
        }

        return aa;
    }

    private void doSave() {
        if (StringUtils.isEmpty(systemId)) {
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("showSendEnterpriseName", showSendEnterpriseName);
            jsonObject.put("showBillAmountMoney", showBillAmountMoney);
            jsonObject.put("systemId", systemId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showLoading();
        EasyHttp.put(BaseHost.settingBill)
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

    private void getMsg() {
        showLoading();
        EasyHttp.get(BaseHost.settingSms)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        hideLoading();

                        try {
                            JSONObject object = new JSONObject(s);
                            int code = object.optInt("code");
                            if (code == GlobalConstant.OK) {
                                SmsBean smsBean = gson.fromJson(object.optString("data"), new TypeToken<SmsBean>() {
                                }.getType());

                                if (smsBean != null) {
                                    showSendEnterpriseName = smsBean.getShowSendEnterpriseName();
                                    showBillAmountMoney = smsBean.getShowBillAmountMoney();
                                    systemId = smsBean.getSystemId();
                                    setStatus();
                                }
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

    private void setStatus() {
        iv1.setSelected(showSendEnterpriseName == 1);
        iv2.setSelected(showBillAmountMoney == 1);
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
