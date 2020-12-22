package com.app.youcheng.activity.home;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.activity.bill.PayActivity;
import com.app.youcheng.activity.enterprise.QuanActivity;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.dialog.FileTipDialog;
import com.app.youcheng.dialog.PhoneDialog;
import com.app.youcheng.entity.BillOrderBean;
import com.app.youcheng.entity.FirmDetailBean;
import com.app.youcheng.entity.User;
import com.app.youcheng.utils.DateUtils;
import com.app.youcheng.utils.MathUtils;
import com.app.youcheng.utils.MyTextWatcher;
import com.app.youcheng.utils.NetCodeUtils;
import com.app.youcheng.utils.NumEditTextUtils;
import com.app.youcheng.utils.StringUtils;
import com.app.youcheng.utils.ToastUtils;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

import static com.app.youcheng.GlobalConstant.ERROR_401;


public class CreateBillActivity extends BaseActivity {
    @BindView(R.id.tvFirmName)
    TextView tvFirmName;
    @BindView(R.id.llGoSelect)
    LinearLayout llGoSelect;
    @BindView(R.id.etMoney)
    EditText etMoney;
    @BindView(R.id.iv1)
    ImageView iv1;
    @BindView(R.id.iv2)
    ImageView iv2;
    @BindView(R.id.tvSelectTime)
    TextView tvSelectTime;
    @BindView(R.id.etZhangQi)
    EditText etZhangQi;
    @BindView(R.id.etPayTime)
    EditText etPayTime;
    @BindView(R.id.tvEndTime)
    TextView tvEndTime;
    @BindView(R.id.etRemark)
    EditText etRemark;
    @BindView(R.id.tvTip)
    TextView tvTip;
    @BindView(R.id.llXianJie)
    LinearLayout llXianJie;

    private FirmDetailBean firmDetailBean;
    private BillOrderBean billOrderBean;
    private String dateStr;
    private int billType = 1;//1日结,2月结
    private Calendar calendar = Calendar.getInstance();
    private String name;
    private String myId;
    private User curUser;

    private long lastPressTimeOne = 0;
    private long lastPressTimeTwo = 0;

    private Disposable disposable1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                name = data.getStringExtra("name");
                myId = data.getStringExtra("id");
                tvFirmName.setText(name);
                llGoSelect.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected int getViewId() {
        return R.layout.activity_create_bill;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("创建账单");
        tvGoto.setText("确定");
        tvGoto.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {
        super.initData();

        curUser = MyApplication.getApplication().getCurrentUser();

        dateStr = DateUtils.getCurFormatTime();
        firmDetailBean = (FirmDetailBean) getIntent().getSerializableExtra("firmDetailBean");
        billOrderBean = (BillOrderBean) getIntent().getSerializableExtra("billOrderBean");

        iv1.setSelected(true);
        llXianJie.setVisibility(View.GONE);

        if (firmDetailBean != null) {
            name = firmDetailBean.getEnterpriseInfo().getEnterpriseName();
            myId = firmDetailBean.getEnterpriseInfo().getEnterpriseId();
            tvFirmName.setText(name);
            llGoSelect.setVisibility(View.GONE);
        }

        if (billOrderBean != null) {
            setTitle("修改账单");
            name = billOrderBean.getReceiveEnterpriseName();
            myId = billOrderBean.getReceiveEnterpriseId();
            tvFirmName.setText(name);
            llGoSelect.setVisibility(View.GONE);

            etMoney.setText(billOrderBean.getBillAmountMoney());
            billType = billOrderBean.getBillType();
            dateStr = billOrderBean.getBillTime();
            if (billType == 2) {
                //月结
                iv1.setSelected(false);
                iv2.setSelected(true);
                llXianJie.setVisibility(View.VISIBLE);
                tvSelectTime.setText(billOrderBean.getBillTime().substring(0, billOrderBean.getBillTime().lastIndexOf("-")));
                etZhangQi.setText(billOrderBean.getBillPeriod() + "");
            } else {
                //现结
                iv1.setSelected(true);
                iv2.setSelected(false);
                llXianJie.setVisibility(View.GONE);
                tvSelectTime.setText(billOrderBean.getBillTime());
            }

            etPayTime.setText(billOrderBean.getPaymentTerm() + "");
            tvEndTime.setText(billOrderBean.getOverdueTime());
            tvTip.setText("该账单若无按期确认付款，将在 " + billOrderBean.getOverdueTime() + " 起计算扣除该企业支付诚信价值");
        } else {
            setSelectTime(true);
        }
    }

    @Override
    protected void loadData() {
        super.loadData();

    }

    private void setSelectTime(boolean isDefault) {
//        int day, month;
//
//        String dayTime = etPayTime.getText().toString();
//        if (StringUtils.isNotEmpty(dayTime)) {
//            day = Integer.valueOf(dayTime);
//        } else {
//            day = 0;
//        }
//
//        String monthTime = etZhangQi.getText().toString();
//        if (StringUtils.isNotEmpty(monthTime)) {
//            month = Integer.valueOf(monthTime);
//        } else {
//            month = 0;
//        }

        if (billOrderBean != null) {
            dateStr = billOrderBean.getBillTime();
        } else {
            if (isDefault) {
                if (billType == 1) {
                    dateStr = DateUtils.getCurFormatTime();
                } else {
                    dateStr = DateUtils.getCurFormatMonth() + "-01";
                }
            }
        }

//        String endTime = DateUtils.getDayAfter(dateStr, day);

        if (billType == 1) {
            tvSelectTime.setText(dateStr);
        } else {
            tvSelectTime.setText(dateStr.substring(0, dateStr.lastIndexOf("-")));
//            endTime = DateUtils.getCurrent12MonthAfter(endTime, month + 1);
        }

//        tvEndTime.setText(endTime);
//        tvTip.setText("该账单若无按期确认付款，将在 " + endTime + " 起计算扣除该企业支付诚信价值");
        getEndTime();
    }

    @OnClick({R.id.tvGoto, R.id.llFirm, R.id.ll1, R.id.ll2, R.id.tvSelectTime})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.tvGoto:
                if (billOrderBean != null) {
                    doSend("");
                } else {
                    fileStatus();
                }
                break;
            case R.id.llFirm:
                if (billOrderBean == null) {
                    showActivity(QuanActivity.class, null, 0);
                }
                break;
            case R.id.tvSelectTime:
                if (billOrderBean == null) {
                    if (billType == 1) {

                    } else {
                        showSelectMonth();
                    }
                }
                break;
            case R.id.ll1:
                if (billOrderBean == null) {
                    if (billType != 1) {
                        billType = 1;
                        iv1.setSelected(true);
                        iv2.setSelected(false);
                        llXianJie.setVisibility(View.GONE);
                        setSelectTime(true);
                    }
                }
                break;
            case R.id.ll2:
                if (billOrderBean == null) {
                    if (billType == 1) {
                        billType = 2;
                        iv1.setSelected(false);
                        iv2.setSelected(true);
                        llXianJie.setVisibility(View.VISIBLE);
                        setSelectTime(true);
                    }
                }
                break;
        }
    }

    private void showPhoneDialog(String phone) {
        PhoneDialog phoneDialog = new PhoneDialog(this, phone);
        phoneDialog.setClicklistener(new PhoneDialog.ClickListenerInterface() {
            @Override
            public void doSure(String s) {
                if (s.length() == 11) {
                    doSend(s);
                } else {
                    ToastUtils.showToast("请输入正确的手机号码");
                }
            }
        });
        phoneDialog.show();
    }

    @Override
    protected void setListener() {
        super.setListener();

//        etPayTime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                //当actionId == XX_SEND 或者 XX_DONE时都触发
//                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
//                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
//                if (actionId == EditorInfo.IME_ACTION_SEND
//                        || actionId == EditorInfo.IME_ACTION_DONE
//                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
//                    String charSequence = etPayTime.getText().toString();
//                    if (StringUtils.isNotEmpty(charSequence)) {
//                        if (MathUtils.getBigDecimalCompareTo(charSequence, "0", 0) <= 0) {
//                            ToastUtils.showToast("最小值1个工作日");
//                            etPayTime.setText("1");
//                        }
//                    } else {
//                        ToastUtils.showToast("最小值1个工作日");
//                        etPayTime.setText("1");
//                    }
//                    setSelectTime(false);
//                }
//                return false;
//            }
//        });
//
//        etZhangQi.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                //当actionId == XX_SEND 或者 XX_DONE时都触发
//                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
//                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
//                if (actionId == EditorInfo.IME_ACTION_SEND
//                        || actionId == EditorInfo.IME_ACTION_DONE
//                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
//                    String charSequence = etZhangQi.getText().toString();
//                    if (StringUtils.isNotEmpty(charSequence)) {
//                        if (MathUtils.getBigDecimalCompareTo(charSequence, "0", 0) <= 0) {
//                            ToastUtils.showToast("最小值1个月");
//                            etZhangQi.setText("1");
//                        } else if (MathUtils.getBigDecimalCompareTo(charSequence, "12", 0) > 0) {
//                            ToastUtils.showToast("不能大于12个月");
//                            etZhangQi.setText("12");
//                        }
//                    } else {
//                        ToastUtils.showToast("最小值1个月");
//                        etZhangQi.setText("1");
//                    }
//                    setSelectTime(false);
//                }
//                return false;
//            }
//        });

        etPayTime.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                lastPressTimeTwo = System.currentTimeMillis();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doChangePayTime();
                    }
                }, 801);
            }
        });

        etZhangQi.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                lastPressTimeOne = System.currentTimeMillis();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doChangeZhangQi();
                    }
                }, 801);
            }
        });

        etMoney.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (NumEditTextUtils.isVaildText(etMoney, 2)) {

                }
            }
        });
    }

    private void doChangePayTime() {
        long now = System.currentTimeMillis();
        if (now - lastPressTimeTwo > 800) {
            String charSequence = etPayTime.getText().toString();
            if (StringUtils.isNotEmpty(charSequence)) {
                if (MathUtils.getBigDecimalCompareTo(charSequence, "0", 0) <= 0) {
                    ToastUtils.showToast("最小值1个工作日");
                    etPayTime.setText("1");
                }
            }
            setSelectTime(false);
        }
    }

    private void doChangeZhangQi() {
        long now = System.currentTimeMillis();
        if (now - lastPressTimeOne > 800) {
            String charSequence = etZhangQi.getText().toString();
            if (StringUtils.isNotEmpty(charSequence)) {
                if (MathUtils.getBigDecimalCompareTo(charSequence, "0", 0) <= 0) {
                    ToastUtils.showToast("最小值1个月");
                    etZhangQi.setText("1");
                } else if (MathUtils.getBigDecimalCompareTo(charSequence, "12", 0) > 0) {
                    ToastUtils.showToast("不能大于12个月");
                    etZhangQi.setText("12");
                }
            }
            setSelectTime(false);
        }
    }

//    private void showSelectDate() {
//        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//
//                if (monthOfYear + 1 <= 9) {
//                    month = "0" + (monthOfYear + 1);
//                } else {
//                    month = String.valueOf(monthOfYear + 1);
//                }
//                if (dayOfMonth <= 9) {
//                    day = "0" + dayOfMonth;
//                } else {
//                    day = String.valueOf(dayOfMonth);
//                }
//                dateStr = String.valueOf(year) + "-" + month + "-" + day;
//
//                tvSelectTime.setText(dateStr);
//                String endTime = DateUtils.getCurrent12MonthAfter(dateStr, 1);
//                tvEndTime.setText(endTime);
//                tvTip.setText("该账单若无按期确认付款，将在" + endTime + "起计算扣除该企业支付诚信价值");
//            }
//        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
//                calendar.get(Calendar.DAY_OF_MONTH));
//
//        datePickerDialog.show();
//    }

    private void showSelectMonth() {
        //时间选择器
        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String time = DateUtils.getMyFormatTime("yyyy-MM", date.getTime() + "");
                if (StringUtils.isNotEmpty(time)) {
                    dateStr = time + "-01";
                    setSelectTime(false);
                }
            }
        }).setType(new boolean[]{true, true, false, false, false, false}).build();
        pvTime.show();

//        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//
//                String month;
//                if (monthOfYear + 1 <= 9) {
//                    month = "0" + (monthOfYear + 1);
//                } else {
//                    month = String.valueOf(monthOfYear + 1);
//                }
//
//                dateStr = String.valueOf(year) + "-" + month + "-01";
//                setSelectTime(false);
//            }
//        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
//                calendar.get(Calendar.DAY_OF_MONTH));
//        datePickerDialog.show();
    }

    private void doSend(String phone) {
        if (StringUtils.isEmpty(myId)) {
            ToastUtils.showToast("请选择企业");
            return;
        }

        String billAmountMoney = etMoney.getText().toString();
        String billTime = tvSelectTime.getText().toString();
        String overdueTime = tvEndTime.getText().toString();
        String paymentTerm = etPayTime.getText().toString();
        String notes = etRemark.getText().toString();
        String billPeriod = etZhangQi.getText().toString();

        if (StringUtils.isNotEmpty(billAmountMoney, overdueTime)) {

        } else {
            ToastUtils.showToast("请完善信息");
            return;
        }

        if (billType == 2) {
            //月结
            if (StringUtils.isEmpty(billPeriod)) {
                ToastUtils.showToast("请完善信息");
                return;
            }

            billTime = billTime + "-01";
        } else {
            //现结
            if (StringUtils.isEmpty(paymentTerm)) {
                ToastUtils.showToast("请完善信息");
                return;
            }
        }

        if (MathUtils.getBigDecimalCompareTo(billAmountMoney, "0", 2) <= 0) {
            ToastUtils.showToast("金额必须大于0");
            return;
        }

        if (DateUtils.compareDate2(overdueTime, DateUtils.getCurFormatTime())) {
            ToastUtils.showToast("账单到期日不能创建在当天时间之前");
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("billAmountMoney", billAmountMoney);
            if (billType == 2) {
                jsonObject.put("billPeriod", billPeriod);
            } else {
                jsonObject.put("billPeriod", 0);
            }
            if (billOrderBean != null) {
//                jsonObject.put("billTime", billOrderBean.getBillTime());
            } else {
                jsonObject.put("phone", phone);
                jsonObject.put("billTime", billTime);
            }
            jsonObject.put("billType", billType);
            jsonObject.put("notes", notes);
            if (StringUtils.isNotEmpty(paymentTerm)) {
                jsonObject.put("paymentTerm", paymentTerm);
            } else {
                jsonObject.put("paymentTerm", 0);
            }

            if (billOrderBean != null) {
                jsonObject.put("billId", billOrderBean.getBillId());
            } else {
                jsonObject.put("receiveEnterpriseId", myId);
                jsonObject.put("billQuestion", "");
            }

            jsonObject.put("overdueTime", overdueTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = BaseHost.sendBill;
        if (billOrderBean != null) {
            url = BaseHost.updateBill;
        }

        showLoading();
        EasyHttp.post(url)
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
                                String data = object.getString("data");

                                if (billOrderBean != null) {
                                    //修改账单
                                    ToastUtils.showToast("已发送");
                                    setResult(RESULT_OK);
                                    finish();
                                } else {
                                    //发送账单
                                    if (StringUtils.isNotEmpty(data)) {
                                        if (data.equals("0")) {
                                            ToastUtils.showToast("已发送");
                                            finish();
                                        } else {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("billId", data);
                                            showActivity(PayActivity.class, bundle);
                                            finish();
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

    private void fileStatus() {
        if (StringUtils.isEmpty(myId)) {
            ToastUtils.showToast("请选择企业");
            return;
        }

        if (MyApplication.getApplication().isLogin()) {
            showLoading();
            EasyHttp.get(BaseHost.fileStatus + myId)
                    .execute(new SimpleCallBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            hideLoading();
                            try {
                                JSONObject object = new JSONObject(s);
                                int code = object.optInt("code");
                                if (code == GlobalConstant.OK) {
                                    int data = object.optInt("data");
                                    if (data == 1) {
                                        getPhone();
                                    } else {
                                        showFileTipDialog();
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

    private void getPhone() {
        if (StringUtils.isEmpty(myId)) {
            ToastUtils.showToast("请选择企业");
            return;
        }

        String billAmountMoney = etMoney.getText().toString();
        String billTime = tvSelectTime.getText().toString();
        String overdueTime = tvEndTime.getText().toString();
        String paymentTerm = etPayTime.getText().toString();
        String billPeriod = etZhangQi.getText().toString();

        if (StringUtils.isNotEmpty(billAmountMoney, overdueTime)) {

        } else {
            ToastUtils.showToast("请完善信息");
            return;
        }

        if (billType == 2) {
            //月结
            if (StringUtils.isEmpty(billPeriod)) {
                ToastUtils.showToast("请完善信息");
                return;
            }

            billTime = billTime + "-01";
        } else {
            //现结
            if (StringUtils.isEmpty(paymentTerm)) {
                ToastUtils.showToast("请完善信息");
                return;
            }
        }

        if (MathUtils.getBigDecimalCompareTo(billAmountMoney, "0", 2) <= 0) {
            ToastUtils.showToast("金额必须大于0");
            return;
        }

        if (DateUtils.compareDate2(overdueTime, DateUtils.getCurFormatTime())) {
            ToastUtils.showToast("账单到期日不能创建在当天时间之前");
            return;
        }

        if (MyApplication.getApplication().isLogin()) {
            showLoading();
            EasyHttp.get(BaseHost.getPhone + myId)
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

    private void getEndTime() {
        String billTime = tvSelectTime.getText().toString();
        String paymentTerm = "0";
        if (StringUtils.isNotEmpty(etPayTime.getText().toString())) {
            paymentTerm = etPayTime.getText().toString();
        }
        String billPeriod = "0";
        if (StringUtils.isNotEmpty(etZhangQi.getText().toString())) {
            billPeriod = etZhangQi.getText().toString();
        }

        if (billType == 1) {
            billPeriod = "0";
        }

        if (MyApplication.getApplication().isLogin()) {
            showLoading();
            EasyHttp.get(BaseHost.getEndTime)
                    .params("billTime", billTime)
                    .params("billPeriod", billPeriod)
                    .params("billType", billType + "")
                    .params("overPayTime", paymentTerm)
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
                                        tvEndTime.setText(data);
                                        tvTip.setText("该账单若无按期确认付款，将在 " + data + " 起计算扣除该企业支付诚信价值");
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

    private void showFileTipDialog() {
        final FileTipDialog fileTipDialog = new FileTipDialog(this);
        fileTipDialog.setClicklistener(new FileTipDialog.ClickListenerInterface() {
            @Override
            public void doSure() {
                fileTipDialog.dismiss();
                showActivity(DanganActivity.class, null);
            }
        });
        fileTipDialog.show();
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
