package com.app.youcheng.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.app.youcheng.R;
import com.app.youcheng.entity.BillBean;
import com.app.youcheng.utils.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class BillRecordAdapter extends BaseQuickAdapter<BillBean, BaseViewHolder> {
    private Context context;
    private int type = 0;

    public BillRecordAdapter(Context context, @Nullable List<BillBean> data, int type) {
        super(R.layout.adapter_bill_record, data);
        this.context = context;
        this.type = type;
    }

    @Override
    protected void convert(BaseViewHolder helper, BillBean item) {
        String phone = "";

        if (type == 0) {
            if (StringUtils.isNotEmpty(item.getReceiveUserName(), item.getReceiveUserPhone())) {
                phone = item.getReceiveUserName() + "：" + item.getReceiveUserPhone();
            }

            helper.setText(R.id.tvName, item.getSendUserName())
                    .setText(R.id.tvStatus, getStatus(item.getReceiveStatus()))
                    .setText(R.id.tvFirm, "支付企业：" + item.getReceiveEnterpriseName())
                    .setText(R.id.tvPhone, phone);
        } else if (type == 1) {
            if (StringUtils.isNotEmpty(item.getSendUserName(), item.getSendUserPhone())) {
                phone = item.getSendUserName() + "：" + item.getSendUserPhone();
            }

            helper.setText(R.id.tvName, item.getEnterpriseRealName())
                    .setText(R.id.tvStatus, getStatus(item.getReceiveStatus()))
                    .setText(R.id.tvFirm, "支付企业：" + item.getSendEnterpriseName())
                    .setText(R.id.tvPhone, phone);
        } else {
            if (StringUtils.isNotEmpty(item.getReceiveUserName(), item.getReceiveUserPhone())) {
                phone = item.getReceiveUserName() + "：" + item.getReceiveUserPhone();
            }

            helper.setText(R.id.tvName, item.getSendUserName())
                    .setText(R.id.tvStatus, "待评价")
                    .setText(R.id.tvFirm, "支付企业：" + item.getReceiveEnterpriseName())
                    .setText(R.id.tvPhone, phone);
        }

        helper.setText(R.id.tvTime, item.getSendTime())
                .setText(R.id.tvMoney, "账单金额：" + item.getBillAmountMoney() + "元");
    }

    private String getStatus(int status) {
        //(0 已支付,1,未支付,2,已逾期,3申请复核)
        String str = "";

        if (status == 0) {
            str = "已支付";
        } else if (status == 1) {
            str = "未支付";
        } else if (status == 2) {
            str = "已逾期";
        } else if (status == 3) {
            str = "申请复核";
        } else if (status == 4) {
            str = "待评价";
        } else if (status == 5) {
            str = "已评价";
        }
        return str;
    }


}
