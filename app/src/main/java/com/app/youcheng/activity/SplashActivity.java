package com.app.youcheng.activity;

import android.os.Bundle;
import android.os.Handler;

import com.app.youcheng.MainActivity;
import com.app.youcheng.R;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.utils.SharedPreferencesUtil;
import com.gyf.barlibrary.ImmersionBar;

import static com.app.youcheng.GlobalConstant.SP_KEY_IS_NEED_SHOW_LOCK;


/**
 * 启动页
 */
public class SplashActivity extends BaseActivity {
//    @BindView(R.id.ivBg)
//    ImageView ivBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferencesUtil.getInstance().setParam(SP_KEY_IS_NEED_SHOW_LOCK, false);
        isNeedChecke = false;
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).fullScreen(false).init();

//        ImageBgUtils.scaleImage(this, ivBg, R.drawable.bg_splash);
    }

    @Override
    protected int getViewId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initData() {
        super.initData();

        if (!isTaskRoot()) {
            finish();
        } else {
            timerStart();
        }
    }

    @Override
    protected void loadData() {
        super.loadData();

    }

    private void timerStart() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                boolean isFirst = SharedPreferencesUtil.getInstance().getBooleanParam(GlobalConstant.IS_FIRST_LOGIN);
//                if (isFirst) {
//                    showActivity(LeadActivity.class, null);
//                } else {
                showActivity(MainActivity.class, null);
//                }
                finish();
            }
        }, 1500);
    }


}
