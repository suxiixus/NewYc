package com.app.youcheng.activity.my;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.entity.User;
import com.app.youcheng.entity.UserInfo;
import com.app.youcheng.utils.StringUtils;
import com.bumptech.glide.Glide;
import com.zhouyou.http.EasyHttp;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;


public class FirmSetActivity extends BaseActivity {
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvCode)
    TextView tvCode;
    @BindView(R.id.tvLabel)
    TextView tvLabel;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tvDefaultSet)
    TextView tvDefaultSet;
    @BindView(R.id.tvMan)
    TextView tvMan;
    @BindView(R.id.tvPhone)
    TextView tvPhone;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.iv1)
    ImageView iv1;
    @BindView(R.id.iv2)
    ImageView iv2;
    @BindView(R.id.iv3)
    ImageView iv3;
    @BindView(R.id.iv4)
    ImageView iv4;
    @BindView(R.id.tvUpdateName)
    TextView tvUpdateName;

    private Disposable disposable1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @Override
    protected int getViewId() {
        return R.layout.activity_my_firm_set;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("我的企业");
        tvGoto.setVisibility(View.VISIBLE);
        tvGoto.setText("修改");
    }

    @Override
    protected void initData() {
        super.initData();

        User user = MyApplication.getApplication().getCurrentUser();
        if (user != null) {
            UserInfo userInfo = user.getUserInfo();
            if (userInfo != null) {
                tvName.setText(userInfo.getEnterpriseName());
                tvCode.setText(userInfo.getEnterpriseCode());
                tvTime.setText(userInfo.getRegisterTime());
                tvMan.setText(userInfo.getCompanyContacts());
                tvPhone.setText(userInfo.getCompanyTel());
                tvAddress.setText(userInfo.getCompanyAddress());

                String txt = "";
                List<UserInfo.EnterpriseLabelIdsBean> labelIdsBeanList = userInfo.getEnterpriseLabelIds();
                if (labelIdsBeanList != null && labelIdsBeanList.size() > 0) {
                    txt = labelIdsBeanList.get(0).getLabelValue();
                    for (int i = 1; i < labelIdsBeanList.size(); i++) {
                        txt = txt + " " + labelIdsBeanList.get(i).getLabelValue();
                    }
                }
                if (StringUtils.isNotEmpty(txt)) {
                    tvLabel.setText(txt);
                }

                if (StringUtils.isNotEmpty(userInfo.getBusinessLicensePicture())) {
                    Glide.with(this).load(userInfo.getBusinessLicensePicture()).into(iv1);
                }

                if (StringUtils.isNotEmpty(userInfo.getEnterpriseLogoPicture())) {
                    Glide.with(this).load(userInfo.getEnterpriseLogoPicture()).into(iv2);
                }

                if (StringUtils.isNotEmpty(userInfo.getCardPictureFront())) {
                    Glide.with(this).load(userInfo.getCardPictureFront()).into(iv3);
                }

                if (StringUtils.isNotEmpty(userInfo.getCardPictureBack())) {
                    Glide.with(this).load(userInfo.getCardPictureBack()).into(iv4);
                }
            }
        }
    }

    @OnClick({R.id.tvUpdateName, R.id.tvGoto})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.tvUpdateName:
                showActivity(FirmUpdateNameActivity.class, null);
                break;
            case R.id.tvGoto:
                showActivity(FirmChangeActivity.class, null, 0);
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
