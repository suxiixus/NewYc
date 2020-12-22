package com.app.youcheng.activity.enterprise;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.adapter.QuanRecordAdapter;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.entity.HomeRecordBean;
import com.app.youcheng.entity.User;
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


public class QuanActivity extends BaseActivity {
    @BindView(R.id.rvRecord)
    RecyclerView rvRecord;
    @BindView(R.id.etSearch)
    EditText etSearch;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private QuanRecordAdapter recordAdapter;
    private List<HomeRecordBean> recordList = new ArrayList<>();
    private int pageNo = 1;
    private int total;
    private User user;


    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.fragment_quan;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("企业列表");
    }

    @Override
    protected void initData() {
        super.initData();

        user = MyApplication.getApplication().getCurrentUser();
        initRv();
    }

    private void initRv() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvRecord.setLayoutManager(manager);
        recordAdapter = new QuanRecordAdapter(this, recordList);
        recordAdapter.bindToRecyclerView(rvRecord);
        recordAdapter.setEmptyView(R.layout.empty_no_message);
    }

    @Override
    protected void loadData() {
        super.loadData();

        getBillRecord(true);
    }

    @Override
    protected void setListener() {
        super.setListener();

        recordAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (recordList.get(position).getEnterpriseId().equals(user.getUserInfo().getEnterpriseId())) {
                    ToastUtils.showToast("禁止向自己所在企业发送账单");
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("name", recordList.get(position).getEnterpriseName());
                    intent.putExtra("id", recordList.get(position).getEnterpriseId());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        recordAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (recordList.size() < total) {
                    pageNo = pageNo + 1;
                    getBillRecord(false);
                } else {
                    recordAdapter.loadMoreEnd();
                }
            }
        }, rvRecord);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNo = 1;
                getBillRecord(false);
            }
        });
    }

    private void getBillRecord(boolean isShow) {
        String keyword = etSearch.getText().toString();

        if (isShow) {
            showLoading();
        }
        EasyHttp.get(BaseHost.homeList)
                .params("pageNum", pageNo + "")
                .params("pageSize", GlobalConstant.pageSize + "")
                .params("param", keyword)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        closeAll();

                        try {
                            JSONObject object = new JSONObject(s);
                            int code = object.optInt("code");
                            if (code == GlobalConstant.OK) {
                                total = object.optInt("total");

                                List<HomeRecordBean> cloudRecordBeans = gson.fromJson(object.getJSONArray("data").toString(), new TypeToken<List<HomeRecordBean>>() {
                                }.getType());

                                billRecordSuccess(cloudRecordBeans);
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

    private void billRecordSuccess(List<HomeRecordBean> list) {
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

    @OnClick({R.id.tvSearch})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.tvSearch:
                pageNo = 1;
                getBillRecord(true);
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
