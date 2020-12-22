package com.app.youcheng.activity.my;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.app.youcheng.R;
import com.app.youcheng.adapter.RechargeAdapter;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.entity.HomeRecordBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhouyou.http.EasyHttp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;


public class FirmRechargeActivity extends BaseActivity {
    @BindView(R.id.rvRecord)
    RecyclerView rvRecord;

    private RechargeAdapter recordAdapter;
    private List<HomeRecordBean> recordList = new ArrayList<>();

    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.activity_my_firm_recharge;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("企业充值");
    }

    @Override
    protected void initData() {
        super.initData();

        initRv();
    }

    private void initRv() {
        recordList.add(new HomeRecordBean());
        recordList.add(new HomeRecordBean());
        recordList.add(new HomeRecordBean());
        recordList.add(new HomeRecordBean());
        recordList.add(new HomeRecordBean());
        recordList.add(new HomeRecordBean());

        GridLayoutManager manager = new GridLayoutManager(this, 3);
        rvRecord.setLayoutManager(manager);
        recordAdapter = new RechargeAdapter(this, recordList);
        recordAdapter.bindToRecyclerView(rvRecord);
    }

    @OnClick({R.id.tvAdd})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.tvAdd:

                break;
        }
    }

    @Override
    protected void setListener() {
        super.setListener();

        recordAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

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
