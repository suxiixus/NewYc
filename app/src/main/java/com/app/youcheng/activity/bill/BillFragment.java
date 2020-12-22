package com.app.youcheng.activity.bill;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MainActivity;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.base.BaseFragment;
import com.app.youcheng.entity.BillCountBean;
import com.app.youcheng.entity.EventBean;
import com.app.youcheng.utils.EventBusUtils;
import com.app.youcheng.utils.NetCodeUtils;
import com.app.youcheng.widget.MyfragmentPagerAdpter;
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

import static com.app.youcheng.GlobalConstant.ERROR_401;

public class BillFragment extends BaseFragment {
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tab)
    TabLayout tab;
    @BindView(R.id.tvOverNum)
    TextView tvOverNum;
    @BindView(R.id.tvNoPayNum)
    TextView tvNoPayNum;

    private String[] strings;
    private List<BaseFragment> fragments = new ArrayList<>();
    private int[] tabIcons = {R.drawable.item_bill_selector, R.drawable.item_bill_selector, R.drawable.item_bill_selector};

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refreshAll();
        }
    }

    public void refreshAll() {
        for (BaseFragment fragment : fragments) {
            ((BillRecordFragment) fragment).refreshBillRecord(true);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bill;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        super.initViews(savedInstanceState);
        setTitle("有诚账单");
    }

    @Override
    protected void initData() {
        super.initData();

        strings = new String[]{"发出账单", "收到账单", "待评价"};
        fragments.add(BillRecordFragment.getInstance(0));
        fragments.add(BillRecordFragment.getInstance(1));
        fragments.add(BillRecordFragment.getInstance(2));

        initPageAdapter();
    }

    private void initPageAdapter() {
        MyfragmentPagerAdpter adpter = new MyfragmentPagerAdpter(getChildFragmentManager(), fragments, strings);
        viewPager.setAdapter(adpter);
        tab.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);
        setupTabIcons();
    }

    private void setupTabIcons() {
        tab.getTabAt(0).setCustomView(getTabView(0, true));
        tab.getTabAt(1).setCustomView(getTabView(1, false));
        tab.getTabAt(2).setCustomView(getTabView(2, false));
    }

    private View getTabView(int position, boolean isSelect) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_tab, null);
        TextView tvTitleTxt = view.findViewById(R.id.tvTitleTxt);
        tvTitleTxt.setText(strings[position]);
        ImageView ivTitleIcon = view.findViewById(R.id.ivTitleIcon);
        ivTitleIcon.setImageResource(tabIcons[position]);
        ivTitleIcon.setSelected(isSelect);
        view.setTag(position);
        return view;
    }

    @Override
    protected void loadData() {
        super.loadData();

        if (isFirst) {
            getCount();
            isFirst = false;
        }
    }

    public void getCount() {
        if (MyApplication.getApplication().isLogin()) {
            EasyHttp.get(BaseHost.billCount)
                    .execute(new SimpleCallBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            try {
                                JSONObject object = new JSONObject(s);
                                int code = object.optInt("code");
                                if (code == GlobalConstant.OK) {
                                    BillCountBean billCountBean = gson.fromJson(object.getString("data"), new TypeToken<BillCountBean>() {
                                    }.getType());

                                    if (billCountBean != null) {
                                        tvOverNum.setText(billCountBean.getOverBillCount());
                                        tvNoPayNum.setText(billCountBean.getNoPayBillCount());
                                    }
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
                            NetCodeUtils.checkedErrorCode(e.getCode(), e.getMessage());
                        }
                    });
        }
    }

    @Override
    protected String getmTag() {
        return getClass().getSimpleName();
    }


    @OnClick({R.id.llOne})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.llOne:

                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        if (eventBean != null) {
            if (eventBean.getType() == 0) {
                //登录成功

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
