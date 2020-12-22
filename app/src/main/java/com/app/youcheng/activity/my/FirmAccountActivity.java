package com.app.youcheng.activity.my;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.adapter.FirmAccountAdapter;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.entity.UserAccount;
import com.app.youcheng.utils.NetCodeUtils;
import com.app.youcheng.utils.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.reflect.TypeToken;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

import static com.app.youcheng.GlobalConstant.ERROR_401;


public class FirmAccountActivity extends BaseActivity {
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rvRecord)
    RecyclerView rvRecord;

    private FirmAccountAdapter recordAdapter;
    private List<UserAccount> recordList = new ArrayList<>();
    private int pageNo = 1;
    private int total;

    private Disposable disposable1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    getUserList(true);
                    break;
            }
        }
    }

    @Override
    protected int getViewId() {
        return R.layout.activity_my_firm_account;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("企业账号");
    }

    @Override
    protected void initData() {
        super.initData();

        initRv();
    }

    @Override
    protected void loadData() {
        super.loadData();
        getUserList(true);
    }

    private void initRv() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvRecord.setLayoutManager(manager);
        recordAdapter = new FirmAccountAdapter(this, recordList);
        recordAdapter.bindToRecyclerView(rvRecord);
    }

    private void getUserList(boolean isShow) {
        if (MyApplication.getApplication().isLogin()) {
            if (isShow) {
                showLoading();
            }
            EasyHttp.get(BaseHost.getUserListUrl)
                    .params("pageNum", pageNo + "")
                    .params("pageSize", GlobalConstant.pageSize + "")
                    .execute(new SimpleCallBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            closeAll();

                            try {
                                JSONObject object = new JSONObject(s);
                                int code = object.optInt("code");
                                if (code == GlobalConstant.OK) {
                                    total = object.optInt("total");

                                    List<UserAccount> userAccountList = gson.fromJson(object.optString("data"), new TypeToken<List<UserAccount>>() {
                                    }.getType());

                                    recordSuccess(userAccountList);
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
                            closeAll();
                            NetCodeUtils.checkedErrorCode(e.getCode(), e.getMessage());
                        }
                    });
        }
    }

    private void recordSuccess(List<UserAccount> list) {
        recordAdapter.loadMoreComplete();

        if (list == null) {
            recordList.clear();
            recordAdapter.setEmptyView(R.layout.empty_no_message);
        } else {
            if (pageNo == 1) {
                recordList.clear();
                if (list.size() == 0) {
                    recordAdapter.setEmptyView(R.layout.empty_no_message);
                }
            } else if (list.size() == 0) {
                recordAdapter.loadMoreEnd();
            }
            recordList.addAll(list);
        }

        recordAdapter.notifyDataSetChanged();
        recordAdapter.disableLoadMoreIfNotFullPage();
    }

    @OnClick({R.id.tvAdd})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.tvAdd:
                showActivity(AddAccountActivity.class, null, 0);
                break;
        }
    }

    @Override
    protected void setListener() {
        super.setListener();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData(false);
            }
        });

        recordAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (recordList.size() < total) {
                    pageNo = pageNo + 1;
                    getUserList(false);
                } else {
                    recordAdapter.loadMoreEnd();
                }
            }
        }, rvRecord);

        recordAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.tvJieBang) {
                    doDelete(recordList.get(position).getUserId());
                }
            }
        });
    }

    private void refreshData(boolean isShow) {
        pageNo = 1;
        getUserList(isShow);
    }

    private void doDelete(String id) {
        showLoading();
        EasyHttp.delete(BaseHost.deleteUser + id)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        hideLoading();

                        try {
                            JSONObject object = new JSONObject(s);
                            int code = object.optInt("code");
                            if (code == GlobalConstant.OK) {
                                ToastUtils.showToast("解绑成功");
                                refreshData(true);
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

    private void closeAll() {
        hideLoading();
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        if (loadingDialog.isShowing() && swipeRefreshLayout.isRefreshing()) {
            closeAll();
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
