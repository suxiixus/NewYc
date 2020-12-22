package com.app.youcheng.base;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.youcheng.R;
import com.app.youcheng.dialog.LoadingDialog;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {
    protected View rootView;
    private Unbinder unbinder;
    protected ImageView ivBack;
    protected TextView tvTitle;
    protected TextView tvGoto;
    protected LinearLayout llTitle;
    protected Activity activity;
    protected LoadingDialog loadingDialog;
    private ImmersionBar immersionBar;
    protected Gson gson = new Gson();

    private boolean isNeedDes = true;
    protected boolean isFirst = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutId(), null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        activity = getActivity();
        initBaseView();
        initViews(savedInstanceState);
        initData();
        setListener();

        rootView.post(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        });
    }

    /**
     * 加载基础控件
     */
    private void initBaseView() {
        loadingDialog = ((BaseActivity) getActivity()).getloadingdialog();
        tvTitle = rootView.findViewById(R.id.tvTitle);
        ivBack = rootView.findViewById(R.id.ivBack);
        tvGoto = rootView.findViewById(R.id.tvGoto);
        llTitle = rootView.findViewById(R.id.llTitle);
        initImmersionBar();
        if (llTitle != null)
            immersionBar.setTitleBar(activity, llTitle);
    }

    /**
     * 设定标题栏的高度
     */
    private void initImmersionBar() {
        if (immersionBar == null) {
            immersionBar = ImmersionBar.with(this);
            immersionBar.statusBarDarkFont(true, 0.3f);//设置顶部状态栏字体颜色(true黑色)
            immersionBar.flymeOSStatusBarFontColor("#000000").init();
            immersionBar.keyboardEnable(false, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN).init();
        }
    }

    /**
     * 设置标题
     *
     * @param title
     */
    protected void setTitle(String title) {
        if (tvTitle != null)
            tvTitle.setText(title);
    }

    /**
     * 是否显示返回键
     *
     * @param showBackBtn
     */
    public void setShowBackBtn(boolean showBackBtn) {
        if (ivBack != null && showBackBtn) {
            if (showBackBtn) {
                ivBack.setVisibility(View.VISIBLE);
                ivBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.finish();
                    }
                });
            } else {
                ivBack.setVisibility(View.GONE);
            }
        }
    }

    protected abstract int getLayoutId();

    protected void initViews(Bundle savedInstanceState) {
    }

    protected void loadData() {
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


    public void showLoading() {
        if (loadingDialog != null)
            loadingDialog.show();
    }

    public void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing())
            loadingDialog.dismiss();
    }

    @Override
    public View getView() {
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (immersionBar != null && isNeedDes) {
            immersionBar.destroy();
        }
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public void setNeedDes(boolean needDes) {
        isNeedDes = needDes;
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

    protected abstract String getmTag();

}
