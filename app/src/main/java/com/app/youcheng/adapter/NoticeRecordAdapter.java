package com.app.youcheng.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.app.youcheng.R;
import com.app.youcheng.entity.NoticeBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class NoticeRecordAdapter extends BaseQuickAdapter<NoticeBean, BaseViewHolder> {
    private Context context;

    public NoticeRecordAdapter(Context context, @Nullable List<NoticeBean> data) {
        super(R.layout.adapter_notice_record, data);
        this.context = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, NoticeBean item) {
        helper.setText(R.id.tvTime, item.getCreateTime())
                .setText(R.id.tvTitle, item.getNoticeTitle());
    }


}
