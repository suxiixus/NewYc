package com.app.youcheng.activity.home;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.adapter.HomeRecordAdapter;
import com.app.youcheng.base.BaseFragment;
import com.app.youcheng.entity.HomeRecordBean;
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


public class HomeRecordFragment extends BaseFragment {
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rvRecord)
    RecyclerView rvRecord;

    private HomeRecordAdapter recordAdapter;
    private List<HomeRecordBean> recordList = new ArrayList<>();
    private int pageNo = 1;
    private int type = 0;//0 优质企业 1，虚假企业 2 不诚实企业
    private int total;

    private Disposable disposable1;

    public static HomeRecordFragment getInstance(int type) {
        HomeRecordFragment fragment = new HomeRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_record;
    }

    @Override
    protected void initData() {
        super.initData();
        setNeedDes(false);
        llTitle.setVisibility(View.GONE);
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getInt("type");
        }

        initRv();
    }

    private void initRv() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rvRecord.setLayoutManager(manager);
        recordAdapter = new HomeRecordAdapter(getContext(), recordList);
        recordAdapter.bindToRecyclerView(rvRecord);
        setEmptyView();
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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshBillRecord(false);
                ((HomeFragment) getParentFragment()).getCount();
            }
        });

        recordAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                if (MyApplication.getApplication().isLogin()) {
                Bundle bundle = new Bundle();
                bundle.putInt("type", 1);
                bundle.putString("enterpriseId", recordList.get(position).getEnterpriseId());
                showActivity(HomeRecordDetailActivity.class, bundle);
//                } else {
//                    ToastUtils.showToast("请先登录");
//                }
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
    }

    public void refreshBillRecord(boolean isShow) {
        pageNo = 1;
        getBillRecord(isShow);
    }

    private void getBillRecord(boolean isShow) {
        if (isShow) {
            showLoading();
        }
        EasyHttp.get(BaseHost.homeTypeList + type)
                .params("pageNum", pageNo + "")
                .params("pageSize", GlobalConstant.pageSize + "")
                .params("score", type + "")
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
            setEmptyView();
        } else {
            if (pageNo == 1) {
                recordList.clear();
                if (list.size() == 0) {
                    setEmptyView();
                }
            } else if (list.size() == 0) {
                recordAdapter.loadMoreEnd();
            }
            recordList.addAll(list);
        }

        recordAdapter.notifyDataSetChanged();
        recordAdapter.disableLoadMoreIfNotFullPage();
    }

    private void setEmptyView() {
        if (type == 1) {
            recordAdapter.setEmptyView(R.layout.empty_home_2);
        } else if (type == 2) {
            recordAdapter.setEmptyView(R.layout.empty_home_3);
        } else {
            recordAdapter.setEmptyView(R.layout.empty_home_1);
        }
    }

    private void closeAll() {
        hideLoading();
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
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
                    if (loadingDialog.isShowing() || swipeRefreshLayout.isRefreshing()) {
                        closeAll();
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
