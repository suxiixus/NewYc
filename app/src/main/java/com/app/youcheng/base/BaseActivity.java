package com.app.youcheng.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.dialog.LoadingDialog;
import com.app.youcheng.entity.EventBean;
import com.app.youcheng.utils.EventBusUtils;
import com.app.youcheng.utils.NetCodeUtils;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jpush.android.api.JPushInterface;


public abstract class BaseActivity extends AppCompatActivity {
    protected ImageView ivBack;
    protected TextView tvTitle;
    protected TextView tvGoto;
    protected LinearLayout llTitle;
    protected BaseActivity activity;
    protected LoadingDialog loadingDialog;
    private ImmersionBar immersionBar;
    private Unbinder unbinder;
    protected Gson gson = new Gson();

    protected boolean isNeedChecke = true;// 解锁界面 是不需要判断的 用此变量控制
    protected boolean isNormalBar = true;

    public LoadingDialog getloadingdialog() {
        return loadingDialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initLanguage();
        activity = this;
        EventBusUtils.register(this);
        setContentView(getViewId());
        unbinder = ButterKnife.bind(this);
        initBaseView();
        initViews(savedInstanceState);
        initData();
        setListener();
        ActivityManage.addActivity(this);
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        });
    }

    public void doBaseLogout() {
        if (MyApplication.getApplication().isLogin()) {
            showLoading();
            EasyHttp.get(BaseHost.logout)
                    .execute(new SimpleCallBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            hideLoading();
                            try {
                                JSONObject object = new JSONObject(s);
                                int code = object.optInt("code");
                                if (code == GlobalConstant.OK) {
                                    JPushInterface.deleteAlias(activity, 0);

                                    MyApplication.getApplication().deleteCurrentUser();
                                    MyApplication.getApplication().setHeaders("");
                                    EventBusUtils.postEvent(new EventBean(1));
                                    finish();
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
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (isNeedShowLockActivity()) {
//            showActivity(LockActivity.class, null);
//            SharedPreferencesUtil.getInstance().setParam(SP_KEY_IS_NEED_SHOW_LOCK, false);
//        } else {
//            SharedPreferencesUtil.getInstance().setParam(SP_KEY_IS_NEED_SHOW_LOCK, false);
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (!CommonUtils.isAppOnForeground(getApplicationContext()))
//            SharedPreferencesUtil.getInstance().setParam(SP_KEY_IS_NEED_SHOW_LOCK, true);
//        else
//            SharedPreferencesUtil.getInstance().setParam(SP_KEY_IS_NEED_SHOW_LOCK, false);
//    }

    /**
     * 解锁
     */
//    protected boolean isNeedShowLockActivity() {
//        boolean isLogin = MyApplication.getApplication().isLogin();//登录否？
//        boolean lockOpen = SharedPreferencesUtil.getInstance().getBooleanParam(GlobalConstant.WAKE_UP);//开启否？
//        boolean b = SharedPreferencesUtil.getInstance().getBooleanParam(SP_KEY_IS_NEED_SHOW_LOCK);//是否处于后台过？
//        return isLogin && isNeedChecke && lockOpen && b;
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        MobclickAgent.onResume(this);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        MobclickAgent.onPause(this);
//    }

    /**
     * 初始化语言
     */
//    private void initLanguage() {
//        Locale l = null;
//        int code = SharedPreferencesUtil.getInstance().getLanguageCode();
//        if (code == 1) l = Locale.CHINESE;
//        else if (code == 2) l = Locale.ENGLISH;
//
//        Resources resources = getApplicationContext().getResources();
//        DisplayMetrics dm = resources.getDisplayMetrics();
//        Configuration config = resources.getConfiguration();
//        config.locale = l;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            LocaleList localeList = new LocaleList(l);
//            LocaleList.setDefault(localeList);
//            config.setLocales(localeList);
//            getApplicationContext().createConfigurationContext(config);
//        }
//        Locale.setDefault(l);
//        resources.updateConfiguration(config, dm);
//    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {

    }

    protected abstract int getViewId();

    /**
     * 初始化控件
     *
     * @param savedInstanceState
     */
    protected void initViews(Bundle savedInstanceState) {
    }

    /**
     * 初始数据加载
     */
    protected void loadData() {

    }


    /**
     * 设置基础化控件
     */
    private void initBaseView() {
        loadingDialog = new LoadingDialog(activity);
        tvTitle = findViewById(R.id.tvTitle);
        ivBack = findViewById(R.id.ivBack);
        tvGoto = findViewById(R.id.tvGoto);
        llTitle = findViewById(R.id.llTitle);
        initImmersionBar();
        if (llTitle != null)
            immersionBar.setTitleBar(this, llTitle);
    }

    /**
     * 设定标题栏的高度
     */
    private void initImmersionBar() {
        if (immersionBar == null) {
            immersionBar = ImmersionBar.with(this);
            if (isNormalBar) {
                immersionBar.statusBarDarkFont(true, 0.3f);//设置顶部状态栏字体颜色(true黑色)
                immersionBar.flymeOSStatusBarFontColor("#000000").init();
            }
            immersionBar.keyboardEnable(false, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN).init();
        }
    }

    /**
     * 显示加载框
     */
    public void showLoading() {
        if (loadingDialog != null)
            loadingDialog.show();
    }

    /**
     * 关闭加载框
     */
    public void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing())
            loadingDialog.dismiss();
    }

    /**
     * 设置头部标题
     *
     * @param title
     */
    protected void setTitle(String title) {
        if (tvTitle != null)
            tvTitle.setText(title);
    }

    public void setShowBackBtn(boolean showBackBtn) {
        if (ivBack != null && showBackBtn) {
            ivBack.setVisibility(View.VISIBLE);
            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                }
            });
        }
    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    /**
     * 各控件的点击事件
     *
     * @param v
     */
    protected void setOnClickListener(View v) {

    }

    /**
     * 各控件的点击
     */
    protected void setListener() {

    }

    /**
     * 跳转activity,不关闭当前界面
     *
     * @param cls
     * @param bundle
     */
    protected void showActivity(Class<?> cls, Bundle bundle) {
        showActivity(cls, bundle, -1);
    }

    /**
     * 跳转activity,不关闭当前界面，含跳转回来的的回调
     *
     * @param cls
     * @param bundle
     */
    protected void showActivity(Class<?> cls, Bundle bundle, int requesCode) {
        Intent intent = new Intent(activity, cls);
        if (bundle != null)
            intent.putExtras(bundle);
        if (requesCode >= 0)
            startActivityForResult(intent, requesCode);
        else
            startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (immersionBar != null) {
            immersionBar.destroy();
        }
        EventBusUtils.unRegister(this);
        ActivityManage.removeActivity(this);
    }

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        SharedPreferences preferences = newBase.getSharedPreferences("language", Context.MODE_PRIVATE);
//        String selectedLanguage = preferences.getString("language", "");
//        super.attachBaseContext(LanguageUtil.attachBaseContext(newBase, selectedLanguage));
//    }


}
