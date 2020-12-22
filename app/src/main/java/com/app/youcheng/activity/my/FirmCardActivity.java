package com.app.youcheng.activity.my;


import android.os.Bundle;
import android.view.View;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.R;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.dialog.CardGuizeDialog;
import com.app.youcheng.dialog.CardTipDialog;
import com.app.youcheng.utils.NetCodeUtils;
import com.app.youcheng.utils.ToastUtils;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.OnClick;
import io.reactivex.disposables.Disposable;


public class FirmCardActivity extends BaseActivity {
//    @BindView(R.id.rvRecord)
//    RecyclerView rvRecord;

//    private FirmAccountAdapter recordAdapter;
//    private List<UserAccount> recordList = new ArrayList<>();

    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.activity_my_firm_card;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("我的卡包");
    }

    @Override
    protected void initData() {
        super.initData();

//        initRv();
    }

//    private void initRv() {
//        LinearLayoutManager manager = new LinearLayoutManager(this);
//        rvRecord.setLayoutManager(manager);
//        recordAdapter = new FirmAccountAdapter(this, recordList);
//        recordAdapter.bindToRecyclerView(rvRecord);
//    }

    @OnClick({R.id.llChongShen, R.id.tvGuize})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.llChongShen:
                showCardTipDialog();
                break;
            case R.id.tvGuize:
                showCardGuizeDialog();
                break;
        }
    }

    private void showCardTipDialog() {
        CardTipDialog cardTipDialog = new CardTipDialog(this, "");
        cardTipDialog.setClicklistener(new CardTipDialog.ClickListenerInterface() {
            @Override
            public void doSure() {
                doReset();
            }
        });
        cardTipDialog.show();
    }

    private void showCardGuizeDialog() {
        CardGuizeDialog cardTipDialog = new CardGuizeDialog(this);
        cardTipDialog.show();
    }

    private void doReset() {
        showLoading();
        EasyHttp.post(BaseHost.entReset)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        hideLoading();
                        try {
                            JSONObject object = new JSONObject(s);
                            int code = object.optInt("code");
                            if (code == GlobalConstant.OK) {
                                ToastUtils.showToast("使用成功");
                                doBaseLogout();
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
