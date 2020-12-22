package com.app.youcheng.activity.my;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.entity.UserInfo;
import com.app.youcheng.utils.NetCodeUtils;
import com.app.youcheng.utils.StringUtils;
import com.app.youcheng.utils.ToastUtils;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

import static com.app.youcheng.GlobalConstant.ERROR_401;


public class SetPhoneActivity extends BaseActivity {
    @BindView(R.id.iv1)
    ImageView iv1;
    @BindView(R.id.etTxt)
    EditText etTxt;

    private int isShow = 0;//(0否,1是)
    private UserInfo userInfo;

    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.activity_set_phone;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("联系电话");
        tvGoto.setVisibility(View.VISIBLE);
        tvGoto.setText("保存");
    }

    @Override
    protected void initData() {
        super.initData();

        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");
        if (userInfo != null) {
            isShow = userInfo.getShowTel();
            if (StringUtils.isNotEmpty(userInfo.getCompanyTel())) {
                etTxt.setText(userInfo.getCompanyTel());
            }
            setStatus();
        }
    }

    private void setStatus() {
        iv1.setSelected(isShow == 1);
    }

    @OnClick({R.id.tvGoto, R.id.iv1})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.tvGoto:
                doSave();
                break;
            case R.id.iv1:
                isShow = getIntV(isShow);
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
        if (userInfo == null) {
            return;
        }

        String txt = etTxt.getText().toString();
        if (StringUtils.isEmpty(txt)) {
            ToastUtils.showToast("请输入联系电话");
            return;
        }

        if (txt.length() != 11) {
            ToastUtils.showToast("请输入正确的联系电话");
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("enterpriseLogoPicture", userInfo.getEnterpriseLogoPicture());
            jsonObject.put("enterpriseId", userInfo.getEnterpriseId());
            jsonObject.put("billSureSet", 0);
            jsonObject.put("companyAddress", userInfo.getCompanyAddress());
            jsonObject.put("companyContacts", userInfo.getCompanyContacts());
            jsonObject.put("companyTel", txt);
            jsonObject.put("showAddress", userInfo.getShowAddress());//(0否,1是)
            jsonObject.put("showContacts", userInfo.getShowContacts());
            jsonObject.put("showTel", isShow);

            JSONArray jsonArray = new JSONArray();
            List<UserInfo.EnterpriseLabelIdsBean> labelIdsBeanList = userInfo.getEnterpriseLabelIds();
            if (labelIdsBeanList != null && labelIdsBeanList.size() > 0) {
                for (int i = 0; i < labelIdsBeanList.size(); i++) {
                    if (StringUtils.isNotEmpty(labelIdsBeanList.get(i).getLabelValue())) {
                        jsonArray.put(labelIdsBeanList.get(i).getLabelValue());
                    }
                }
            }
            jsonObject.put("labelEntityList", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showLoading();
        EasyHttp.put(BaseHost.entInfo)
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
