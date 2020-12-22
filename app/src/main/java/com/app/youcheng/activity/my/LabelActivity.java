package com.app.youcheng.activity.my;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.adapter.LabelAdapter;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.dialog.AddLabelDialog;
import com.app.youcheng.dialog.DeleteTipDialog;
import com.app.youcheng.entity.LabelBean;
import com.app.youcheng.utils.NetCodeUtils;
import com.app.youcheng.utils.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.reflect.TypeToken;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

import static com.app.youcheng.GlobalConstant.ERROR_401;


public class LabelActivity extends BaseActivity {
    @BindView(R.id.rvRecord)
    RecyclerView rvRecord;

    private LabelAdapter recordAdapter;
    private List<LabelBean> recordList = new ArrayList<>();
    private AddLabelDialog addLabelDialog;
    private DeleteTipDialog deleteTipDialog;

    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.activity_label;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("企业标签");
    }

    @Override
    protected void initData() {
        super.initData();

        initRv();
    }

    @Override
    protected void loadData() {
        super.loadData();
        getLabelList(true);
    }

    private void initRv() {
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        rvRecord.setLayoutManager(manager);
        recordAdapter = new LabelAdapter(this, recordList);
        recordAdapter.bindToRecyclerView(rvRecord);
    }

    private void getLabelList(boolean isShow) {
        if (MyApplication.getApplication().isLogin()) {
            if (isShow) {
                showLoading();
            }
            EasyHttp.get(BaseHost.labelList)
                    .params("pageNum", "1")
                    .params("pageSize", GlobalConstant.pageSize + "")
                    .execute(new SimpleCallBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            closeAll();

                            try {
                                JSONObject object = new JSONObject(s);
                                int code = object.optInt("code");
                                if (code == GlobalConstant.OK) {
                                    List<LabelBean> list = gson.fromJson(object.optString("data"), new TypeToken<List<LabelBean>>() {
                                    }.getType());

                                    recordList.clear();
                                    recordList.addAll(list);
                                    recordAdapter.notifyDataSetChanged();
                                } else {
                                    if (code == ERROR_401) {
                                        //登录失效
                                        MyApplication.getApplication().doLogout();
                                        finish();
                                    } else {
                                        NetCodeUtils.checkedErrorCode(object.optString("code"), object.optString("msg"));
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ApiException e) {
                            closeAll();
                            NetCodeUtils.checkedErrorCode(e.getCode(), e.getMessage());
                        }
                    });
        }
    }

    @OnClick({R.id.llLabel})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.llLabel:
                if (recordList.size() < 5) {
                    showAddLabelDialog();
                } else {
                    ToastUtils.showToast("标签最多添加5个");
                }
                break;
        }
    }

    private void showAddLabelDialog() {
        addLabelDialog = new AddLabelDialog(this);
        addLabelDialog.setClicklistener(new AddLabelDialog.ClickListenerInterface() {
            @Override
            public void doSure(String s) {
                addLabelDialog.dismiss();
                doAdd(s);
            }
        });
        addLabelDialog.show();
    }

    @Override
    protected void setListener() {
        super.setListener();

        recordAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (recordList.size() > position) {
                    LabelBean labelBean = recordList.get(position);
                    if (labelBean != null) {
                        showDeleteTipDialog(labelBean.getLabelId());
                    }
                }
            }
        });
    }

    private void showDeleteTipDialog(final String id) {
        deleteTipDialog = new DeleteTipDialog(this, "");
        deleteTipDialog.setClicklistener(new DeleteTipDialog.ClickListenerInterface() {
            @Override
            public void doSure() {
                deleteTipDialog.dismiss();
                doDelete(id);
            }
        });
        deleteTipDialog.show();
    }

    private void doAdd(String str) {
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("labelValue", str);

            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showLoading();
        EasyHttp.post(BaseHost.labelAdd)
                .headers("Content-Type", "application/json")
                .upJson(jsonArray.toString())
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        hideLoading();

                        try {
                            JSONObject object = new JSONObject(s);
                            int code = object.optInt("code");
                            if (code == GlobalConstant.OK) {
                                ToastUtils.showToast("添加成功");
                                getLabelList(true);
                                setResult(RESULT_OK);
                            } else {
                                if (code == ERROR_401) {
                                    //登录失效
                                    MyApplication.getApplication().doLogout();
                                    finish();
                                } else {
                                    NetCodeUtils.checkedErrorCode(object.optString("code"), object.optString("msg"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ApiException e) {
                        hideLoading();
                        NetCodeUtils.checkedErrorCode(e.getCode(), e.getMessage());
                    }
                });
    }

    private void doDelete(String id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("labelId", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showLoading();
        EasyHttp.post(BaseHost.labelDelete + id)
                .headers("Content-Type", "application/json")
                .upJson(jsonObject.toString())
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        hideLoading();

                        try {
                            JSONObject object = new JSONObject(s);
                            int code = object.optInt("code");
                            if (code == GlobalConstant.OK) {
                                ToastUtils.showToast("删除成功");
                                getLabelList(true);
                                setResult(RESULT_OK);
                            } else {
                                if (code == ERROR_401) {
                                    //登录失效
                                    MyApplication.getApplication().doLogout();
                                    finish();
                                } else {
                                    NetCodeUtils.checkedErrorCode(object.optString("code"), object.optString("msg"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ApiException e) {
                        hideLoading();
                        NetCodeUtils.checkedErrorCode(e.getCode(), e.getMessage());
                    }
                });
    }

    private void closeAll() {
        hideLoading();
    }

    @Override
    public void onBackPressed() {
        if (loadingDialog.isShowing()) {
            closeAll();
            disAll();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disAll();
    }

    private void disAll() {
        if (disposable1 != null) {
            EasyHttp.cancelSubscription(disposable1);
        }
    }


}
