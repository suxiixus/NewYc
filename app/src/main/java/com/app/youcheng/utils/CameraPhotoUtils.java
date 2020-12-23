package com.app.youcheng.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.app.youcheng.R;

import java.io.File;

public class CameraPhotoUtils {
    public static final int CAMERA_PHOTO = 100;
    public static final int SELECT_PHOTO = 101;
    public static final int ZOOM_PHOTO = 102;

    private File imageCameraFile;
    private File imageSelectFile;
    private File imageZoomFile;
    private Uri imageUri;
    private CameraPhotoListener mListener;

    private Activity mActivity;
    private Fragment mFragment;
    private int type;
    private boolean mShouldCrop = false;//是否要裁剪（默认不裁剪）
    private String filename = "cachePhoto.jpg";
    private String zoomName = "zoomPhoto.jpg";
    private Context mContext;

    //剪裁图片宽高比
    private int mAspectX = 1;
    private int mAspectY = 1;
    //剪裁图片大小
    private int mOutputX = 400;
    private int mOutputY = 400;

    public CameraPhotoUtils(Activity activity, CameraPhotoListener photoSelectListener, boolean shouldCrop) {
        type = 0;
        mActivity = activity;
        mContext = activity.getApplicationContext();
        mListener = photoSelectListener;
        mShouldCrop = shouldCrop;
    }

    public CameraPhotoUtils(Fragment fragment, CameraPhotoListener photoSelectListener, boolean shouldCrop) {
        type = 1;
        mFragment = fragment;
        mContext = fragment.getActivity().getApplicationContext();
        mListener = photoSelectListener;
        mShouldCrop = shouldCrop;
    }

    public void startCamera() {
        imageCameraFile = FileUtils.getMyFile(mActivity, filename);
        if (imageCameraFile != null) {
            imageUri = FileUtils.getUriForFile(mContext, imageCameraFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            if (type == 0) {
                mActivity.startActivityForResult(intent, CAMERA_PHOTO);
            } else {
                mFragment.startActivityForResult(intent, CAMERA_PHOTO);
            }
        }
    }

    public void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        if (type == 0) {
            mActivity.startActivityForResult(intent, SELECT_PHOTO);
        } else {
            mFragment.startActivityForResult(intent, SELECT_PHOTO);
        }
    }

    public void attachToActivityForResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_PHOTO://拍照
                    if (mShouldCrop) {
                        zoomPhoto();
                    } else {
                        if (mListener != null) {
                            mListener.onFinish(imageCameraFile);
                        }
                    }
                    break;
                case SELECT_PHOTO://图库
                    if (data != null) {
                        imageUri = data.getData();

                        if (mShouldCrop) {
                            zoomPhoto();
                        } else {
                            if (Build.VERSION.SDK_INT >= 19)
                                imageSelectFile = UriUtils.getUriFromKitKat(mContext, imageUri);
                            else
                                imageSelectFile = UriUtils.getUriBeforeKitKat(mContext, imageUri);
                            if (imageSelectFile == null) {
                                ToastUtils.showToast(mContext.getString(R.string.library_file_exception));
                                return;
                            }

                            if (mListener != null) {
                                mListener.onFinish(imageSelectFile);
                            }
                        }
                    }
                    break;
                case ZOOM_PHOTO:
                    if (data != null) {
                        if (mListener != null) {
                            mListener.onFinish(imageZoomFile);
                        }
                    }
                    break;
            }
        }
    }

    private void zoomPhoto() {
        if (imageUri != null) {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(imageUri, "image/*");
            intent.putExtra("crop", "true");

            //设置剪裁图片宽高比
            intent.putExtra("mAspectX", mAspectX);
            intent.putExtra("mAspectY", mAspectY);

            //设置剪裁图片大小
            intent.putExtra("mOutputX", mOutputX);
            intent.putExtra("mOutputY", mOutputY);

            // 是否返回uri
            intent.putExtra("return-data", false);

            imageZoomFile = FileUtils.getMyFile(mActivity, zoomName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageZoomFile));
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//权限

            if (type == 0) {
                mActivity.startActivityForResult(intent, ZOOM_PHOTO);
            } else {
                mFragment.startActivityForResult(intent, ZOOM_PHOTO);
            }
        }
    }

    public interface CameraPhotoListener {
        void onFinish(File outputFile);
    }

    public void deleteAllCache() {
        FileUtils.deleteMyFile(filename);
        FileUtils.deleteMyFile(zoomName);
    }

}
