package com.app.youcheng.activity.bill;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.activity.home.CreateBillActivity;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.dialog.BackBillDialog;
import com.app.youcheng.dialog.BillApplyDialog;
import com.app.youcheng.dialog.DeleteTipDialog;
import com.app.youcheng.dialog.PhoneDialog;
import com.app.youcheng.dialog.PingjiaDialog;
import com.app.youcheng.entity.BillOrderBean;
import com.app.youcheng.utils.CommonUtils;
import com.app.youcheng.utils.DateUtils;
import com.app.youcheng.utils.MathUtils;
import com.app.youcheng.utils.NetCodeUtils;
import com.app.youcheng.utils.StringUtils;
import com.app.youcheng.utils.ToastUtils;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.reflect.TypeToken;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

import static com.app.youcheng.GlobalConstant.ERROR_401;


public class BillDetailActivity extends BaseActivity {
    @BindView(R.id.tvTopStatus)
    TextView tvTopStatus;
    @BindView(R.id.tvID)
    TextView tvID;
    @BindView(R.id.tvOverTime)
    TextView tvOverTime;
    @BindView(R.id.tvFirm)
    TextView tvFirm;
    @BindView(R.id.tvPay)
    TextView tvPay;
    @BindView(R.id.tvMoney)
    TextView tvMoney;
    @BindView(R.id.tvZhangQi)
    TextView tvZhangQi;
    @BindView(R.id.tvBillTime)
    TextView tvBillTime;
    @BindView(R.id.tvQiXian)
    TextView tvQiXian;
    @BindView(R.id.tvDeadTime)
    TextView tvDeadTime;
    @BindView(R.id.tvRemark)
    TextView tvRemark;
    @BindView(R.id.tvTopMsg)
    TextView tvTopMsg;

    @BindView(R.id.llApply)
    LinearLayout llApply;
    @BindView(R.id.llBottom)
    LinearLayout llBottom;
    @BindView(R.id.tvUpdate)
    TextView tvUpdate;
    @BindView(R.id.tvBack)
    TextView tvBack;
    @BindView(R.id.tvPayEnd)
    TextView tvPayEnd;
    @BindView(R.id.tvEvaluate)
    TextView tvEvaluate;
    @BindView(R.id.llDelete)
    LinearLayout llDelete;
    @BindView(R.id.tvTipBill)
    TextView tvTipBill;

    @BindView(R.id.llFenZhi)
    LinearLayout llFenZhi;
    @BindView(R.id.tvFenZhi)
    TextView tvFenZhi;
    @BindView(R.id.tvQiYe)
    TextView tvQiYe;
    @BindView(R.id.tvFenNum)
    TextView tvFenNum;

    private String billId;
    private int type = 0;
    private BillOrderBean billOrderBean;

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
        return R.layout.activity_bill_detail;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();

        billId = getIntent().getStringExtra("billId");
        type = getIntent().getIntExtra("type", 0);

        getDetail(true);
    }

    private void getDetail(boolean isShow) {
        if (StringUtils.isEmpty(billId)) {
            return;
        }

        String url = BaseHost.sendBillDetail;
        if (type == 1) {
            url = BaseHost.receiveDetailBill;
        }

        if (isShow) {
            showLoading();
        }
        EasyHttp.get(url + billId)
                .params("billId", billId)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        hideLoading();

                        try {
                            JSONObject object = new JSONObject(s);
                            int code = object.optInt("code");
                            if (code == GlobalConstant.OK) {
                                billOrderBean = gson.fromJson(object.getString("data"), new TypeToken<BillOrderBean>() {
                                }.getType());

                                if (billOrderBean != null) {
                                    tvTopStatus.setText(getStatus(billOrderBean.getReceiveStatus(), type));
                                    tvID.setText(billOrderBean.getBillId());
                                    tvOverTime.setText(billOrderBean.getOverdueTime());
                                    tvPay.setText(billOrderBean.getBillType() == 1 ? "现结" : "月结");//1现结,2月结
                                    tvMoney.setText(billOrderBean.getBillAmountMoney() + "元");

                                    if (billOrderBean.getBillPeriod() > 0) {
                                        tvZhangQi.setText(billOrderBean.getBillPeriod() + "个月");
                                    } else {
                                        tvZhangQi.setText("---");
                                    }

                                    tvBillTime.setText(billOrderBean.getBillTime());

                                    if (billOrderBean.getPaymentTerm() > 0) {
                                        tvQiXian.setText(billOrderBean.getPaymentTerm() + "个工作日");
                                    } else {
                                        tvQiXian.setText("---");
                                    }

                                    tvDeadTime.setText(billOrderBean.getOverdueTime());
                                    if (StringUtils.isNotEmpty(billOrderBean.getNotes())) {
                                        tvRemark.setText(billOrderBean.getNotes());
                                    }
                                    tvTopMsg.setText(getMsg(billOrderBean.getReceiveStatus(), type, billOrderBean.getBillQuestion()));

                                    if (StringUtils.isNotEmpty(billOrderBean.getCompanyRank())) {
                                        if (MathUtils.getBigDecimalCompareTo(billOrderBean.getCompanyRank(), "0", 0) >= 0) {
                                            tvFenNum.setText("+" + billOrderBean.getCompanyRank() + "分");
                                        } else {
                                            tvFenNum.setText(billOrderBean.getCompanyRank() + "分");
                                        }
                                    } else {
                                        tvFenNum.setText("---");
                                    }

                                    if (type == 1) {
                                        tvQiYe.setText("发起企业");
                                        tvFenZhi.setText("我的企业分值");
                                        tvFirm.setText(billOrderBean.getSendEnterpriseName());

                                        if (billOrderBean.getReceiveStatus() == 0) {
                                            llBottom.setVisibility(View.GONE);
                                        } else if (billOrderBean.getReceiveStatus() == 1) {
                                            tvBack.setVisibility(View.VISIBLE);
                                            tvPayEnd.setVisibility(View.VISIBLE);
                                        } else if (billOrderBean.getReceiveStatus() == 2) {
                                            llApply.setVisibility(View.VISIBLE);
                                            tvPayEnd.setVisibility(View.VISIBLE);
                                        } else {
                                            llBottom.setVisibility(View.GONE);
                                        }
                                    } else {
                                        tvFenZhi.setText("客户企业分值");
                                        //(0 已支付,1,未支付,2,已逾期,3申请复核,4待评价,5已评价)
                                        tvFirm.setText(billOrderBean.getReceiveEnterpriseName());

                                        if (billOrderBean.getReceiveStatus() == 4) {
                                            llDelete.setVisibility(View.VISIBLE);
                                            tvEvaluate.setVisibility(View.VISIBLE);
                                        } else {
                                            boolean isDaoqi = false;
//                                            String cur = DateUtils.getDayAfter(DateUtils.getCurFormatTime(), billOrderBean.getPaymentTerm());
//                                            if (!DateUtils.compareDate(cur, billOrderBean.getOverdueTime())) {
//                                                isDaoqi = true;
//                                            }

                                            if (billOrderBean.getBillType() == 1) {
                                                //现结
                                                if (!DateUtils.compareDate(DateUtils.getCurFormatTime(), billOrderBean.getBillTime())) {
                                                    isDaoqi = true;
                                                }
                                            } else {
                                                //月结
                                                String yyy = DateUtils.getCurrent12MonthAfter2(billOrderBean.getBillTime(), billOrderBean.getBillPeriod() + 1);
                                                if (!DateUtils.compareDate(DateUtils.getCurFormatTime(), yyy)) {
                                                    isDaoqi = true;
                                                }
                                            }

                                            //是否显示删除，修改(账期有没有过期)
                                            if (!isDaoqi) {
                                                tvUpdate.setVisibility(View.VISIBLE);
                                                llDelete.setVisibility(View.VISIBLE);
                                            } else {
                                                tvPayEnd.setVisibility(View.VISIBLE);
                                                tvTipBill.setVisibility(View.VISIBLE);
                                            }

                                            if (billOrderBean.getReceiveStatus() == 0
                                                    || billOrderBean.getReceiveStatus() == 1) {
                                                tvPayEnd.setVisibility(View.VISIBLE);
                                            } else if (billOrderBean.getReceiveStatus() == 2) {
                                                tvPayEnd.setVisibility(View.VISIBLE);
                                                tvTipBill.setVisibility(View.GONE);
                                            } else if (billOrderBean.getReceiveStatus() == 3) {
                                                tvUpdate.setVisibility(View.VISIBLE);
                                                tvPayEnd.setVisibility(View.VISIBLE);
                                            } else {
                                                llBottom.setVisibility(View.GONE);
                                            }
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

    private String getStatus(int status, int type) {
        //(0 已支付,1,未支付,2,已逾期,3申请复核,4待评价,5已评价)
        //type 0-1-2
        String str = "";

        if (type == 1) {
            if (status == 0) {
                str = "您已确认付款";
            } else if (status == 1) {
                str = "待确认付款";
            } else if (status == 2) {
                str = "账单已逾期";
            } else if (status == 3) {
                str = "账单被打回";
            } else if (status == 4) {
                str = "双方已确认付款";
            } else if (status == 5) {
                str = "双方已确认付款";
            }
        } else {
            if (status == 0) {
                str = "账单已付款";
            } else if (status == 1) {
                str = "待确认付款";
            } else if (status == 2) {
                str = "账单已逾期";
            } else if (status == 3) {
                str = "账单被打回";
            } else if (status == 4) {
                str = "双方已确认付款";
            } else if (status == 5) {
                str = "双方已确认付款";
            }
        }

        return str;
    }

    private String getMsg(int status, int type, String msg) {
        //(0 已支付,1,未支付,2,已逾期,3申请复核,4待评价,5已评价)
        //type 0-1-2
        String str = "";

        if (type == 1) {
            if (status == 0) {
                str = "您已按期确认付款,企业分值提升";
            } else if (status == 1) {
                str = "您有一张企业订单,请及时确认付款";
            } else if (status == 2) {
                str = "您已逾期付款,每天扣除2分企业分值";
            } else if (status == 3) {
                str = "您对此账单有异议，请求复核";
            } else if (status == 4) {
                str = "账单支付企业按期付款,获得10分企业分值!";
            } else if (status == 5) {
                str = "账单支付企业按期付款,获得10分企业分值!";
            }
        } else {
            if (status == 0) {
                str = "对方已按期确认付款,企业分值提升";
            } else if (status == 1) {
                str = "自动设定已付款后用户有一次修改机会,若用户自行确认已付款则无法修改，确认未付款则开始扣分知道用户确认付款为止!";
            } else if (status == 2) {
                str = "客户账单到期未付款,每天扣除2分账单支付企业分值!";
            } else if (status == 3) {
                if (StringUtils.isNotEmpty(msg)) {
                    str = "客户对此账单有异议，请求复核\n客户描述:" + msg + "\n账单依然在生效请核实后尽快修改，如未及时确认好导致客户误认为扣分可能会被列为不诚实企业";
                }
            } else if (status == 4) {
                str = "账单支付企业按期付款,获得10分企业分值!";
            } else if (status == 5) {
                str = "账单支付企业按期付款,获得10分企业分值!";
            }
        }
        return str;
    }

    @OnClick({R.id.tvUpdate, R.id.tvBack, R.id.tvPayEnd,
            R.id.tvEvaluate, R.id.llDelete, R.id.llCopy,
            R.id.llApply, R.id.tvTipBill})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.tvTipBill:
//                doTip();
                getPhone();
                break;
            case R.id.llApply:
                if (billOrderBean != null) {
                    if (billOrderBean.getAppeal() == 1) {
                        showActivity(AppealActivity.class, null);
                    } else {
                        showBillApplyDialog();
                    }
                }
                break;
            case R.id.tvUpdate:
                if (billOrderBean != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("billOrderBean", billOrderBean);
                    showActivity(CreateBillActivity.class, bundle, 0);
                }
                break;
            case R.id.tvBack:
                showBackBillDialog();
                break;
            case R.id.tvPayEnd:
                if (type == 0) {
                    String cur = DateUtils.getDayAfter(DateUtils.getCurFormatTime(), billOrderBean.getPaymentTerm());
                    if (!DateUtils.compareDate(cur, billOrderBean.getOverdueTime())) {
                        showSelectDate();
                    } else {
                        doPayEnd("", "");
                    }
                } else {
                    doPayEnd("", "");
                }
                break;
            case R.id.tvEvaluate:
                showPingjiaDialog();
                break;
            case R.id.llDelete:
                showDeleteTipDialog();
                break;
            case R.id.llCopy:
                if (StringUtils.isNotEmpty(tvID.getText().toString())) {
                    CommonUtils.copyText(this, tvID.getText().toString());
                    ToastUtils.showToast("已复制");
                }
                break;
        }
    }

    private void showDeleteTipDialog() {
        final DeleteTipDialog deleteTipDialog = new DeleteTipDialog(this, "是否删除改账单？");
        deleteTipDialog.setClicklistener(new DeleteTipDialog.ClickListenerInterface() {
            @Override
            public void doSure() {
                deleteTipDialog.dismiss();
                doDelete();
            }
        });
        deleteTipDialog.show();
    }

    private void showSelectDate() {
        //时间选择器
        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String time = DateUtils.getMyFormatTime("yyyy-MM-dd", date.getTime() + "");
                if (StringUtils.isNotEmpty(time)) {
                    doPayEnd(time, "");
                }
            }
        }).setType(new boolean[]{true, true, true, false, false, false}).build();
        pvTime.show();
    }

    private void doPayEnd(String time, String phone) {
        if (billOrderBean == null) {
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("billId", billOrderBean.getBillId());
            jsonObject.put("billType", type);//(0发送账单,1接收账单)

            if (type == 0 && StringUtils.isNotEmpty(time)) {
                jsonObject.put("payTime", time);
                jsonObject.put("sendStatus", 0);//(0 已支付,1,未支付,2,已逾期,3申请复核)
            } else {
                if (StringUtils.isNotEmpty(phone)) {
                    jsonObject.put("sendStatus", 1);//(0 已支付,1,未支付,2,已逾期,3申请复核)
                } else {
                    jsonObject.put("sendStatus", 0);//(0 已支付,1,未支付,2,已逾期,3申请复核)
                }
            }

            if (StringUtils.isNotEmpty(phone)) {
                jsonObject.put("phone", phone);
            } else {
                jsonObject.put("phone", "");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        showLoading();
        EasyHttp.post(BaseHost.updateStatus)
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
                                ToastUtils.showToast("已确认");
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

    private void doDelete() {
        if (billOrderBean == null) {
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("billId", billOrderBean.getBillId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showLoading();
        EasyHttp.post(BaseHost.deleteBill + billOrderBean.getBillId())
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
                                ToastUtils.showToast("删除成功");
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

    private void getPhone() {
        if (MyApplication.getApplication().isLogin()) {
            showLoading();
            EasyHttp.get(BaseHost.getPhone + billOrderBean.getReceiveEnterpriseId())
                    .execute(new SimpleCallBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            hideLoading();
                            try {
                                JSONObject object = new JSONObject(s);
                                int code = object.optInt("code");
                                if (code == GlobalConstant.OK) {
                                    String data = object.getString("data");
                                    if (StringUtils.isNotEmpty(data)) {
                                        showPhoneDialog(data);
                                    } else {
                                        showPhoneDialog("");
                                    }
                                } else {
                                    showPhoneDialog("");
                                }
                            } catch (JSONException e) {
                                showPhoneDialog("");
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

    private void showPhoneDialog(String phone) {
        PhoneDialog phoneDialog = new PhoneDialog(this, phone);
        phoneDialog.setClicklistener(new PhoneDialog.ClickListenerInterface() {
            @Override
            public void doSure(String s) {
                if (s.length() == 11) {
                    doPayEnd("", s);
                } else {
                    ToastUtils.showToast("请输入正确的手机号码");
                }
            }
        });
        phoneDialog.show();
    }

//    private void doTip() {
//        if (billOrderBean == null) {
//            return;
//        }
//
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("enterpriseId", billOrderBean.getSendEnterpriseId());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        showLoading();
//        EasyHttp.post(BaseHost.noticeBill + billOrderBean.getSendEnterpriseId())
//                .headers("Content-Type", "application/json")
//                .upJson(jsonObject.toString())
//                .execute(new SimpleCallBack<String>() {
//                    @Override
//                    public void onSuccess(String s) {
//                        hideLoading();
//
//                        try {
//                            JSONObject object = new JSONObject(s);
//                            int code = object.optInt("code");
//                            if (code == GlobalConstant.OK) {
//                                ToastUtils.showToast("已提醒");
//                                setResult(RESULT_OK);
//                                finish();
//                            } else {
//                                if (code == ERROR_401) {
//                                    //登录失效
//                                    MyApplication.getApplication().doLogout();
//                                    finish();
//                                } else {
//                                    NetCodeUtils.checkedErrorCode(object.optString("code"), object.optString("msg"));
//                                }
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError(ApiException e) {
//                        hideLoading();
//                        NetCodeUtils.checkedErrorCode(e.getCode(), e.getMessage());
//                    }
//                });
//    }

    private void showBillApplyDialog() {
        BillApplyDialog applyDialog = new BillApplyDialog(this);
        applyDialog.setClicklistener(new BillApplyDialog.ClickListenerInterface() {
            @Override
            public void doSure(String str) {
                doApply(str);
            }
        });
        applyDialog.show();
    }

    private void showPingjiaDialog() {
        PingjiaDialog pingjiaDialog = new PingjiaDialog(this);
        pingjiaDialog.setClicklistener(new PingjiaDialog.ClickListenerInterface() {
            @Override
            public void doSure(String str) {
                doPingjia(str);
            }
        });
        pingjiaDialog.show();
    }

    private void showBackBillDialog() {
        BackBillDialog backBillDialog = new BackBillDialog(this);
        backBillDialog.setClicklistener(new BackBillDialog.ClickListenerInterface() {
            @Override
            public void doSure(String str) {
                doBackBill(str);
            }
        });
        backBillDialog.show();
    }

    private void doBackBill(String str) {
        if (billOrderBean == null) {
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("billId", billOrderBean.getBillId());
            jsonObject.put("question", str);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showLoading();
        EasyHttp.post(BaseHost.returnBill)
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
                                ToastUtils.showToast("操作成功");
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

    private void doPingjia(String str) {
        if (billOrderBean == null) {
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("billId", billOrderBean.getBillId());
            jsonObject.put("adminEvaluationContent", str);
            jsonObject.put("createTime", DateUtils.getCurFormatTime());
            jsonObject.put("evaluationEnterpriseId", billOrderBean.getReceiveEnterpriseId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showLoading();
        EasyHttp.post(BaseHost.evaluationBill)
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
                                ToastUtils.showToast("操作成功");
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

    private void doApply(String str) {
        if (billOrderBean == null) {
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("billId", billOrderBean.getBillId());
            jsonObject.put("question", str);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showLoading();
        EasyHttp.post(BaseHost.checkBill)
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
                                ToastUtils.showToast("操作成功");
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
