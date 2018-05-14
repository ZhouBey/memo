package yy.zpy.cc.memo.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.activity_memo_preview.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import yy.zpy.cc.memo.R
import yy.zpy.cc.memo.interf.IBaseUI
import yy.zpy.cc.memo.util.Constant
import java.io.File
import java.util.regex.Pattern

/**
 * Created by zpy on 2018/5/11.
 */
class PreviewMemoActivity : BaseActivity(), IBaseUI {
    override fun getLayout(): Int = R.layout.activity_memo_preview
    var memoContent: String? = null
    override fun initView() {
        memoContent = intent.getStringExtra("memoContent")

    }

    override fun show() {
        memoContent?.let {
            showMemoInfo(it)
        }
    }

    override fun viewListener() {
        ib_back.setOnClickListener {
            this@PreviewMemoActivity.finish()
            overridePendingTransition(R.anim.anim_slide_no, R.anim.anim_slide_out_right)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        viewListener()
        show()
    }

    private fun showMemoInfo(content: String) {
        val contentTagList = cutStringByImgTag(content)
        var isFirst = true
        ll_preview_memo_content.addView(View(this@PreviewMemoActivity))
        contentTagList.forEach {
            if (Pattern.compile(Constant.REGEX_IMAGE_TAG).matcher(it).find()) {
                val matcher = Pattern.compile(Constant.REGEX_IMAGE_ID_TAG).matcher(it)
                while (matcher.find()) {
                    val imageView = ImageView(this@PreviewMemoActivity)
                    val imageID = matcher.group(3)
                    imageView.setTag(R.id.tag_image_view_uri, imageID)
                    Glide.with(this@PreviewMemoActivity).load(File(Environment.getExternalStorageDirectory().toString() + "/" + Constant.MEMO_PICTURES + "/" + imageID + ".png"))
                            .apply(RequestOptions().error(R.drawable.img_error)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                            ).into(object : SimpleTarget<Drawable>() {
                                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                    adjustImageView(this@PreviewMemoActivity, imageView, resource)
                                }

                                override fun onLoadFailed(errorDrawable: Drawable?) {
                                    super.onLoadFailed(errorDrawable)
                                    imageView.layoutParams.width = matchParent
                                    imageView.layoutParams.height = dip(130)
                                    imageView.setImageDrawable(errorDrawable)
                                }
                            })
                    ll_preview_memo_content.addView(imageView)
                    isFirst = false
                }
            } else {
                val textView = TextView(this@PreviewMemoActivity)
                with(textView) {
                    if (isFirst) {
                        textAddTextChangeListener(this)
                        isFirst = false
                    }
                    setLineSpacing(0f, 1.1f)
                    textColor = R.color.colorFont
                    textSize = 14f
                    text = it
                }
                ll_preview_memo_content.addView(textView)
            }
        }
    }

    private fun textAddTextChangeListener(textView: TextView) {
        textView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString()
                val split = text?.split("\n")
                val firstLine = split?.get(0)
                if (firstLine != null) {
                    s.setSpan(ForegroundColorSpan(resources.getColor(R.color.colorFontDark)),
                            0,
                            firstLine.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    s.setSpan(AbsoluteSizeSpan(dip(17)),
                            0,
                            firstLine.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }
}