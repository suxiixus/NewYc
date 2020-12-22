package com.app.youcheng.activity.bill;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.dialog.PayEndDialog;
import com.app.youcheng.entity.EventBean;
import com.app.youcheng.entity.PayResult;
import com.app.youcheng.utils.MathUtils;
import com.app.youcheng.utils.NetCodeUtils;
import com.app.youcheng.utils.StringUtils;
import com.app.youcheng.utils.ToastUtils;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.iwgang.countdownview.CountdownView;
import io.reactivex.disposables.Disposable;

import static com.app.youcheng.GlobalConstant.ERROR_401;


public class PayActivity extends BaseActivity {
    @BindView(R.id.ivYue)
    ImageView ivYue;
    @BindView(R.id.ivWechat)
    ImageView ivWechat;
    @BindView(R.id.ivALi)
    ImageView ivALi;
    @BindView(R.id.countdownView)
    CountdownView countdownView;
    @BindView(R.id.tvYue)
    TextView tvYue;

    private int type = 1;
    private String money = "0";
    private String billId;

    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.activity_pay;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("支付订单");
    }

    @Override
    protected void initData() {
        super.initData();

        billId = getIntent().getStringExtra("billId");

        countdownView.start(10 * 60 * 1000);
        setSelect(ivYue);
        getMoney();
    }

    private void setSelect(ImageView iv) {
        ivYue.setSelected(false);
        ivWechat.setSelected(false);
        ivALi.setSelected(false);
        iv.setSelected(true);
    }

    @OnClick({R.id.tvOK, R.id.llYue, R.id.llWeChat, R.id.llAli})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.tvOK:
                if (type == 1) {
                    if (MathUtils.getBigDecimalCompareTo(money, "10", 2) >= 0) {
                        doPay();
                    } else {
                        ToastUtils.showToast("余额不足");
                    }
                } else if (type == 2) {
                    doPay();
                } else if (type == 3) {
                    doPay();
                }
                break;
            case R.id.llYue:
                setSelect(ivYue);
                type = 1;
                break;
            case R.id.llWeChat:
                setSelect(ivWechat);
                type = 2;
                break;
            case R.id.llAli:
                setSelect(ivALi);
                type = 3;
                break;
        }
    }

    @Override
    protected void setListener() {
        super.setListener();

        countdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {

            }
        });
    }

    private void getMoney() {
        if (MyApplication.getApplication().isLogin()) {
            showLoading();
            EasyHttp.get(BaseHost.entMoney)
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
                                        money = data;
                                        tvYue.setText("余额支付(" + money + "元)");
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
    }

    private void doPay() {
        if (StringUtils.isEmpty(billId)) {
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("billId", billId);
            if (type == 2) {
                jsonObject.put("body", "创建账单费用");
                jsonObject.put("subject", "创建账单费用");
            } else if (type == 3) {
                jsonObject.put("body", "创建账单费用");
                jsonObject.put("subject", "创建账单费用");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = BaseHost.localPay;
        if (type == 2) {
            url = BaseHost.wxPay;
        } else if (type == 3) {
            url = BaseHost.aliPay;
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

                                if (type == 1) {
                                    ToastUtils.showToast("支付成功");
                                    finish();
                                } else if (type == 2) {
                                    if (StringUtils.isNotEmpty(data)) {
                                        doWeChat(data);
                                    }
                                } else if (type == 3) {
                                    if (StringUtils.isNotEmpty(data)) {
                                        doAli(data);
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

    private void doWeChat(String orderInfo) {
        try {
            JSONObject json = new JSONObject(orderInfo);

            IWXAPI api = WXAPIFactory.createWXAPI(this, GlobalConstant.WXAPPID);
            PayReq req = new PayReq();
            req.appId = json.getString("appId");
            req.partnerId = json.getString("partnerId");
            req.prepayId = json.getString("prepayId");
            req.nonceStr = json.getString("nonceStr");
            req.timeStamp = json.getString("timeStamp");
            req.packageValue = json.getString("wxPackage");
            req.sign = json.getString("sign");
            api.sendReq(req); //这里就发起调用微信支付了
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doAli(final String orderInfo) {
        // 必须异步调用
        Thread payThread = new Thread(new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(PayActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = GlobalConstant.SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        });
        payThread.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case GlobalConstant.SDK_PAY_FLAG: {
                    //这里接收支付宝的回调信息
                    Log.i("sx", msg.obj.toString());
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        showPayEndDialog();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    private void showPayEndDialog() {
        countdownView.pause();
        PayEndDialog payEndDialog = new PayEndDialog(this);
        payEndDialog.setClicklistener(new PayEndDialog.ClickListenerInterface() {
            @Override
            public void doSure() {
                finish();
            }
        });
        payEndDialog.show();
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

    @Override
    public void onEvent(EventBean eventBean) {
        super.onEvent(eventBean);
        if (eventBean != null) {
            if (eventBean.getType() == 2) {
                showPayEndDialog();
            }
        }
    }

}
