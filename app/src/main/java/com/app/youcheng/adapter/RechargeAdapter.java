package com.app.youcheng.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.app.youcheng.R;
import com.app.youcheng.entity.HomeRecordBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class RechargeAdapter extends BaseQuickAdapter<HomeRecordBean, BaseViewHolder> {
    private Context context;

    public RechargeAdapter(Context context, @Nullable List<HomeRecordBean> data) {
        super(R.layout.adapter_recharge_money, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeRecordBean item) {
//        String amount = MathUtils.getBigDecimalAdd(item.getTradeAmount(), item.getFeeAmount(), 8);
//
//        helper.setText(R.id.tvAmount, getFUHao(item.getTradeType()) + amount + " " + item.getSymbol());
//
//        helper.setImageResource(R.id.ivIcon, getIcon(item.getTradeType()))
//                .setText(R.id.tvType, getType(item.getTradeType()))
//                .setText(R.id.tvRemark, item.getToAddress())
//                .setText(R.id.tvStatus, getStatus(item.getStatus()))
//                .setTextColor(R.id.tvStatus, getStatusColor(item.getStatus()))
//                .setText(R.id.tvTime, DateUtils.getMyTime(item.getCreateTime()));
    }


}
