package com.app.youcheng.activity.home;


import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.app.youcheng.R;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.utils.StringUtils;

import butterknife.BindView;

/**
 * 详情
 */
public class NoticeDetailActivity extends BaseActivity {
    @BindView(R.id.wb)
    WebView wb;

    @Override
    protected int getViewId() {
        return R.layout.activity_notice_detail;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("消息详情");
    }

    @Override
    protected void initData() {
        super.initData();

        initWb();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String content = getIntent().getStringExtra("content");
            if (StringUtils.isNotEmpty(content)) {
                wb.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
            }
        }
    }

    private void initWb() {
        wb.getSettings().setJavaScriptEnabled(true);
        wb.getSettings().setSupportZoom(false);
        wb.getSettings().setBuiltInZoomControls(false);
        wb.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        wb.setVerticalScrollBarEnabled(false);
        wb.setHorizontalScrollBarEnabled(false);
        wb.setBackgroundColor(getResources().getColor(R.color.white));
    }


}
