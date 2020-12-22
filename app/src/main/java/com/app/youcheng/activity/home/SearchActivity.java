package com.app.youcheng.activity.home;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.adapter.SearchHistoryAdapter;
import com.app.youcheng.adapter.SearchRecordAdapter;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.entity.HomeRecordBean;
import com.app.youcheng.entity.SearchHistoryBean;
import com.app.youcheng.utils.NetCodeUtils;
import com.app.youcheng.utils.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.reflect.TypeToken;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

import static com.app.youcheng.GlobalConstant.ERROR_401;


public class SearchActivity extends BaseActivity {
    @BindView(R.id.llDefault)
    LinearLayout llDefault;
    @BindView(R.id.llCompany)
    LinearLayout llCompany;
    @BindView(R.id.llTopPP)
    LinearLayout llTopPP;
    @BindView(R.id.rvRecord)
    RecyclerView rvRecord;
    @BindView(R.id.rvSearch)
    RecyclerView rvSearch;
    @BindView(R.id.etSearch)
    EditText etSearch;
    @BindView(R.id.tvDown)
    TextView tvDown;

    private SearchRecordAdapter recordAdapter;
    private SearchHistoryAdapter historyAdapter;
    private List<HomeRecordBean> recordList = new ArrayList<>();
    private List<SearchHistoryBean> historyBeanList = new ArrayList<>();
    private int pageNo = 1;
    private int total;
    private int isSort = 0;
    private int score = 0;

    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
    }

    @Override
    protected void initData() {
        super.initData();

        initRv();
    }

    private void initRv() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvRecord.setLayoutManager(manager);
        recordAdapter = new SearchRecordAdapter(this, recordList);
        recordAdapter.bindToRecyclerView(rvRecord);
        recordAdapter.setEmptyView(R.layout.empty_no_message);

        LinearLayoutManager manager2 = new LinearLayoutManager(this);
        rvSearch.setLayoutManager(manager2);
        historyAdapter = new SearchHistoryAdapter(this, historyBeanList);
        historyAdapter.bindToRecyclerView(rvSearch);

        doRefreshHis();
    }

    private void doRefreshHis() {
        List<SearchHistoryBean> allHis = DataSupport.findAll(SearchHistoryBean.class);

        historyBeanList.clear();
        if (allHis != null && allHis.size() > 0) {
            historyBeanList.addAll(allHis);
            Collections.reverse(historyBeanList);
        }
        historyAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.tvBack, R.id.tvDown})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.tvBack:
                if (llCompany.getVisibility() == View.VISIBLE) {
                    clickBack();
                } else {
                    finish();
                }
                break;
            case R.id.tvDown:
                showPopulWindow();
                break;
        }
    }

    private void showPopulWindow() {
        String[] strings = new String[]{"分值排序", "优质企业", "虚假企业", "不诚实企业"};

        final ListPopupWindow listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strings));
        listPopupWindow.setAnchorView(llTopPP);
        listPopupWindow.setModal(true);

        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listPopupWindow.dismiss();

                if (llCompany.getVisibility() == View.VISIBLE) {
                    if (i == 0) {
                        if (isSort == 0) {
                            isSort = 1;
                        } else {
                            isSort = 0;
                        }
                    } else if (i == 1) {
                        score = 1;
                    } else if (i == 2) {
                        score = 2;
                    } else if (i == 3) {
                        score = 3;
                    }
                    pageNo = 1;
                    getBillRecord(true);
                }
            }
        });

        listPopupWindow.show();
    }

    private void clickBack() {
        isSort = 0;
        score = 0;
        etSearch.setText("");
        llDefault.setVisibility(View.VISIBLE);
        llCompany.setVisibility(View.GONE);
        recordList.clear();
        recordAdapter.notifyDataSetChanged();
        pageNo = 1;
    }

    @Override
    protected void setListener() {
        super.setListener();

        historyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (historyBeanList.size() > position) {
                    etSearch.setText(historyBeanList.get(position).getName());
                    doSearch();
                }
            }
        });

        historyAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.ivDelete) {
                    if (historyBeanList.size() > position) {
                        historyBeanList.get(position).delete();
                        doRefreshHis();
                    }
                }
            }
        });

        recordAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                if (MyApplication.getApplication().isLogin()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", 1);
                    bundle.putString("enterpriseId", recordList.get(position).getEnterpriseId());
                    showActivity(HomeRecordDetailActivity.class, bundle);
//                } else {
//                    ToastUtils.showToast("请先登录");
//                }
            }
        });

        recordAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (recordList.size() < total) {
                    pageNo = pageNo + 1;
                    getBillRecord(false);
                } else {
                    recordAdapter.loadMoreEnd();
                }
            }
        }, rvRecord);

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //当actionId == XX_SEND 或者 XX_DONE时都触发
                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    doSearch();
                }
                return false;
            }
        });
    }

    private void doSearch() {
        llDefault.setVisibility(View.GONE);
        llCompany.setVisibility(View.VISIBLE);

        pageNo = 1;
        getBillRecord(true);
    }

    private void getBillRecord(boolean isShow) {
        String keyword = etSearch.getText().toString();

        if (isShow && StringUtils.isNotEmpty(keyword)) {
            List<SearchHistoryBean> list = DataSupport.where("name = ?", keyword).find(SearchHistoryBean.class);
            if (list != null && list.size() > 0) {
                list.get(0).delete();
            }

            SearchHistoryBean searchHistoryBean = new SearchHistoryBean(keyword);
            searchHistoryBean.save();

            List<SearchHistoryBean> allHis = DataSupport.findAll(SearchHistoryBean.class);
            if (allHis != null && allHis.size() > 20) {
                allHis.get(0).delete();
            }

            doRefreshHis();
        }

        if (isShow) {
            showLoading();
        }
        EasyHttp.get(BaseHost.queryEnterpriseList)
                .params("pageNum", pageNo + "")
                .params("pageSize", GlobalConstant.pageSize + "")
                .params("keyword", keyword)
                .params("order", isSort + "")//0是false 1是true
                .params("score", score + "")//1,2,3
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        hideLoading();

                        try {
                            JSONObject object = new JSONObject(s);
                            int code = object.optInt("code");
                            if (code == GlobalConstant.OK) {
                                total = object.optInt("total");

                                List<HomeRecordBean> cloudRecordBeans = gson.fromJson(object.getJSONArray("data").toString(), new TypeToken<List<HomeRecordBean>>() {
                                }.getType());

                                billRecordSuccess(cloudRecordBeans);
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

    private void billRecordSuccess(List<HomeRecordBean> list) {
        recordAdapter.loadMoreComplete();

        if (list == null) {
            recordList.clear();
            recordAdapter.setEmptyView(R.layout.empty_no_message);
        } else {
            if (pageNo == 1) {
                recordList.clear();
                if (list.size() == 0) {
                    recordAdapter.setEmptyView(R.layout.empty_no_message);
                }
            } else if (list.size() == 0) {
                recordAdapter.loadMoreEnd();
            }
            recordList.addAll(list);
        }

        recordAdapter.notifyDataSetChanged();
        recordAdapter.disableLoadMoreIfNotFullPage();
    }

    @Override
    public void onBackPressed() {
        if (loadingDialog.isShowing()) {
            hideLoading();
            disAll();
        } else if (llCompany.getVisibility() == View.VISIBLE) {
            clickBack();
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
