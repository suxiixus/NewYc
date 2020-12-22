package com.app.youcheng.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.app.youcheng.R;
import com.app.youcheng.entity.BillMsgBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class BillMsgAdapter extends BaseQuickAdapter<BillMsgBean, BaseViewHolder> {
    private Context context;

    public BillMsgAdapter(Context context, @Nullable List<BillMsgBean> data) {
        super(R.layout.adapter_notice_record, data);
        this.context = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, BillMsgBean item) {
        helper.setText(R.id.tvTime, item.getCreateTime())
                .setText(R.id.tvTitle, item.getContent());
    }


}
