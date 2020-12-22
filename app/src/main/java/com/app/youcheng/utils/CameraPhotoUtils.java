package com.app.youcheng.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.app.youcheng.R;

import java.io.File;

public class CameraPhotoUtils {
    public static final int CAMERA_PHOTO = 100;
    public static final int SELECT_PHOTO = 101;

    private File imageCameraFile;
    private File imageSelectFile;
    private Uri imageUri;
    private CameraPhotoListener mListener;

    private Activity mActivity;
    private Fragment mFragment;
    private int type;
    private boolean mShouldCrop = false;//是否要裁剪（默认不裁剪）
    private String filename = "cachePhoto.jpg";
    private Context mContext;

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
                    if (mListener != null) {
                        mListener.onFinish(imageCameraFile);
                    }
                    break;
                case SELECT_PHOTO://图库
                    if (data != null) {
                        imageUri = data.getData();
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
                    break;
            }
        }
    }

    public interface CameraPhotoListener {
        void onFinish(File outputFile);
    }


    public void deleteAllCache() {
        FileUtils.deleteMyFile(filename);
    }

}
