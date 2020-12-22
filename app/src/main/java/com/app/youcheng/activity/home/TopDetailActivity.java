package com.app.youcheng.activity.home;


import android.os.Bundle;
import android.widget.ImageView;

import com.app.youcheng.R;
import com.app.youcheng.base.BaseActivity;

import butterknife.BindView;


public class TopDetailActivity extends BaseActivity {
    @BindView(R.id.ivBg)
    ImageView ivBg;

    @Override
    protected int getViewId() {
        return R.layout.activity_home_top;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();

        int pos = getIntent().getIntExtra("pos", 0);
        if (pos == 0) {
            setTitle("账单使用攻略");
        } else {
            setTitle("账单使用流程");
            ivBg.setImageResource(R.mipmap.icon_home_top_detail2);
        }
    }


}
