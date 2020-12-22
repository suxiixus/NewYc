package com.app.youcheng.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.app.youcheng.R;
import com.app.youcheng.entity.UserAccount;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class FirmAccountAdapter extends BaseQuickAdapter<UserAccount, BaseViewHolder> {
    private Context context;

    public FirmAccountAdapter(Context context, @Nullable List<UserAccount> data) {
        super(R.layout.adapter_firm_account, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, UserAccount item) {
        helper.setText(R.id.tvName, item.getNickName())
                .setText(R.id.tvPhone, item.getPhonenumber())
                .setText(R.id.tvUserType, getStatus(item.getUserType()));

        TextView tvJieBang = helper.getView(R.id.tvJieBang);
        if (item.getUserType() == 2) {
            tvJieBang.setVisibility(View.GONE);
        } else {
            tvJieBang.setVisibility(View.VISIBLE);
        }

        helper.addOnClickListener(R.id.tvJieBang);
    }

    private String getStatus(int type) {
        //00系统用户 01 客服，02 用户管理员，03 企业员工
        String status = "";
        if (type == 0) {
            status = "系统用户";
        } else if (type == 1) {
            status = "客服";
        } else if (type == 2) {
            status = "用户管理员";
        } else if (type == 3) {
            status = "企业员工";
        }

        return status;
    }

}
