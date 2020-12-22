package com.app.youcheng.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.app.youcheng.R;
import com.app.youcheng.entity.FirmDetailBean;
import com.app.youcheng.utils.StringUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class FirmSayAdapter extends BaseQuickAdapter<FirmDetailBean.EvaluationBean, BaseViewHolder> {
    private Context context;

    public FirmSayAdapter(Context context, @Nullable List<FirmDetailBean.EvaluationBean> data) {
        super(R.layout.adapter_firm_say, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, FirmDetailBean.EvaluationBean item) {
        helper.setText(R.id.tvName, item.getUserName())
                .setText(R.id.tvTime, item.getCreateTime())
                .setText(R.id.tvContent, item.getAdminEvaluationContent());

        if (StringUtils.isNotEmpty(item.getLogo())) {
            Glide.with(context).load(item.getLogo()).into((ImageView) helper.getView(R.id.ivLogo));
        }
    }

}
