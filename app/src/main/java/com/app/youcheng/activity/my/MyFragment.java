package com.app.youcheng.activity.my;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.activity.home.HomeRecordDetailActivity;
import com.app.youcheng.activity.login.LoginActivity;
import com.app.youcheng.activity.set.SetActivity;
import com.app.youcheng.base.BaseFragment;
import com.app.youcheng.entity.EventBean;
import com.app.youcheng.entity.User;
import com.app.youcheng.entity.UserInfo;
import com.app.youcheng.utils.EventBusUtils;
import com.app.youcheng.utils.NetCodeUtils;
import com.app.youcheng.utils.StringUtils;
import com.app.youcheng.widget.CircleImageView;
import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.app.youcheng.GlobalConstant.ERROR_401;

public class MyFragment extends BaseFragment {
    @BindView(R.id.llDefault)
    LinearLayout llDefault;
    @BindView(R.id.llLogin)
    LinearLayout llLogin;
    @BindView(R.id.tvTopName)
    TextView tvTopName;
    @BindView(R.id.ivStyle)
    ImageView ivStyle;
    @BindView(R.id.tvNickName)
    TextView tvNickName;
    @BindView(R.id.tvUserType)
    TextView tvUserType;
    //    @BindView(R.id.tvAccountMoney)
//    TextView tvAccountMoney;
    @BindView(R.id.tvSendMoney)
    TextView tvSendMoney;
    @BindView(R.id.tvCompanyRank)
    TextView tvCompanyRank;
    @BindView(R.id.ivLogo)
    CircleImageView ivLogo;
//    private boolean isNeed = false;

    @Override
    public void onStart() {
        super.onStart();
//        if (isNeed) {
        getInfo();
//        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getInfo();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    getInfo();
                    break;
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        super.initViews(savedInstanceState);
        setTitle("我的");
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @Override
    protected void loadData() {
        super.loadData();
        if (isFirst) {
            getInfo();
            isFirst = false;
        }
    }

    private void getInfo() {
//        isNeed = false;
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

                                            setLoginStatus(user);
                                        }
                                    }
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
    protected String getmTag() {
        return getClass().getSimpleName();
    }


    @OnClick({R.id.ivSet, R.id.llMsg, R.id.llFirmSet,
            R.id.llFirmAccount, R.id.llBill, R.id.llCard,
            R.id.llDefault, R.id.ivShare, R.id.llMyTop, R.id.ivFen})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.ivFen:
                showActivity(RankMsgActivity.class, null);
                break;
            case R.id.ivSet:
                //设置
                if (MyApplication.getApplication().isLogin()) {
                    showActivity(SetActivity.class, null, 0);
                } else {
                    showActivity(LoginActivity.class, null, 0);
                }
                break;
            case R.id.llMsg:
                //企业诚信
                Bundle bundle = new Bundle();
                bundle.putInt("type", 0);
                showActivity(HomeRecordDetailActivity.class, bundle);
                break;
            case R.id.llFirmSet:
                //企业信息完善
                showActivity(FirmSetActivity.class, null, 0);
                break;
            case R.id.llFirmAccount:
                //账号管理
                showActivity(FirmAccountActivity.class, null);
                break;
            case R.id.llBill:
                //消费记录
                showActivity(FirmBillActivity.class, null);
                break;
            case R.id.llCard:
                //我的卡包
                showActivity(FirmCardActivity.class, null);
                break;
            case R.id.llDefault:
                showActivity(ShareActivity.class, null);
                break;
            case R.id.ivShare:
                showActivity(ShareActivity.class, null);
                break;
            case R.id.llMyTop:
                if (!MyApplication.getApplication().isLogin()) {
                    showActivity(LoginActivity.class, null, 0);
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        if (eventBean != null) {
            if (eventBean.getType() == 0) {
                //登录成功
//                isNeed = true;
            } else if (eventBean.getType() == 1) {
                //退出登录
                setLogOutStatus();
            }
        }
    }

    private void setLoginStatus(User user) {
        llDefault.setVisibility(View.GONE);
        llLogin.setVisibility(View.VISIBLE);
        tvTopName.setText(user.getUserInfo().getEnterpriseName());
        tvTopName.setTextColor(getResources().getColor(R.color.font_main_black));
        tvNickName.setText(user.getUserInfo().getNickName());
        tvUserType.setText(getStatus(user.getUserInfo().getUserType()));
//        tvAccountMoney.setText(user.getUserInfo().getAccountMoney());
        tvSendMoney.setText(user.getUserInfo().getSendMoney());
        tvCompanyRank.setText(user.getUserInfo().getCompanyRank() + "");
        ivStyle.setVisibility(View.VISIBLE);

        if (StringUtils.isNotEmpty(user.getUserInfo().getCompanyScore())) {
            //0 优质企业 1，虚假企业 2 不诚实企业
            if (user.getUserInfo().getCompanyScore().equals("1")) {
                ivStyle.setImageResource(R.mipmap.icon_quan_type_2);
            } else if (user.getUserInfo().getCompanyScore().equals("2")) {
                ivStyle.setImageResource(R.mipmap.icon_quan_type_3);
            } else {
                ivStyle.setImageResource(R.mipmap.icon_my_youzhi);
            }
        } else {
            ivStyle.setImageResource(R.drawable.shape_bg_transparent);
        }

        if (StringUtils.isNotEmpty(user.getUserInfo().getEnterpriseLogoPicture())) {
            Glide.with(this).load(user.getUserInfo().getEnterpriseLogoPicture()).into(ivLogo);
        }
    }

    private String getStatus(int type) {
        //00系统用户 01 客服，02 用户管理员，03 企业员工
        String status = "";
        if (type == 0) {
            status = "系统用户";
        } else if (type == 1) {
            status = "客服";
        } else if (type == 2) {
            status = "用户管理员";
        } else if (type == 3) {
            status = "企业员工";
        }

        return status;
    }

    private void setLogOutStatus() {
        llDefault.setVisibility(View.VISIBLE);
        llLogin.setVisibility(View.GONE);
        tvTopName.setText("点击登录");
        tvTopName.setTextColor(getResources().getColor(R.color.font_blue));
        tvNickName.setText("***");
        tvUserType.setText("***");
//        tvAccountMoney.setText("0");
        tvSendMoney.setText("0");
        tvCompanyRank.setText("0");
        ivStyle.setVisibility(View.GONE);
        ivLogo.setImageResource(R.mipmap.icon_my_header);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBusUtils.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusUtils.unRegister(this);
    }


}
