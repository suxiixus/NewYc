package com.app.youcheng.activity.enterprise;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MainActivity;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.activity.home.HomeRecordDetailActivity;
import com.app.youcheng.adapter.QuanRecordAdapter;
import com.app.youcheng.base.BaseFragment;
import com.app.youcheng.entity.EventBean;
import com.app.youcheng.entity.HomeRecordBean;
import com.app.youcheng.utils.EventBusUtils;
import com.app.youcheng.utils.NetCodeUtils;
import com.app.youcheng.utils.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.reflect.TypeToken;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.app.youcheng.GlobalConstant.ERROR_401;

public class QuanFragment extends BaseFragment {
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
    private boolean isNeedRefresh = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_quan;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                pageNo = 1;
                getBillRecord(true);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isNeedRefresh) {
            pageNo = 1;
            getBillRecord(true);
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        super.initViews(savedInstanceState);
        setTitle("企业圈");
        tvGoto.setVisibility(View.VISIBLE);
        tvGoto.setText("添加");
    }

    @Override
    protected void initData() {
        super.initData();

        initRv();
    }

    private void initRv() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rvRecord.setLayoutManager(manager);
        recordAdapter = new QuanRecordAdapter(getContext(), recordList);
        recordAdapter.bindToRecyclerView(rvRecord);
        recordAdapter.setEmptyView(R.layout.empty_no_message);
    }

    @Override
    protected void loadData() {
        super.loadData();

        if (isFirst) {
            getBillRecord(true);
            isFirst = false;
        }
    }

    @Override
    protected String getmTag() {
        return getClass().getSimpleName();
    }

    @Override
    protected void setListener() {
        super.setListener();

        recordAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (MyApplication.getApplication().isLogin()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("type",1);
                    bundle.putString("enterpriseId", recordList.get(position).getEnterpriseId());
                    showActivity(HomeRecordDetailActivity.class, bundle,0);
                } else {
                    ToastUtils.showToast("请先登录");
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
        isNeedRefresh = false;
        String keyword = etSearch.getText().toString();

        if (isShow) {
            showLoading();
        }
        EasyHttp.get(BaseHost.friendsList)
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
                                    ((MainActivity) getActivity()).selectHome();
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

    @OnClick({R.id.tvSearch, R.id.tvGoto})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.tvSearch:
                pageNo = 1;
                getBillRecord(true);
                break;
            case R.id.tvGoto:
                showActivity(AddQuanActivity.class, null, 0);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        if (eventBean != null) {
            if (eventBean.getType() == 0) {
                //登录成功
                isNeedRefresh = true;
            } else if (eventBean.getType() == 1) {
                //退出登录

            }
        }
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
