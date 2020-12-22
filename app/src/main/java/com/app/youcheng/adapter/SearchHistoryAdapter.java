package com.app.youcheng.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.app.youcheng.R;
import com.app.youcheng.entity.SearchHistoryBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class SearchHistoryAdapter extends BaseQuickAdapter<SearchHistoryBean, BaseViewHolder> {
    private Context context;

    public SearchHistoryAdapter(Context context, @Nullable List<SearchHistoryBean> data) {
        super(R.layout.adapter_search_history, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchHistoryBean item) {
        helper.setText(R.id.tvName, item.getName());

        helper.addOnClickListener(R.id.ivDelete);
    }


}
