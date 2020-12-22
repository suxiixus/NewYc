package com.app.youcheng.utils;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class ImageBgUtils {

    public static void scaleImage(final Activity activity, final ImageView view, int drawableResId) {
        // 获取屏幕的高宽
        Point outSize = new Point();
        activity.getWindow().getWindowManager().getDefaultDisplay().getSize(outSize);

        // 解析将要被处理的图片
        Bitmap resourceBitmap = BitmapFactory.decodeResource(activity.getResources(), drawableResId);

        if (resourceBitmap == null) {
            return;
        }

        double phoneS = (double) outSize.y / (double) outSize.x;
        double imgS = (double) resourceBitmap.getHeight() / (double) resourceBitmap.getWidth();

        final Bitmap finallyBitmap;
        if (phoneS >= imgS) {
            // 使用图片的缩放比例计算将要放大的图片的宽度
            int bitmapScaledWidth = Math.round(resourceBitmap.getWidth() * outSize.y * 1.0f / resourceBitmap.getHeight());

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(resourceBitmap, bitmapScaledWidth, outSize.y, false);

            int offset = (scaledBitmap.getWidth() - outSize.x) / 2;

            finallyBitmap = Bitmap.createBitmap(scaledBitmap, offset, 0, scaledBitmap.getWidth() - 2 * offset, scaledBitmap.getHeight());

            if (!finallyBitmap.equals(scaledBitmap)) {
                scaledBitmap.recycle();
            }
        } else {
            // 使用图片的缩放比例计算将要放大的图片的高度
            int bitmapScaledHeight = Math.round(resourceBitmap.getHeight() * outSize.x * 1.0f / resourceBitmap.getWidth());

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(resourceBitmap, outSize.x, bitmapScaledHeight, false);

            int offset = (scaledBitmap.getHeight() - outSize.y) / 2;

            finallyBitmap = Bitmap.createBitmap(scaledBitmap, 0, offset, scaledBitmap.getWidth(), scaledBitmap.getHeight() - 2 * offset);

            if (!finallyBitmap.equals(scaledBitmap)) {
                scaledBitmap.recycle();
            }
        }

        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (!finallyBitmap.isRecycled()) {
                    view.setImageBitmap(finallyBitmap);
                }
                return true;
            }
        });
    }


}
