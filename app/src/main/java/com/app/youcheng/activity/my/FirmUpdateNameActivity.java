package com.app.youcheng.activity.my;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.app.youcheng.BaseHost;
import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.R;
import com.app.youcheng.base.BaseActivity;
import com.app.youcheng.dialog.PhotoDialog;
import com.app.youcheng.entity.User;
import com.app.youcheng.utils.FileUtils;
import com.app.youcheng.utils.NetCodeUtils;
import com.app.youcheng.utils.StringUtils;
import com.app.youcheng.utils.ToastUtils;
import com.app.youcheng.utils.UriUtils;
import com.bumptech.glide.Glide;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.body.UIProgressResponseCallBack;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

import static com.app.youcheng.GlobalConstant.ERROR_401;


public class FirmUpdateNameActivity extends BaseActivity {
    @BindView(R.id.iv1)
    ImageView iv1;
    @BindView(R.id.iv3)
    ImageView iv3;
    @BindView(R.id.iv4)
    ImageView iv4;

    @BindView(R.id.etEnterpriseName)
    EditText etEnterpriseName;
    @BindView(R.id.etEnterpriseCode)
    EditText etEnterpriseCode;

    private int type = 1;
    private String filename = "header.jpg";

    private File imageFile;
    private File imageSelectFile;
    private Uri imageUri;
    private String oneData;
    //    private String twoData;
    private String threeData;
    private String fourData;

    private Disposable disposable1;

    @Override
    protected int getViewId() {
        return R.layout.activity_my_update_firm_name;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setShowBackBtn(true);
        setTitle("企业更名");
        tvGoto.setText("提交");
        tvGoto.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {
        super.initData();

        imageFile = FileUtils.getMyFile(this, filename);
    }

    @OnClick({R.id.tvGoto, R.id.iv1, R.id.iv3, R.id.iv4,})
    @Override
    protected void setOnClickListener(View v) {
        super.setOnClickListener(v);

        switch (v.getId()) {
            case R.id.iv1:
                showPhotoDialog(1);
                break;
            case R.id.iv3:
                showPhotoDialog(3);
                break;
            case R.id.iv4:
                showPhotoDialog(4);
                break;
            case R.id.tvGoto:
                doSave();
                break;
        }
    }

    private void showPhotoDialog(int type) {
        this.type = type;

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
                                    if (type == 1) {
                                        oneData = url;
                                        Glide.with(FirmUpdateNameActivity.this).load(oneData).into(iv1);
                                    } else if (type == 3) {
                                        threeData = url;
                                        Glide.with(FirmUpdateNameActivity.this).load(threeData).into(iv3);
                                    } else if (type == 4) {
                                        fourData = url;
                                        Glide.with(FirmUpdateNameActivity.this).load(fourData).into(iv4);
                                    }
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
//            int progress = (int) (bytesRead * 100 / contentLength);
//            HttpLog.e(progress + "% ");
//            dialog.setProgress(progress);
//            dialog.setMessage(progress + "%");
//            if (done) {//完成
//                dialog.setMessage("上传完整");
//            }
        }
    };

    private void doSave() {
        User user = MyApplication.getApplication().getCurrentUser();
        if (user != null && user.getUserInfo() != null) {

        } else {
            return;
        }

        String enterpriseName = etEnterpriseName.getText().toString();
        String enterpriseCode = etEnterpriseCode.getText().toString();

        if (StringUtils.isNotEmpty(enterpriseName, enterpriseCode, oneData, threeData, fourData)) {

        } else {
            ToastUtils.showToast("请完善信息");
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("businessLicensePicture", oneData);
            jsonObject.put("cardPictureBack", fourData);
            jsonObject.put("cardPictureFront", threeData);
            jsonObject.put("enterpriseCode", enterpriseCode);
            jsonObject.put("enterpriseName", enterpriseName);
            jsonObject.put("enterpriseId", user.getUserInfo().getEnterpriseId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showLoading();
        EasyHttp.put(BaseHost.entName)
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
                                ToastUtils.showToast("已提交");
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
