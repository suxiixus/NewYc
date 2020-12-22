package com.app.youcheng.activity.login;

import android.view.KeyEvent;
import android.view.View;

import com.app.youcheng.R;
import com.app.youcheng.base.BaseFragment;
import com.zhouyou.http.EasyHttp;

import io.reactivex.disposables.Disposable;


public class SignOneFragment extends BaseFragment {

    private Disposable disposable1;

    public static SignOneFragment getInstance() {
        SignOneFragment fragment = new SignOneFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sign_one;
    }

    @Override
    protected void initData() {
        super.initData();
        llTitle.setVisibility(View.GONE);
    }

    @Override
    protected void loadData() {
        super.loadData();

    }

    @Override
    protected String getmTag() {
        return getClass().getSimpleName();
    }

    @Override
    protected void setListener() {
        super.setListener();

    }


    private void getBillRecord(boolean isShow) {
//        if (cloudCoinBean != null) {
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("coinType", cloudCoinBean.getCoinType());
//                jsonObject.put("mainCoinType", cloudCoinBean.getMainCoinType());
//                jsonObject.put("pageNo", pageNo);
//                jsonObject.put("pageSize", GlobalConstant.pageSize);
//                jsonObject.put("startTime", "");
//                jsonObject.put("endTime", "");
//
//                JSONArray jaTradeTypes = new JSONArray();
//                if (type == -1) {
//
//                } else {
//                    jaTradeTypes.put(type);
//                }
//                jsonObject.put("tradeTypes", jaTradeTypes);//0：充币 1：提币
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            if (isShow) {
//                showLoading();
//            }
//
//            disposable1 = EasyHttp.post(BaseHost.cloudTransactionUrl)
//                    .headers("Content-Type", "application/json")
//                    .upJson(jsonObject.toString())
//                    .execute(new SimpleCallBack<String>() {
//                        @Override
//                        public void onSuccess(String s) {
//                            hideLoading();
//                            try {
//                                JSONObject object = new JSONObject(s);
//                                int code = object.optInt("code");
//                                if (code == OK) {
//                                    JSONObject data = object.getJSONObject("data");
//                                    List<CloudRecordBean> cloudRecordBeans = gson.fromJson(data.getString("records"), new TypeToken<List<CloudRecordBean>>() {
//                                    }.getType());
//
//                                    billRecordSuccess(cloudRecordBeans);
//                                } else {
//                                    if (code == ERROR_401) {
//                                        //登录失效
//                                        showLoading();
//                                        CasClient.getInstance().logout(true);
//                                    } else {
//                                        NetCodeUtils.checkedErrorCode(object.optString("code"), object.optString("message"));
//                                    }
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onError(ApiException e) {
//                            hideLoading();
//                            NetCodeUtils.checkedErrorCode(e.getCode(), e.getMessage());
//                        }
//                    });
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_BACK) {
                    if (loadingDialog.isShowing()) {
                        hideLoading();
                        disAll();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disAll();
    }

    private void disAll() {
        if (disposable1 != null) {
            EasyHttp.cancelSubscription(disposable1);
        }
    }


}
