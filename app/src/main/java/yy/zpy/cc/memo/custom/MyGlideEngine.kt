package yy.zpy.cc.memo.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.zhihu.matisse.engine.ImageEngine

/**
 * Created by zpy on 2017/9/26.
 */
class MyGlideEngine : ImageEngine {
    override fun loadAnimatedGifThumbnail(context: Context?, resize: Int, placeholder: Drawable?, imageView: ImageView?, uri: Uri?) {
        Glide.with(context)
                .asBitmap()
                .load(uri)
                .apply(RequestOptions().placeholder(placeholder).override(resize, resize).centerCrop())
                .transition(BitmapTransitionOptions().crossFade(300))
                .into(imageView)
    }

    override fun loadImage(context: Context?, resizeX: Int, resizeY: Int, imageView: ImageView?, uri: Uri?) {
        Glide.with(context)
                .load(uri)
                .apply(RequestOptions().priority(Priority.HIGH).override(resizeX, resizeY).centerCrop())
                .transition(DrawableTransitionOptions().crossFade(300))
                .into(imageView)
    }

    override fun loadAnimatedGifImage(context: Context?, resizeX: Int, resizeY: Int, imageView: ImageView?, uri: Uri?) {
        Glide.with(context)
                .asGif()
                .load(uri)
                .apply(RequestOptions().priority(Priority.HIGH).override(resizeX, resizeY).centerCrop())
                .into(imageView)
    }

    override fun supportAnimatedGif(): Boolean = true

    override fun loadThumbnail(context: Context?, resize: Int, placeholder: Drawable?, imageView: ImageView?, uri: Uri?) {
        Glide.with(context)
                .asBitmap()
                .load(uri)
                .apply(RequestOptions().placeholder(placeholder).override(resize, resize).centerCrop())
                .transition(BitmapTransitionOptions().crossFade(300))
                .into(imageView)
    }

}