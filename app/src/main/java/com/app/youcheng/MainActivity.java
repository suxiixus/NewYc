package com.app.youcheng;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;

import com.app.youcheng.activity.bill.BillDetailActivity;
import com.app.youcheng.activity.bill.BillFragment;
import com.app.youcheng.activity.enterprise.QuanFragment;
import com.app.youcheng.activity.home.HomeFragment;
import com.app.youcheng.activity.my.MyFragment;
import com.app.youcheng.base.ActivityManage;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.entity.EventBean;
import com.app.youcheng.utils.StringUtils;
import com.app.youcheng.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    @BindView(R.id.llHome)
    LinearLayout llHome;
    @BindView(R.id.llMy)
    LinearLayout llMy;
    @BindView(R.id.llEnterprise)
    LinearLayout llEnterprise;
    @BindView(R.id.llBill)
    LinearLayout llBill;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private long lastPressTime = 0;
    private int curPage = -1;
    private boolean isNeedGo = false;

    private HomeFragment homeFragment;
    private QuanFragment enterPriseFragment;
    private BillFragment billFragment;
    private MyFragment myFragment;

    @Override
    public void onEvent(EventBean eventBean) {
        super.onEvent(eventBean);
        if (eventBean != null) {
            if (eventBean.getType() == 0) {
                //登录成功
                llEnterprise.setVisibility(View.VISIBLE);
                llBill.setVisibility(View.VISIBLE);
            } else if (eventBean.getType() == 1) {
                //退出登录
                llEnterprise.setVisibility(View.GONE);
                llBill.setVisibility(View.GONE);
                isNeedGo = true;
            } else if (eventBean.getType() == 3 && StringUtils.isNotEmpty(eventBean.getBillId())) {
                if (MyApplication.getApplication().isLogin()) {
                    Bundle myBundle = new Bundle();
                    myBundle.putString("billId", eventBean.getBillId());
                    myBundle.putInt("type", 0);

                    Intent intent = new Intent(activity, BillDetailActivity.class);
                    intent.putExtras(myBundle);
//                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (isNeedGo) {
            selectHome();
        }
    }

    public void selectHome() {
        selectView(llHome, 0);
        isNeedGo = false;
    }

    public void setVis() {
        llEnterprise.setVisibility(View.VISIBLE);
        llBill.setVisibility(View.VISIBLE);
    }

    @Override
    protected int getViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        super.initViews(savedInstanceState);

        fragmentManager = getSupportFragmentManager();
        selectView(llHome, 0);
    }

    @OnClick({R.id.llHome, R.id.llEnterprise, R.id.llBill, R.id.llMy})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);
        switch (v.getId()) {
            case R.id.llHome:
                selectView(llHome, 0);
                break;
            case R.id.llEnterprise:
                selectView(llEnterprise, 1);
                break;
            case R.id.llBill:
                selectView(llBill, 2);
                break;
            case R.id.llMy:
                selectView(llMy, 3);
                break;
        }
    }

    private void selectView(View v, int page) {
        if (curPage != page) {
            curPage = page;
            llHome.setSelected(false);
            llEnterprise.setSelected(false);
            llBill.setSelected(false);
            llMy.setSelected(false);
            v.setSelected(true);
            setSelection(page);
        }
    }

    private void setSelection(int page) {
        try {
            // 开启一个Fragment事务
            transaction = fragmentManager.beginTransaction();
            // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
            hideFragments(transaction);
            switch (page) {
                //首页
                case 0:
                    if (homeFragment == null) {
                        homeFragment = new HomeFragment();
                        transaction.add(R.id.fragment_content, homeFragment);
                    } else {
                        transaction.show(homeFragment);
                    }
                    break;
                //企业圈
                case 1:
                    if (enterPriseFragment == null) {
                        enterPriseFragment = new QuanFragment();
                        transaction.add(R.id.fragment_content, enterPriseFragment);
                    } else {
                        transaction.show(enterPriseFragment);
                    }
                    break;
                //有诚账单
                case 2:
                    if (billFragment == null) {
                        billFragment = new BillFragment();
                        transaction.add(R.id.fragment_content, billFragment);
                    } else {
                        transaction.show(billFragment);
                    }
                    break;
                //我的
                case 3:
                    if (myFragment == null) {
                        myFragment = new MyFragment();
                        transaction.add(R.id.fragment_content, myFragment);
                    } else {
                        transaction.show(myFragment);
                    }
                    break;
            }
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (enterPriseFragment != null) {
            transaction.hide(enterPriseFragment);
        }
        if (billFragment != null) {
            transaction.hide(billFragment);
        }
        if (myFragment != null) {
            transaction.hide(myFragment);
        }
    }

    @Override
    public void onBackPressed() {
        if (loadingDialog.isShowing()) {
            hideLoading();
        } else {
            long now = System.currentTimeMillis();
            if (lastPressTime == 0 || now - lastPressTime > 2 * 1000) {
                ToastUtils.showToast("再按一次退出应用");
                lastPressTime = now;
            } else if (now - lastPressTime < 2 * 1000) {
                ActivityManage.finishAll();
            }
        }
    }


}
