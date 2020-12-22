package com.app.youcheng.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.app.youcheng.R;
import com.app.youcheng.entity.HomeRecordBean;
import com.app.youcheng.utils.StringUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class SearchRecordAdapter extends BaseQuickAdapter<HomeRecordBean, BaseViewHolder> {
    private Context context;

    public SearchRecordAdapter(Context context, @Nullable List<HomeRecordBean> data) {
        super(R.layout.adapter_home_record, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeRecordBean item) {

        String label = "";
        List<String> labelValues = item.getLabelValues();
        if (labelValues != null && labelValues.size() > 0) {
            label = labelValues.get(0);
            for (int i = 1; i < labelValues.size(); i++) {
                label = label + " " + labelValues.get(i);
            }
        }

        helper.setText(R.id.tvName, item.getEnterpriseName())
                .setText(R.id.tvStatus, getStatus(item.getStatus()))
                .setTextColor(R.id.tvStatus, item.getStatus() == 1 ? context.getResources().getColor(R.color.white)
                        : context.getResources().getColor(R.color.font_main_black))
                .setBackgroundColor(R.id.tvStatus, item.getStatus() == 1 ? context.getResources().getColor(R.color.main_blue)
                        : context.getResources().getColor(R.color.main_btn_gray))
                .setText(R.id.tvRank, item.getEnterpriseRank() + "")
                .setText(R.id.tvOverDue, item.getOverdueLoan())
                .setText(R.id.tvLabel, label);

        if (StringUtils.isNotEmpty(item.getEnterpriseLogoPicture())) {
            Glide.with(context).load(item.getEnterpriseLogoPicture()).into((ImageView) helper.getView(R.id.ivLogo));
        }

        ImageView ivStatus = helper.getView(R.id.ivStatus);

        if (StringUtils.isNotEmpty(item.getCompanyScore())) {
            //0 优质企业 1，虚假企业 2 不诚实企业
            if (item.getCompanyScore().equals("1")) {
                ivStatus.setImageResource(R.mipmap.icon_home_status_xujia);
            } else if (item.getCompanyScore().equals("2")) {
                ivStatus.setImageResource(R.mipmap.icon_home_status_bushi);
            } else {
                ivStatus.setImageResource(R.mipmap.icon_home_status_xing);
            }
        } else {
            ivStatus.setImageResource(R.drawable.shape_bg_transparent);
        }
    }

    //0:未注册，1已注册,2未通过审核
    private String getStatus(int status) {
        String str = "";
        if (status == 0) {
            str = "未注册";
        } else if (status == 1) {
            str = "已注册";
        } else if (status == 2) {
            str = "未通过审核";
        }

        return str;
    }

}
