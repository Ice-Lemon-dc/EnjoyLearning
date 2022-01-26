package com.dc.enjoylearning.recyclerview.commonadapter;

import android.widget.ImageView;

/**
 * 可能使用不同的图片加载（Picasso/Glide/Fresco），采用解耦的方式把实现方式传出去
 *
 * @author Lemon
 */
public interface HolderImageLoader {

    /**
     * 加载图片
     *
     * @param imageView ImageView
     * @param path 路径
     */
    void loadImage(ImageView imageView, String path);
}
