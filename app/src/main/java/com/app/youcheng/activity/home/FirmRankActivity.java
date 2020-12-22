package com.app.youcheng.activity.home;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.adapter.FirmRankAdapter;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.entity.FirmDetailBean;
import com.app.youcheng.utils.NetCodeUtils;
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
import io.reactivex.disposables.Disposable;

import static com.app.youcheng.GlobalConstant.ERROR_401;


public class FirmRankActivity extends BaseActivity {
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rvRecord)
    RecyclerView rvRecord;

    private FirmRankAdapter recordAdapter;
    private List<FirmDetailBean.EnterpriseRankBean> recordList = new ArrayList<>();
    private FirmDetailBean firmDetailBean;
    private int pageNo = 1;
    private int total;

    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.activity_firm_rank;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("分值明细");
    }

    @Override
    protected void initData() {
        super.initData();

        firmDetailBean = (FirmDetailBean) getIntent().getSerializableExtra("firmDetailBean");
        initRv();
    }

    private void initRv() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvRecord.setLayoutManager(manager);
        recordAdapter = new FirmRankAdapter(this, recordList);
        recordAdapter.bindToRecyclerView(rvRecord);
        recordAdapter.setEmptyView(R.layout.empty_no_message);
    }

    @Override
    protected void loadData() {
        super.loadData();
        getRankList(true);
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
                    getRankList(false);
                } else {
                    recordAdapter.loadMoreEnd();
                }
            }
        }, rvRecord);
    }

    private void refreshData(boolean isShow) {
        pageNo = 1;
        getRankList(isShow);
    }

    private void getRankList(boolean isShow) {
        if (firmDetailBean == null) {
            return;
        }

        if (isShow) {
            showLoading();
        }
        EasyHttp.get(BaseHost.entRank + firmDetailBean.getEnterpriseInfo().getEnterpriseId())
                .params("enterpriseId", firmDetailBean.getEnterpriseInfo().getEnterpriseId())
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

                                List<FirmDetailBean.EnterpriseRankBean> list = gson.fromJson(object.optString("data"), new TypeToken<List<FirmDetailBean.EnterpriseRankBean>>() {
                                }.getType());

                                recordSuccess(list);
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

    private void recordSuccess(List<FirmDetailBean.EnterpriseRankBean> list) {
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

    private void closeAll() {
        hideLoading();
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        if (loadingDialog.isShowing() && swipeRefreshLayout.isRefreshing()) {
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
