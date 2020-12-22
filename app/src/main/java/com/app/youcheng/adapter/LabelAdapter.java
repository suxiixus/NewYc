package com.app.youcheng.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.app.youcheng.R;
import com.app.youcheng.entity.LabelBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class LabelAdapter extends BaseQuickAdapter<LabelBean, BaseViewHolder> {
    private Context context;

    public LabelAdapter(Context context, @Nullable List<LabelBean> data) {
        super(R.layout.adapter_txt, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, LabelBean item) {
        helper.setText(R.id.tvName, item.getLabelValue());
    }


}
