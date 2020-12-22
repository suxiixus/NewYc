package com.app.youcheng.activity.my;


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
import com.app.youcheng.adapter.PayRecordAdapter;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.dialog.FapiaoDialog;
import com.app.youcheng.entity.PayRecordBean;
import com.app.youcheng.utils.DateUtils;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

import static com.app.youcheng.GlobalConstant.ERROR_401;


public class FirmBillActivity extends BaseActivity {
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rvRecord)
    RecyclerView rvRecord;
    @BindView(R.id.tvTime)
    TextView tvTime;

    private Calendar calendar = Calendar.getInstance();
    private PayRecordAdapter recordAdapter;
    private List<PayRecordBean> recordList = new ArrayList<>();
    private int pageNo = 1;
    private int total;

    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.activity_my_firm_bill;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("消费明细");
//        tvGoto.setVisibility(View.VISIBLE);
//        tvGoto.setText("开发票");
    }

    @Override
    protected void initData() {
        super.initData();

        String time = DateUtils.getCurFormatMonth();
        if (StringUtils.isNotEmpty(time)) {
            tvTime.setText(time);
        }

        initRv();
    }

    private void initRv() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvRecord.setLayoutManager(manager);
        recordAdapter = new PayRecordAdapter(this, recordList);
        recordAdapter.bindToRecyclerView(rvRecord);
    }

    @Override
    protected void loadData() {
        super.loadData();
        getBillRecord(true);
    }

    @OnClick({R.id.tvTime, R.id.tvGoto})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.tvTime:
                showSelectMonth();
                break;
            case R.id.tvGoto:
                showFapiaoDialog();
                break;
        }
    }

    private void showFapiaoDialog() {
        FapiaoDialog fapiaoDialog = new FapiaoDialog(this);
        fapiaoDialog.show();
    }

    @Override
    protected void setListener() {
        super.setListener();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNo = 1;
                getBillRecord(false);
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

    private void showSelectMonth() {
        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
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

//        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
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

    private void getBillRecord(boolean isShow) {
        String time = tvTime.getText().toString();
        if (StringUtils.isEmpty(time)) {
            return;
        }

        if (isShow) {
            showLoading();
        }

        EasyHttp.get(BaseHost.payList)
                .params("pageNum", pageNo + "")
                .params("pageSize", GlobalConstant.pageSize + "")
                .params("date", time)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        closeAll();

                        try {
                            JSONObject object = new JSONObject(s);
                            int code = object.optInt("code");
                            if (code == GlobalConstant.OK) {
                                total = object.optInt("total");

                                List<PayRecordBean> billBeanList = gson.fromJson(object.getJSONArray("data").toString(), new TypeToken<List<PayRecordBean>>() {
                                }.getType());

                                billRecordSuccess(billBeanList);
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

    private void billRecordSuccess(List<PayRecordBean> list) {
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
