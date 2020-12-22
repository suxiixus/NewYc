package com.app.youcheng.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.app.youcheng.R;
import com.app.youcheng.entity.FirmDetailBean;
import com.app.youcheng.utils.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class FirmRankAdapter extends BaseQuickAdapter<FirmDetailBean.EnterpriseRankBean, BaseViewHolder> {
    private Context context;

    public FirmRankAdapter(Context context, @Nullable List<FirmDetailBean.EnterpriseRankBean> data) {
        super(R.layout.adapter_firm_rank, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, FirmDetailBean.EnterpriseRankBean item) {
        helper.setText(R.id.tvName, getName(item, item.getSettlementReason()))
//                .setText(R.id.tvID, item.getRankId())
                .setText(R.id.tvTime, item.getSettlementTime())
                .setText(R.id.tvFen, getFen(item.getSettlementReason(), item.getRankCalculation()))
                .setTextColor(R.id.tvFen, getResColor(item.getSettlementReason(), item.getRankCalculation()));
    }

    private String getName(FirmDetailBean.EnterpriseRankBean item, int type) {
        //0逾期确认，1按期支付,2平台增加，3平台扣除
        String str = "---";

        if (type == 0) {
            str = "逾期确认";
        } else if (type == 1) {
            str = "按期支付";
        } else if (type == 2) {
            str = "平台增加";
        } else if (type == 3) {
            str = "平台扣除";
        }

        if (StringUtils.isNotEmpty(item.getSendEnterpriseName())) {
            str = str + item.getSendEnterpriseName() + "账单";
        } else {
            str = str + "***账单";
        }

        if (StringUtils.isNotEmpty(item.getBillAmountMoney())) {
            str = str + " " + item.getBillAmountMoney() + "元";
        } else {
            str = str + " ***元";
        }


        return str;
    }

    private String getFen(int type, int fen) {
        //0逾期确认，1按期支付,2平台增加，3平台扣除
        String str = fen + "分";
        if (type == 0) {
            str = fen + "分";
        } else if (type == 1) {
            str = "+" + fen + "分";
        } else if (type == 2) {
            str = "+" + fen + "分";
        } else if (type == 3) {
            str = fen + "分";
        }
        return str;
    }

    private int getResColor(int type, int fen) {
        //0逾期确认，1按期支付,2平台增加，3平台扣除
        if (type == 0) {
            return context.getResources().getColor(R.color.font_red);
        } else if (type == 1) {
            return context.getResources().getColor(R.color.font_green);
        } else if (type == 2) {
            return context.getResources().getColor(R.color.font_green);
        } else if (type == 3) {
            return context.getResources().getColor(R.color.font_red);
        } else {
            return context.getResources().getColor(R.color.font_main_black);
        }
    }


}
