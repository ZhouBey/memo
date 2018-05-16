package yy.zpy.cc.memo.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.activity_memo_preview.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.forEachChildWithIndex
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import yy.zpy.cc.greendaolibrary.bean.MemoBean
import yy.zpy.cc.memo.R
import yy.zpy.cc.memo.dialog.PreviewMemoSettingDialog
import yy.zpy.cc.memo.interf.IBaseUI
import yy.zpy.cc.memo.logcat
import yy.zpy.cc.memo.util.Constant
import java.io.File
import java.util.regex.Pattern
import kotlin.properties.Delegates

/**
 * Created by zpy on 2018/5/11.
 */
class PreviewMemoActivity : BaseActivity(), IBaseUI {
    companion object {
        const val DEFAULT_FONT_SIZE = 14F
    }

    override fun getLayout(): Int = R.layout.activity_memo_preview
    private var memoBeanId: Long? = null
    private var memoBean: MemoBean? = null
    private var previewMemoSettingDialog: PreviewMemoSettingDialog by Delegates.notNull()
    private var textWatcher: TextWatcher? = null
    override fun initView() {
        memoBeanId = intent.getLongExtra("memoBeanID", -1L)
        memoBean = app.memoBeanDao?.load(memoBeanId)
        initPreviewMemoSettingDialog()
    }

    override fun show() {
        memoBean?.let {
            logcat(it.content)
            showMemoInfo(it.content)
        }
    }

    private fun initPreviewMemoSettingDialog() {
        previewMemoSettingDialog = PreviewMemoSettingDialog(this@PreviewMemoActivity, R.style.WhiteDialog)
        previewMemoSettingDialog.memoFontSizeSettingListener = object : PreviewMemoSettingDialog.IMemoFontSizeSettingListener {
            override fun onFontSizeAdd() {
                val textSize = memoBean?.fontSize ?: DEFAULT_FONT_SIZE
                if (textSize < 20) {
                    val newTextSize = textSize.plus(1F)
                    memoBean?.fontSize = newTextSize
                    setMemoFontSize(newTextSize)
                }
            }

            override fun onFontSizeReduce() {
                val textSize = memoBean?.fontSize ?: DEFAULT_FONT_SIZE
                if (textSize > 10) {
                    val newTextSize = textSize.minus(1F)
                    memoBean?.fontSize = newTextSize
                    setMemoFontSize(newTextSize)
                }
            }
        }
    }

    private fun setMemoFontSize(textSize: Float) {
        val firstChild = ll_preview_memo_content.getChildAt(0)
        if (firstChild is TextView) {
            ll_preview_memo_content.removeView(firstChild)
            val textView = TextView(this@PreviewMemoActivity)
            with(textView) {
                textAddTextChangeListener(this, textSize.plus(3F))
                setLineSpacing(0f, 1.1f)
                textColor = R.color.colorFont
                text = firstChild.text
            }
            ll_preview_memo_content.addView(textView, 0)
        }
        ll_preview_memo_content.forEachChildWithIndex { _, view ->
            if (view is TextView) {
                view.textSize = textSize
            }
        }
    }

    override fun viewListener() {
        ib_back.setOnClickListener {
            this@PreviewMemoActivity.finish()
            overridePendingTransition(R.anim.anim_slide_no, R.anim.anim_slide_out_right)
        }
        rl_root_preview_memo.setOnClickListener {
            previewMemoSettingDialog.show()
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
//        ll_preview_memo_content.addView(View(this@PreviewMemoActivity))
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
                val fontSize = if (memoBean?.fontSize ?: 0F == 0F) {
                    DEFAULT_FONT_SIZE
                } else {
                    memoBean?.fontSize ?: DEFAULT_FONT_SIZE
                }
                with(textView) {
                    if (isFirst) {
                        textAddTextChangeListener(this, fontSize.plus(3F))
                        isFirst = false
                    }
                    setLineSpacing(0f, 1.1f)
                    textColor = R.color.colorFont
                    logcat(fontSize.toString())
                    memoBean?.fontSize = fontSize
                    textSize = fontSize
                    text = it
                }
                ll_preview_memo_content.addView(textView)
            }
        }
    }

    private fun textAddTextChangeListener(textView: TextView, textSize: Float) {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString()
                val split = text?.split("\n")
                val firstLine = split?.get(0)
                if (firstLine != null) {
                    logcat("fuck")
                    s.setSpan(ForegroundColorSpan(resources.getColor(R.color.colorFontDark)),
                            0,
                            firstLine.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    s.setSpan(AbsoluteSizeSpan(dip(textSize)),
                            0,
                            firstLine.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }
        textView.addTextChangedListener(textWatcher)
    }
}