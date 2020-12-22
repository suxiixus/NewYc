package com.app.youcheng.activity.set;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.ImageView;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.dialog.LogoutDialog;
import com.app.youcheng.entity.EventBean;
import com.app.youcheng.utils.EventBusUtils;
import com.app.youcheng.utils.NetCodeUtils;
import com.app.youcheng.utils.ToastUtils;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import io.reactivex.disposables.Disposable;


public class SetActivity extends BaseActivity {
    public static final String SETTINGS_ACTION = "android.settings.APPLICATION_DETAILS_SETTINGS";

    @BindView(R.id.ivMsgStatus)
    ImageView ivMsgStatus;

    private LogoutDialog logoutDialog;

    private Disposable disposable1;

    @Override
    protected void onResume() {
        super.onResume();
        setStatus();
    }

    @Override
    protected int getViewId() {
        return R.layout.activity_set;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("设置");
    }

    @Override
    protected void initData() {
        super.initData();

        setStatus();
    }

    @OnClick({R.id.ivMsgStatus, R.id.tvLogout, R.id.llSafe,
            R.id.llSms, R.id.llOpen, R.id.llHC})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.ivMsgStatus:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Intent intent = new Intent()
                            .setAction(SETTINGS_ACTION)
                            .setData(Uri.fromParts("package",
                                    getApplicationContext().getPackageName(), null));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent()
                            .setAction(SETTINGS_ACTION)
                            .setData(Uri.fromParts("package",
                                    getApplicationContext().getPackageName(), null));
                    startActivity(intent);
                }
                break;
            case R.id.tvLogout:
                if (MyApplication.getApplication().isLogin()) {
                    showLogoutDialog();
                }
                break;
            case R.id.llSafe:
                showActivity(SetSafeActivity.class, null, 0);
                break;
            case R.id.llSms:
                showActivity(SetSmsActivity.class, null, 0);
                break;
            case R.id.llOpen:
                showActivity(SetOpenActivity.class, null, 0);
                break;
            case R.id.llHC:
                ToastUtils.showToast("清理缓存成功");
                break;
        }
    }

    private void showLogoutDialog() {
        if (logoutDialog == null) {
            logoutDialog = new LogoutDialog(this);
            logoutDialog.setClicklistener(new LogoutDialog.ClickListenerInterface() {
                @Override
                public void doSure() {
                    logoutDialog.dismiss();

                    doLogout();
                }
            });
            logoutDialog.show();
        } else {
            logoutDialog.show();
        }
    }

    private void doLogout() {
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
                                    JPushInterface.deleteAlias(SetActivity.this, 0);

                                    ToastUtils.showToast("退出登录成功");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                setStatus();
                setResult(RESULT_OK);
                break;
        }
    }

    private void setStatus() {
        NotificationManagerCompat notification = NotificationManagerCompat.from(this);
        ivMsgStatus.setSelected(notification.areNotificationsEnabled());
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
