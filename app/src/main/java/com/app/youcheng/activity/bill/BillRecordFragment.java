package com.app.youcheng.activity.bill;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MainActivity;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.adapter.BillRecordAdapter;
import com.app.youcheng.base.BaseFragment;
import com.app.youcheng.entity.BillBean;
import com.app.youcheng.entity.EventBean;
import com.app.youcheng.utils.DateUtils;
import com.app.youcheng.utils.EventBusUtils;
import com.app.youcheng.utils.NetCodeUtils;
import com.app.youcheng.utils.StringUtils;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

import static android.app.Activity.RESULT_OK;
import static com.app.youcheng.GlobalConstant.ERROR_401;


public class BillRecordFragment extends BaseFragment {
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rvRecord)
    RecyclerView rvRecord;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.etSearch)
    EditText etSearch;

    private BillRecordAdapter recordAdapter;
    private List<BillBean> recordList = new ArrayList<>();
    private int pageNo = 1;
    private int type = 0;
    private boolean isNeedRefresh = false;
    private Calendar calendar = Calendar.getInstance();
    private int total;

    private Disposable disposable1;

    public static BillRecordFragment getInstance(int type) {
        BillRecordFragment fragment = new BillRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                refreshAll();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isNeedRefresh) {
            refreshBillRecord(true);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bill_record;
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

        String time = DateUtils.getCurFormatMonth();
        if (StringUtils.isNotEmpty(time)) {
            tvTime.setText(time);
        }

        initRv();
    }

    private void initRv() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rvRecord.setLayoutManager(manager);
        recordAdapter = new BillRecordAdapter(getContext(), recordList, type);
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

    @OnClick({R.id.tvSearch, R.id.tvTime})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        switch (v.getId()) {
            case R.id.tvSearch:
                pageNo = 1;
                getBillRecord(true);
                break;
            case R.id.tvTime:
                showSelectMonth();
                break;
        }
    }

    private void showSelectMonth() {
        TimePickerView pvTime = new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String time = DateUtils.getMyFormatTime("yyyy-MM", date.getTime() + "");
                if (StringUtils.isNotEmpty(time)) {
                    tvTime.setText(time);
                    pageNo = 1;
                    getBillRecord(true);
                }
            }
        }).setType(new boolean[]{true, true, false, false, false, false}).build();
        pvTime.show();

//        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                String month, dateStr;
//
//                if (monthOfYear + 1 <= 9) {
//                    month = "0" + (monthOfYear + 1);
//                } else {
//                    month = String.valueOf(monthOfYear + 1);
//                }
//
//                dateStr = String.valueOf(year) + "-" + month;
//                tvTime.setText(dateStr);
//                pageNo = 1;
//                getBillRecord(true);
//            }
//        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
//                calendar.get(Calendar.DAY_OF_MONTH));
//
//        datePickerDialog.show();
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
            }
        });

        recordAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (recordList.size() > position) {
                    Bundle bundle = new Bundle();
                    bundle.putString("billId", recordList.get(position).getBillId());
                    bundle.putInt("type", type);
                    showActivity(BillDetailActivity.class, bundle, 0);
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
    }

    public void refreshBillRecord(boolean isShow) {
        if (rvRecord != null) {
            rvRecord.scrollToPosition(0);
        }

        pageNo = 1;
        getBillRecord(isShow);
        ((BillFragment) getParentFragment()).getCount();
    }

    private void refreshAll() {
        ((BillFragment) getParentFragment()).refreshAll();
        ((BillFragment) getParentFragment()).getCount();
    }

    private void getBillRecord(boolean isShow) {
        String time = tvTime.getText().toString();
        if (StringUtils.isEmpty(time)) {
            return;
        }

        String searchParam = etSearch.getText().toString();
        if (StringUtils.isEmpty(searchParam)) {
            searchParam = "";
        }

        isNeedRefresh = false;
        if (isShow) {
            showLoading();
        }

        String url = BaseHost.sendBillList;
        if (type == 1) {
            url = BaseHost.receiveBill;
        } else if (type == 2) {
            url = BaseHost.evaluateBill;
        }

        EasyHttp.get(url)
                .params("pageNum", pageNo + "")
                .params("pageSize", GlobalConstant.pageSize + "")
                .params("billTime", time)
//                .params("sendTime", time)
                .params("searchParam", searchParam)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        closeAll();

                        try {
                            JSONObject object = new JSONObject(s);
                            int code = object.optInt("code");
                            if (code == GlobalConstant.OK) {
                                total = object.optInt("total");

                                List<BillBean> billBeanList = gson.fromJson(object.getJSONArray("data").toString(), new TypeToken<List<BillBean>>() {
                                }.getType());

                                billRecordSuccess(billBeanList);
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

    private void billRecordSuccess(List<BillBean> list) {
        recordAdapter.loadMoreComplete();

        if (list != null) {
            if (pageNo == 1) {
                recordList.clear();
                if (list.size() == 0) {
                    recordAdapter.setEmptyView(R.layout.empty_no_message);
                }
            } else if (list.size() == 0) {
                recordAdapter.loadMoreEnd();
            }
            recordList.addAll(list);

            recordAdapter.notifyDataSetChanged();
            recordAdapter.disableLoadMoreIfNotFullPage();
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

    private void disAll() {
        if (disposable1 != null) {
            EasyHttp.cancelSubscription(disposable1);
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
        disAll();
        EventBusUtils.unRegister(this);
    }


}
