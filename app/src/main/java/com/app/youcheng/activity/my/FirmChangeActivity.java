package com.app.youcheng.activity.my;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.dialog.PhotoDialog;
import com.app.youcheng.entity.FirmChangeParam;
import com.app.youcheng.entity.User;
import com.app.youcheng.entity.UserInfo;
import com.app.youcheng.utils.FileUtils;
import com.app.youcheng.utils.NetCodeUtils;
import com.app.youcheng.utils.StringUtils;
import com.app.youcheng.utils.ToastUtils;
import com.app.youcheng.utils.UriUtils;
import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.body.UIProgressResponseCallBack;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

import static com.app.youcheng.GlobalConstant.ERROR_401;


public class FirmChangeActivity extends BaseActivity {
    @BindView(R.id.ivLogo)
    ImageView ivLogo;

    private String filename = "header.jpg";
    private File imageFile;
    private File imageSelectFile;
    private Uri imageUri;
    private FirmChangeParam firmChangeParam;
    private UserInfo userInfo;

    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.activity_firm_change;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("我的企业");
        tvGoto.setVisibility(View.VISIBLE);
        tvGoto.setText("保存");
    }

    @Override
    protected void initData() {
        super.initData();

        firmChangeParam = new FirmChangeParam();
        imageFile = FileUtils.getMyFile(this, filename);

        User user = MyApplication.getApplication().getCurrentUser();
        if (user != null) {
            userInfo = user.getUserInfo();
            setParam();
        }
    }

    private void setParam() {
        if (userInfo != null) {
            firmChangeParam.setEnterpriseId(userInfo.getEnterpriseId());
            if (StringUtils.isNotEmpty(userInfo.getEnterpriseLogoPicture())) {
                Glide.with(this).load(userInfo.getEnterpriseLogoPicture()).into(ivLogo);
            }
        }
    }

    @Override
    protected void loadData() {
        super.loadData();
        getInfo();
    }

    @OnClick({R.id.tvGoto, R.id.llLogo, R.id.llLabel, R.id.llMan,
            R.id.llPhone, R.id.llAddress})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.tvGoto:
                if (StringUtils.isEmpty(firmChangeParam.getEnterpriseLogoPicture())) {
                    ToastUtils.showToast("保存成功");
                    finish();
                } else {
                    doSave();
                }
                break;
            case R.id.llLogo:
                showPhotoDialog();
                break;
            case R.id.llLabel:
                showActivity(LabelActivity.class, null, 0);
                break;
            case R.id.llMan:
                if (userInfo != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("userInfo", userInfo);
                    showActivity(SetManActivity.class, bundle, 0);
                }
                break;
            case R.id.llPhone:
                if (userInfo != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("userInfo", userInfo);
                    showActivity(SetPhoneActivity.class, bundle, 0);
                }
                break;
            case R.id.llAddress:
                if (userInfo != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("userInfo", userInfo);
                    showActivity(SetAddressActivity.class, bundle, 0);
                }
                break;
        }
    }

    private void showPhotoDialog() {
        final PhotoDialog photoDialog = new PhotoDialog(this);
        photoDialog.setClicklistener(new PhotoDialog.ClickListenerInterface() {
            @Override
            public void doCamera() {
                checkPermission(0);
                photoDialog.dismiss();
            }

            @Override
            public void doPhoto() {
                checkPermission(1);
                photoDialog.dismiss();
            }
        });
        photoDialog.show();
    }

    /**
     * 0 - 拍照   1 - 相册
     */
    private void checkPermission(final int type) {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.CAMERA)
                .permission(Permission.Group.STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        if (type == 0) {
                            startCamera();
                        } else {
                            chooseFromAlbum();
                        }
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        if (AndPermission.hasAlwaysDeniedPermission(activity, data)) {
                            AndPermission.permissionSetting(activity).execute();
                            return;
                        }
                        ToastUtils.showToast(getString(R.string.str_no_permission));
                    }
                }).start();
    }

    /**
     * 相机
     */
    private void startCamera() {
        if (imageFile == null) {
            ToastUtils.showToast(getString(R.string.str_unknown_error));
            return;
        }
        imageUri = FileUtils.getUriForFile(this, imageFile);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, GlobalConstant.TAKE_PHOTO);
    }

    /**
     * 相册
     */
    private void chooseFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GlobalConstant.CHOOSE_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    getInfo();
                    setResult(RESULT_OK);
                    break;
                case GlobalConstant.TAKE_PHOTO:
                    takePhotoReturn(resultCode, data);
                    break;
                case GlobalConstant.CHOOSE_ALBUM:
                    choseAlbumReturn(resultCode, data);
                    break;
            }
        }
    }

    /**
     * 相册选取返回
     */
    private void choseAlbumReturn(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        imageUri = data.getData();
        if (Build.VERSION.SDK_INT >= 19)
            imageSelectFile = UriUtils.getUriFromKitKat(this, imageUri);
        else
            imageSelectFile = UriUtils.getUriBeforeKitKat(this, imageUri);
        if (imageSelectFile == null) {
            ToastUtils.showToast(getString(R.string.library_file_exception));
            return;
        }
        doUpload(2);
    }

    /**
     * 拍照返回
     */
    private void takePhotoReturn(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        doUpload(1);
    }

    private void doUpload(int upType) {
        File file;
        if (upType == 1) {
            if (imageFile == null) {
                return;
            }
            file = imageFile;
        } else {
            if (imageSelectFile == null) {
                return;
            }
            file = imageSelectFile;
        }

        showLoading();
        EasyHttp.post(BaseHost.uploadUrl)
                .params("file", file, file.getName(), listener)
                .accessToken(true)
                .timeStamp(true)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        hideLoading();

                        try {
                            JSONObject object = new JSONObject(s);
                            int code = object.optInt("code");
                            if (code == GlobalConstant.OK) {
                                String url = object.getJSONObject("data").getString("url");

                                if (StringUtils.isNotEmpty(url)) {
                                    firmChangeParam.setEnterpriseLogoPicture(url);
                                    Glide.with(FirmChangeActivity.this).load(url).into(ivLogo);
                                }
                            } else {
                                NetCodeUtils.checkedErrorCode(object.optString("code"), object.optString("msg"));
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

    private UIProgressResponseCallBack listener = new UIProgressResponseCallBack() {
        @Override
        public void onUIResponseProgress(long bytesRead, long contentLength, boolean done) {

        }
    };

    private void doSave() {
        if (userInfo != null && firmChangeParam != null) {

        } else {
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("enterpriseLogoPicture", firmChangeParam.getEnterpriseLogoPicture());
            jsonObject.put("enterpriseId", userInfo.getEnterpriseId());
            jsonObject.put("billSureSet", 0);
            jsonObject.put("companyAddress", userInfo.getCompanyAddress());
            jsonObject.put("companyContacts", userInfo.getCompanyContacts());
            jsonObject.put("companyTel", userInfo.getCompanyTel());
            jsonObject.put("showAddress", userInfo.getShowAddress());//(0否,1是)
            jsonObject.put("showContacts", userInfo.getShowContacts());
            jsonObject.put("showTel", userInfo.getShowTel());

            JSONArray jsonArray = new JSONArray();
            List<UserInfo.EnterpriseLabelIdsBean> labelIdsBeanList = userInfo.getEnterpriseLabelIds();
            if (labelIdsBeanList != null && labelIdsBeanList.size() > 0) {
                for (int i = 0; i < labelIdsBeanList.size(); i++) {
                    if (StringUtils.isNotEmpty(labelIdsBeanList.get(i).getLabelValue())) {
                        jsonArray.put(labelIdsBeanList.get(i).getLabelValue());
                    }
                }
            }
            jsonObject.put("labelEntityList", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showLoading();
        EasyHttp.put(BaseHost.entInfo)
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
                                ToastUtils.showToast("保存成功");
                                setResult(RESULT_OK);
                                finish();
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

    private void getInfo() {
        if (MyApplication.getApplication().isLogin()) {
            showLoading();
            EasyHttp.get(BaseHost.infoUrl)
                    .execute(new SimpleCallBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            hideLoading();

                            try {
                                JSONObject object = new JSONObject(s);
                                int code = object.optInt("code");
                                if (code == GlobalConstant.OK) {
                                    UserInfo info = gson.fromJson(object.optString("data"), new TypeToken<UserInfo>() {
                                    }.getType());

                                    if (info != null) {
                                        userInfo = info;
                                    }
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
    }

    @Override
    public void onBackPressed() {
        if (loadingDialog.isShowing()) {
            hideLoading();
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
