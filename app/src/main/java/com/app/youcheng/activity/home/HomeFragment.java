package com.app.youcheng.activity.home;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MainActivity;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.activity.login.LoginActivity;
import com.app.youcheng.activity.my.ShareActivity;
import com.app.youcheng.base.BaseFragment;
import com.app.youcheng.entity.EventBean;
import com.app.youcheng.entity.User;
import com.app.youcheng.entity.UserInfo;
import com.app.youcheng.utils.EventBusUtils;
import com.app.youcheng.utils.NetCodeUtils;
import com.app.youcheng.utils.StringUtils;
import com.app.youcheng.utils.UserUtils;
import com.app.youcheng.widget.BannerImageLoader;
import com.app.youcheng.widget.MyfragmentPagerAdpter;
import com.google.gson.reflect.TypeToken;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

import static android.app.Activity.RESULT_OK;
import static com.app.youcheng.GlobalConstant.ERROR_401;

public class HomeFragment extends BaseFragment {
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tab)
    TabLayout tab;
    @BindView(R.id.redDotCheck)
    TextView redDotCheck;

    //    @BindView(R.id.swipeRefreshLayout)
//    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.mAppBarLayout)
    AppBarLayout mAppBarLayout;
    //    @BindView(R.id.ivGoTop)
//    ImageView ivGoTop;
    @BindView(R.id.banner)
    Banner banner;

    private String[] strings;
    private List<BaseFragment> fragments = new ArrayList<>();
    private List<Integer> imageUrls = new ArrayList<>();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    getCount();
                    break;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getCount();
    }

    @Override
    public void onResume() {
        super.onResume();

        banner.startAutoPlay();
    }

    @Override
    public void onPause() {
        super.onPause();

        banner.stopAutoPlay();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        super.initViews(savedInstanceState);

    }

    @Override
    protected void initData() {
        super.initData();

        imageUrls.add(R.mipmap.icon_home_top1);
        imageUrls.add(R.mipmap.icon_home_top2);
        imageUrls.add(R.mipmap.icon_home_top3);
        if (imageUrls.size() > 0) {
            banner.setBackgroundColor(getContext().getResources().getColor(R.color.white));
            banner.setImages(imageUrls); // 设置图片集合
            banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR).setIndicatorGravity(BannerConfig.CENTER) // 设置样式
                    .setImageLoader(new BannerImageLoader());
            banner.setDelayTime(3000); // 设置轮播时间
            banner.start();
        }

        strings = new String[]{"优质企业", "虚假企业", "不诚实企业"};
        fragments.add(HomeRecordFragment.getInstance(0));
        fragments.add(HomeRecordFragment.getInstance(1));
        fragments.add(HomeRecordFragment.getInstance(2));

        initPageAdapter();
    }

    @Override
    protected void loadData() {
        super.loadData();

        if (isFirst) {
            if (!MyApplication.getApplication().isLogin()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        User currentUser = UserUtils.getCurrentUserFromFile();

                        if (currentUser != null) {
                            doLogin(currentUser.getUsername(), currentUser.getPwd());
                        }
                    }
                }).start();
            }
            isFirst = false;
        }

        getCount();
    }

    private void initPageAdapter() {
        MyfragmentPagerAdpter adpter = new MyfragmentPagerAdpter(getChildFragmentManager(), fragments, strings);
        viewPager.setAdapter(adpter);
        tab.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);
    }

    @Override
    protected String getmTag() {
        return getClass().getSimpleName();
    }


    @OnClick({R.id.rlMsg, R.id.ivSearch, R.id.llDangan, R.id.llBill})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.rlMsg:
                if (MyApplication.getApplication().isLogin()) {
                    showActivity(MessageActivity.class, null);
                } else {
                    showActivity(LoginActivity.class, null, 0);
                }
                break;
            case R.id.ivSearch:
                showActivity(SearchActivity.class, null);
                break;
            case R.id.llDangan:
                if (MyApplication.getApplication().isLogin()) {
                    showActivity(DanganActivity.class, null);
                } else {
                    showActivity(LoginActivity.class, null, 0);
                }
                break;
            case R.id.llBill:
                if (MyApplication.getApplication().isLogin()) {
                    showActivity(CreateBillActivity.class, null);
                } else {
                    showActivity(LoginActivity.class, null, 0);
                }
                break;
//            case R.id.ivGoTop:
//                scrollToTop();
//                ((RecordFragment) fragments.get(0)).goTop();
//                break;
        }
    }

//    private void scrollToTop() {
//        //拿到 appbar 的 behavior,让 appbar 滚动
//        ViewGroup.LayoutParams layoutParams = mAppBarLayout.getLayoutParams();
//        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) layoutParams).getBehavior();
//        if (behavior instanceof AppBarLayout.Behavior) {
//            AppBarLayout.Behavior appBarLayoutBehavior = (AppBarLayout.Behavior) behavior;
//            //注意传递负值
//            appBarLayoutBehavior.setTopAndBottomOffset(0);
//        }
//    }

    @Override
    protected void setListener() {
        super.setListener();

        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if (position == 2) {
                    showActivity(ShareActivity.class, null);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("pos", position);
                    showActivity(TopDetailActivity.class, bundle);
                }
            }
        });

//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                closeAll();
//                ((HomeRecordFragment) fragments.get(viewPager.getCurrentItem())).refreshBillRecord(false);
//            }
//        });

//        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                //判断是否滑动到最顶部
//                if (verticalOffset >= 0) {
//                    swipeRefreshLayout.setEnabled(true);
//                } else {
//                    swipeRefreshLayout.setEnabled(false);
//                }

//                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
//                    ivGoTop.setVisibility(View.VISIBLE);
//                } else {
//                    ivGoTop.setVisibility(View.GONE);
//                }
//            }
//        });
    }

//    private void closeAll() {
//        hideLoading();
//        if (swipeRefreshLayout != null)
//            swipeRefreshLayout.setRefreshing(false);
//    }

    //    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent(EventBean eventBean) {
//        switch (eventBean.getOrigin()) {
//            //监听登录状态
//            case EvKey.loginStatus:
//                hideLoading();
//                if (eventBean.isStatue()) {
//                    initUser();
//                } else {
//                    NetCodeUtils.checkedErrorCode(eventBean.getCode(), eventBean.getMessage());
//                }
//                break;
//        }
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EventBusUtils.register(this);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        EventBusUtils.unRegister(this);
//    }

    public void getCount() {
        if (MyApplication.getApplication().isLogin()) {
            showLoading();
            EasyHttp.get(BaseHost.msgCount)
                    .execute(new SimpleCallBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            hideLoading();
                            try {
                                JSONObject object = new JSONObject(s);
                                int code = object.optInt("code");
                                if (code == GlobalConstant.OK) {
                                    int data = object.optInt("data");
                                    if (data > 9) {
                                        redDotCheck.setText("9+");
                                    } else {
                                        redDotCheck.setText(data + "");
                                    }
                                    if (data > 0) {
                                        redDotCheck.setVisibility(View.VISIBLE);
                                    } else {
                                        redDotCheck.setVisibility(View.GONE);
                                    }
                                } else {
                                    NetCodeUtils.checkedErrorCode(object.optString("code"), object.optString("msg"));
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
        } else {
            redDotCheck.setVisibility(View.GONE);
        }
    }

    private void doLogin(final String name, final String pwd) {
        if (StringUtils.isNotEmpty(name, pwd)) {

        } else {
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", name);
            jsonObject.put("password", pwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        EasyHttp.post(BaseHost.loginUrl)
                .headers("Content-Type", "application/json")
                .upJson(jsonObject.toString())
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        try {
                            JSONObject object = new JSONObject(s);
                            int code = object.optInt("code");
                            if (code == GlobalConstant.OK) {
                                String data = object.getString("data");

                                User user = new User();
                                user.setUsername(name);
                                user.setPwd(pwd);
                                if (StringUtils.isNotEmpty(data)) {
                                    user.setToken(data);
                                }
                                MyApplication.getApplication().setCurrentUser(user);
                                MyApplication.getApplication().setHeaders(data);
                                EventBusUtils.postEvent(new EventBean(0));
                                ((MainActivity) getActivity()).setVis();

                                getInfo();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((MainActivity) getActivity()).setVis();
                                    }
                                }, 1000);
                            } else {
                                NetCodeUtils.checkedErrorCode(object.optString("code"), object.optString("msg"));
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

    private void getInfo() {
        if (MyApplication.getApplication().isLogin()) {
            showLoading();
            EasyHttp.get(BaseHost.infoUrl)
                    .execute(new SimpleCallBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            hideLoading();

                            try {
                                JSONObject object = new JSONObject(s);
                                int code = object.optInt("code");
                                if (code == GlobalConstant.OK) {
                                    UserInfo userInfo = gson.fromJson(object.optString("data"), new TypeToken<UserInfo>() {
                                    }.getType());

                                    if (userInfo != null) {
                                        User user = MyApplication.getApplication().getCurrentUser();
                                        if (user != null) {
                                            user.setUserInfo(userInfo);
                                            MyApplication.getApplication().setCurrentUser(user);
                                        }

                                        JPushInterface.setAlias(getContext(), 0, userInfo.getEnterpriseId());
                                        ((MainActivity) getActivity()).setVis();

                                        getCount();
                                    }
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
                            hideLoading();
                            NetCodeUtils.checkedErrorCode(e.getCode(), e.getMessage());
                        }
                    });
        }
    }


}
