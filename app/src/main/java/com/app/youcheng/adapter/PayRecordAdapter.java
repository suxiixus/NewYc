package com.app.youcheng.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.app.youcheng.R;
import com.app.youcheng.entity.PayRecordBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class PayRecordAdapter extends BaseQuickAdapter<PayRecordBean, BaseViewHolder> {
    private Context context;

    public PayRecordAdapter(Context context, @Nullable List<PayRecordBean> data) {
        super(R.layout.adapter_pay_record, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, PayRecordBean item) {
        helper.setText(R.id.tvTime, item.getPostTime())
                .setText(R.id.tvMsg, item.getMessage())
                .setText(R.id.tvPayTxt, getTxt(item.getPayChannel()))
                .setImageResource(R.id.ivPay, getRes(item.getPayChannel()))
                .setText(R.id.tvMoney, "-" + item.getOrderAmount() + "元");
    }

    private String getTxt(int status) {
        //支付渠道(0余额，1,支付宝，2微信)
        String str = "余额";

        if (status == 0) {
            str = "余额";
        } else if (status == 1) {
            str = "支付宝";
        } else if (status == 2) {
            str = "微信";
        }
        return str;
    }

    private int getRes(int status) {
        //支付渠道(0余额，1,支付宝，2微信)
        int res = R.mipmap.icon_pay_yue;

        if (status == 0) {
            res = R.mipmap.icon_pay_yue;
        } else if (status == 1) {
            res = R.mipmap.icon_pay_ali;
        } else if (status == 2) {
            res = R.mipmap.icon_pay_wechat;
        }
        return res;
    }


}
