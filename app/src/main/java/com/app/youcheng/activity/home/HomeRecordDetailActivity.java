package com.app.youcheng.activity.home;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.adapter.FirmSayAdapter;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.entity.FirmDetailBean;
import com.app.youcheng.entity.User;
import com.app.youcheng.utils.CommonUtils;
import com.app.youcheng.utils.NetCodeUtils;
import com.app.youcheng.utils.StringUtils;
import com.app.youcheng.utils.ToastUtils;
import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

import static com.app.youcheng.GlobalConstant.ERROR_401;


public class HomeRecordDetailActivity extends BaseActivity {
    @BindView(R.id.llBottom)
    LinearLayout llBottom;
    @BindView(R.id.rvRecord)
    RecyclerView rvRecord;
    @BindView(R.id.ivLogo)
    ImageView ivLogo;
    @BindView(R.id.ivStatus)
    ImageView ivStatus;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvStatus)
    TextView tvStatus;
    @BindView(R.id.tvMan)
    TextView tvMan;
    @BindView(R.id.tvRank)
    TextView tvRank;
    @BindView(R.id.tvOverDue)
    TextView tvOverDue;
    @BindView(R.id.tvAdd)
    TextView tvAdd;
    @BindView(R.id.tvPhone)
    TextView tvPhone;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.tvReason)
    TextView tvReason;
    @BindView(R.id.tvFen)
    TextView tvFen;
    @BindView(R.id.tvLabel)
    TextView tvLabel;
    @BindView(R.id.llPingJia)
    LinearLayout llPingJia;

    private String enterpriseId;
    private FirmSayAdapter recordAdapter;
    private FirmDetailBean firmDetailBean;
    private List<FirmDetailBean.EvaluationBean> recordList = new ArrayList<>();
    private int friends = 0;
    private User user;
    private int type = 1;

    private String curPhone = "";
    private String curAddress = "";

    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.activity_my_msg;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("企业详情");
    }

    @Override
    protected void initData() {
        super.initData();

        user = MyApplication.getApplication().getCurrentUser();
        type = getIntent().getIntExtra("type", 1);
        enterpriseId = getIntent().getStringExtra("enterpriseId");
        initRv();

        if (type == 0) {
            if (user != null) {
                enterpriseId = user.getUserInfo().getEnterpriseId();
            }
            llBottom.setVisibility(View.GONE);
        }

        getDetail(true);
    }

    private void initRv() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvRecord.setLayoutManager(manager);
        recordAdapter = new FirmSayAdapter(this, recordList);
        recordAdapter.bindToRecyclerView(rvRecord);
        recordAdapter.setEmptyView(R.layout.empty_no_message);
    }

    private void getDetail(boolean isShow) {
        if (StringUtils.isEmpty(enterpriseId)) {
            return;
        }

        if (isShow) {
            showLoading();
        }
        EasyHttp.get(BaseHost.homeDetail + enterpriseId)
                .params("enterpriseId", enterpriseId)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        hideLoading();

                        try {
                            JSONObject object = new JSONObject(s);
                            int code = object.optInt("code");
                            if (code == GlobalConstant.OK) {
                                firmDetailBean = gson.fromJson(object.getString("data"), new TypeToken<FirmDetailBean>() {
                                }.getType());

                                if (firmDetailBean != null) {
                                    if (type == 1) {
                                        //(0:否,1:是)
                                        friends = firmDetailBean.getFriends();
                                        if (friends == 0) {
                                            tvAdd.setText("添加企业圈");
                                        } else {
                                            tvAdd.setText("移除企业圈");
                                        }
                                    }

                                    String txt = "";
                                    List<FirmDetailBean.LabelEntityBean> labelIdsBeanList = firmDetailBean.getLabelEntities();
                                    if (labelIdsBeanList != null && labelIdsBeanList.size() > 0) {
                                        txt = labelIdsBeanList.get(0).getLabelValue();
                                        for (int i = 1; i < labelIdsBeanList.size(); i++) {
                                            txt = txt + " " + labelIdsBeanList.get(i).getLabelValue();
                                        }
                                    }

                                    tvLabel.setText(txt);

                                    FirmDetailBean.EnterpriseInfoBean enterpriseInfoBean = firmDetailBean.getEnterpriseInfo();
                                    if (enterpriseInfoBean != null) {
                                        if (StringUtils.isNotEmpty(enterpriseInfoBean.getEnterpriseLogoPicture())) {
                                            Glide.with(HomeRecordDetailActivity.this).load(enterpriseInfoBean.getEnterpriseLogoPicture()).into(ivLogo);
                                        }

                                        tvName.setText(enterpriseInfoBean.getEnterpriseName());
                                        tvStatus.setText(getStatus(enterpriseInfoBean.getStatus()));
                                        tvStatus.setTextColor(enterpriseInfoBean.getStatus() == 1 ? getResources().getColor(R.color.white) :
                                                getResources().getColor(R.color.font_main_black));
                                        tvStatus.setBackgroundColor(enterpriseInfoBean.getStatus() == 1 ? getResources().getColor(R.color.main_blue) :
                                                getResources().getColor(R.color.main_btn_gray));

                                        if (StringUtils.isNotEmpty(enterpriseInfoBean.getCompanyContacts()))
                                            tvMan.setText(enterpriseInfoBean.getCompanyContacts());

                                        tvRank.setText(enterpriseInfoBean.getEnterpriseRank() + "");
                                        tvOverDue.setText(enterpriseInfoBean.getOverdueLoan());

                                        if (StringUtils.isNotEmpty(enterpriseInfoBean.getCompanyScore())) {
                                            //0 优质企业 1，虚假企业 2 不诚实企业
                                            if (enterpriseInfoBean.getCompanyScore().equals("1")) {
                                                ivStatus.setImageResource(R.mipmap.icon_home_status_xujia);
                                            } else if (enterpriseInfoBean.getCompanyScore().equals("2")) {
                                                ivStatus.setImageResource(R.mipmap.icon_home_status_bushi);
                                            } else {
                                                ivStatus.setImageResource(R.mipmap.icon_home_status_xing);
                                            }
                                        } else {
                                            ivStatus.setImageResource(R.drawable.shape_bg_transparent);
                                        }

                                        if (StringUtils.isNotEmpty(enterpriseInfoBean.getCompanyContacts(), enterpriseInfoBean.getCompanyTel())) {
                                            tvPhone.setText(enterpriseInfoBean.getCompanyContacts() + " " + enterpriseInfoBean.getCompanyTel());
                                            curPhone = enterpriseInfoBean.getCompanyTel();
                                        }

                                        if (StringUtils.isNotEmpty(enterpriseInfoBean.getCompanyAddress())) {
                                            tvAddress.setText(enterpriseInfoBean.getCompanyAddress());
                                            curAddress = enterpriseInfoBean.getCompanyAddress();
                                        }
                                    }

                                    recordList.clear();
                                    List<FirmDetailBean.EvaluationBean> list = firmDetailBean.getEvaluation();
                                    if (list != null) {
                                        recordList.addAll(list);
                                        llPingJia.setVisibility(View.VISIBLE);
                                    } else {
                                        llPingJia.setVisibility(View.INVISIBLE);
                                    }
                                    recordAdapter.notifyDataSetChanged();

                                    List<FirmDetailBean.EnterpriseRankBean> rankBeanList = firmDetailBean.getEnterpriseRank();
                                    if (rankBeanList != null && rankBeanList.size() > 0) {
                                        FirmDetailBean.EnterpriseRankBean rankBean = rankBeanList.get(0);
                                        if (rankBean != null) {
                                            tvReason.setText(getName(rankBean.getSettlementReason()));
                                            tvFen.setText(getFen(rankBean.getSettlementReason(), rankBean.getRankCalculation()));
                                            tvFen.setTextColor(getResColor(rankBean.getSettlementReason(), rankBean.getRankCalculation()));
                                        }
                                    }
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

    private String getName(int type) {
        //0逾期确认，1按期支付,2平台增加，3平台扣除
        String str = "---";

        if (type == 0) {
            str = "逾期确认";
        } else if (type == 1) {
            str = "按期支付";
        } else if (type == 2) {
            str = "平台增加";
        } else if (type == 3) {
            str = "平台扣除";
        }

        return str;
    }

    private String getFen(int type, int fen) {
        //0逾期确认，1按期支付,2平台增加，3平台扣除
        String str = fen + "分";
        if (type == 0) {
            str = fen + "分";
        } else if (type == 1) {
            str = "+" + fen + "分";
        } else if (type == 2) {
            str = "+" + fen + "分";
        } else if (type == 3) {
            str = fen + "分";
        }
        return str;
    }

    private int getResColor(int type, int fen) {
        //0逾期确认，1按期支付,2平台增加，3平台扣除
        if (type == 0) {
            return getResources().getColor(R.color.font_red);
        } else if (type == 1) {
            return getResources().getColor(R.color.font_green);
        } else if (type == 2) {
            return getResources().getColor(R.color.font_green);
        } else if (type == 3) {
            return getResources().getColor(R.color.font_red);
        } else {
            return getResources().getColor(R.color.font_main_black);
        }
    }

    //0:未注册，1已注册,2未通过审核
    private String getStatus(int status) {
        String str = "";
        if (status == 0) {
            str = "未注册";
        } else if (status == 1) {
            str = "已注册";
        } else if (status == 2) {
            str = "未通过审核";
        }

        return str;
    }

    @OnClick({R.id.llRank, R.id.tvAdd, R.id.tvSend,
            R.id.llPhone, R.id.llAddress})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.llPhone:
                if (StringUtils.isNotEmpty(curPhone)) {
                    CommonUtils.copyText(this, curPhone);
                    ToastUtils.showToast("已复制");
                }
                break;
            case R.id.llAddress:
                if (StringUtils.isNotEmpty(curAddress)) {
                    CommonUtils.copyText(this, curAddress);
                    ToastUtils.showToast("已复制");
                }
                break;
            case R.id.llRank:
                if (MyApplication.getApplication().isLogin()) {
                    if (firmDetailBean != null) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("firmDetailBean", firmDetailBean);
                        showActivity(FirmRankActivity.class, bundle);
                    }
                } else {
                    ToastUtils.showToast("请先登录");
                }
                break;
            case R.id.tvAdd:
                if (MyApplication.getApplication().isLogin()) {
                    if (friends == 0) {
                        doAdd();
                    } else {
                        doDelete();
                    }
                } else {
                    ToastUtils.showToast("请先登录");
                }
                break;
            case R.id.tvSend:
                if (MyApplication.getApplication().isLogin()) {
                    if (firmDetailBean != null) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("firmDetailBean", firmDetailBean);
                        showActivity(CreateBillActivity.class, bundle);
                    }
                } else {
                    ToastUtils.showToast("请先登录");
                }
                break;
        }
    }

    private void doAdd() {
        if (firmDetailBean == null) {
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray ja = new JSONArray();
            ja.put(firmDetailBean.getEnterpriseInfo().getEnterpriseId());
            jsonObject.put("friendId", ja);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        showLoading();
        EasyHttp.post(BaseHost.friendsAdd)
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
                                friends = 1;
                                tvAdd.setText("移除企业圈");
                                ToastUtils.showToast("添加成功");
                                setResult(RESULT_OK);
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

    private void doDelete() {
        if (firmDetailBean == null) {
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", firmDetailBean.getEnterpriseInfo().getEnterpriseId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showLoading();
        EasyHttp.post(BaseHost.friendsDelete + firmDetailBean.getEnterpriseInfo().getEnterpriseId())
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
                                friends = 0;
                                tvAdd.setText("添加企业圈");
                                ToastUtils.showToast("移除成功");
                                setResult(RESULT_OK);
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
