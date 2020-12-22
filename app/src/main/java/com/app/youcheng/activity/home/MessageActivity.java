package com.app.youcheng.activity.home;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.activity.bill.BillDetailActivity;
import com.app.youcheng.adapter.BillMsgAdapter;
import com.app.youcheng.adapter.NoticeRecordAdapter;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.entity.BillMsgBean;
import com.app.youcheng.entity.NoticeBean;
import com.app.youcheng.utils.NetCodeUtils;
import com.app.youcheng.utils.StringUtils;
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


public class MessageActivity extends BaseActivity {
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rvNoticeMsg)
    RecyclerView rvNoticeMsg;
    @BindView(R.id.rvBillMsg)
    RecyclerView rvBillMsg;

    @BindView(R.id.tvOne)
    TextView tvOne;
    @BindView(R.id.tvTwo)
    TextView tvTwo;

    private NoticeRecordAdapter recordAdapter;
    private List<NoticeBean> recordList = new ArrayList<>();
    private int pageNo = 1;

    private BillMsgAdapter billMsgAdapter;
    private List<BillMsgBean> billMsgBeanList = new ArrayList<>();
    private int billPageNo = 1;

    private int myType = 0;
    private int total;
    private int billTotal = 0;

    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.activity_message;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();

        initRv();
    }

    private void initRv() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvNoticeMsg.setLayoutManager(manager);
        recordAdapter = new NoticeRecordAdapter(this, recordList);
        recordAdapter.bindToRecyclerView(rvNoticeMsg);
        recordAdapter.setEmptyView(R.layout.empty_no_message);

        LinearLayoutManager manager2 = new LinearLayoutManager(this);
        rvBillMsg.setLayoutManager(manager2);
        billMsgAdapter = new BillMsgAdapter(this, billMsgBeanList);
        billMsgAdapter.bindToRecyclerView(rvBillMsg);
        billMsgAdapter.setEmptyView(R.layout.empty_no_message);
    }

    @Override
    protected void loadData() {
        super.loadData();
        getBillRecord(true);
        getBillMsgList(true);
    }

    @OnClick({R.id.llOne, R.id.llTwo})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.llOne:
                if (myType == 1) {
                    myType = 0;
                    tvOne.setTextColor(getResources().getColor(R.color.font_main_black));
                    tvTwo.setTextColor(getResources().getColor(R.color.font_main_gray));
                    rvNoticeMsg.setVisibility(View.VISIBLE);
                    rvBillMsg.setVisibility(View.GONE);
                }
                break;
            case R.id.llTwo:
                if (myType == 0) {
                    myType = 1;
                    tvOne.setTextColor(getResources().getColor(R.color.font_main_gray));
                    tvTwo.setTextColor(getResources().getColor(R.color.font_main_black));
                    rvNoticeMsg.setVisibility(View.GONE);
                    rvBillMsg.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    protected void setListener() {
        super.setListener();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (myType == 0) {
                    pageNo = 1;
                    getBillRecord(false);
                } else {
                    billPageNo = 1;
                    getBillMsgList(false);
                }
            }
        });

        recordAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (recordList.size() > position) {
                    NoticeBean noticeBean = recordList.get(position);
                    if (noticeBean != null && StringUtils.isNotEmpty(noticeBean.getNoticeContent())) {
                        Bundle bundle = new Bundle();
                        bundle.putString("content", noticeBean.getNoticeContent());
                        showActivity(NoticeDetailActivity.class, bundle);
                    }
                }
            }
        });

        billMsgAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (billMsgBeanList.size() > position) {

                    BillMsgBean billMsgBean = billMsgBeanList.get(position);
                    if (billMsgBean != null && StringUtils.isNotEmpty(billMsgBean.getContent())) {
                        if (billMsgBean.getReadFlag() == 0) {
                            doUpdateMsg(billMsgBean);
                        } else {
                            if (StringUtils.isNotEmpty(billMsgBean.getBillId())) {
                                Bundle bundle = new Bundle();
                                bundle.putString("billId", billMsgBean.getBillId());
                                bundle.putInt("type", 0);
                                showActivity(BillDetailActivity.class, bundle);
                            }
                        }
                    }
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
                    recordAdapter.disableLoadMoreIfNotFullPage();
                }
            }
        }, rvNoticeMsg);

        billMsgAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (billMsgBeanList.size() < billTotal) {
                    billPageNo = billPageNo + 1;
                    getBillMsgList(false);
                } else {
                    billMsgAdapter.loadMoreEnd();
                    billMsgAdapter.disableLoadMoreIfNotFullPage();
                }
            }
        }, rvBillMsg);
    }

    private void doUpdateMsg(final BillMsgBean billMsgBean) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", billMsgBean.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showLoading();
        EasyHttp.post(BaseHost.updateMsgStatus + billMsgBean.getId())
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
                                if (StringUtils.isNotEmpty(billMsgBean.getBillId())) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("billId", billMsgBean.getBillId());
                                    bundle.putInt("type", 0);
                                    showActivity(BillDetailActivity.class, bundle);
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

    private void getBillRecord(boolean isShow) {
        if (isShow) {
            showLoading();
        }
        EasyHttp.get(BaseHost.noticeList)
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

                                List<NoticeBean> noticeBeanList = gson.fromJson(object.getString("data"), new TypeToken<List<NoticeBean>>() {
                                }.getType());
                                billRecordSuccess(noticeBeanList);
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

    private void billRecordSuccess(List<NoticeBean> noticeBeanList) {
        recordAdapter.loadMoreComplete();

        if (noticeBeanList == null) {
            recordList.clear();
            recordAdapter.setEmptyView(R.layout.empty_no_message);
        } else {
            if (pageNo == 1) {
                recordList.clear();
                if (noticeBeanList.size() == 0) {
                    recordAdapter.setEmptyView(R.layout.empty_no_message);
                }
            } else if (noticeBeanList.size() == 0) {
                recordAdapter.loadMoreEnd();
            }
            recordList.addAll(noticeBeanList);
        }

        recordAdapter.notifyDataSetChanged();
        recordAdapter.disableLoadMoreIfNotFullPage();
    }

    private void getBillMsgList(boolean isShow) {
        if (isShow) {
            showLoading();
        }
        EasyHttp.get(BaseHost.billMsgList)
                .params("pageNum", billPageNo + "")
                .params("pageSize", GlobalConstant.pageSize + "")
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        closeAll();
                        try {
                            JSONObject object = new JSONObject(s);
                            int code = object.optInt("code");
                            if (code == GlobalConstant.OK) {
                                billTotal = object.optInt("total");

                                List<BillMsgBean> list = gson.fromJson(object.getString("data"), new TypeToken<List<BillMsgBean>>() {
                                }.getType());
                                billMsgSuccess(list);
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

    private void billMsgSuccess(List<BillMsgBean> noticeBeanList) {
        billMsgAdapter.loadMoreComplete();

        if (noticeBeanList == null) {
            billMsgBeanList.clear();
            billMsgAdapter.setEmptyView(R.layout.empty_no_message);
        } else {
            if (billPageNo == 1) {
                billMsgBeanList.clear();
                if (noticeBeanList.size() == 0) {
                    billMsgAdapter.setEmptyView(R.layout.empty_no_message);
                }
            } else if (noticeBeanList.size() == 0) {
                billMsgAdapter.loadMoreEnd();
            }
            billMsgBeanList.addAll(noticeBeanList);
        }

        billMsgAdapter.notifyDataSetChanged();
        billMsgAdapter.disableLoadMoreIfNotFullPage();
    }

    private void closeAll() {
        hideLoading();
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        if (loadingDialog.isShowing() || swipeRefreshLayout.isRefreshing()) {
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
