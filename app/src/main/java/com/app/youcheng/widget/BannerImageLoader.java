package com.app.youcheng.widget;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;

public class BannerImageLoader extends ImageLoader {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
//        RequestOptions myOptions = new RequestOptions().transform(new GlideRoundTransform(context, 20));
        Glide.with(context).load(path).into(imageView);
    }
}
