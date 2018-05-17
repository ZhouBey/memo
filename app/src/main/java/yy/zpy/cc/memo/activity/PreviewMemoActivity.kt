package yy.zpy.cc.memo.activity

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.activity_memo_preview.*
import org.jetbrains.anko.*
import yy.zpy.cc.greendaolibrary.bean.MemoBean
import yy.zpy.cc.memo.R
import yy.zpy.cc.memo.dialog.PreviewMemoSettingDialog
import yy.zpy.cc.memo.interf.IBaseUI
import yy.zpy.cc.memo.util.Constant
import java.io.File
import java.util.regex.Pattern
import kotlin.properties.Delegates

/**
 * Created by zpy on 2018/5/11.
 */
class PreviewMemoActivity : BaseActivity(), IBaseUI {
    override fun getLayout(): Int = R.layout.activity_memo_preview

    companion object {
        const val DEFAULT_FONT_SIZE = 14F
        const val DEFAULT_LINE_HEIGHT = 10F
        const val DEFAULT_GRAVITY = Gravity.START
        const val DEFAULT_FONT_COLOR = "#3A3F45"
        const val DEFAULT_BACKGROUND_COLOR = "#FFFFFF"
    }

    private var memoBeanId: Long? = null
    private var memoBean: MemoBean? = null
    private var previewMemoSettingDialog: PreviewMemoSettingDialog by Delegates.notNull()
    override fun initView() {
        memoBeanId = intent.getLongExtra("memoBeanID", -1L)
        memoBean = app.memoBeanDao?.load(memoBeanId)
        initPreviewMemoSettingDialog()
    }

    override fun show() {
        memoBean?.let {
            showMemoInfo(it.content)
        }
    }

    private fun initPreviewMemoSettingDialog() {
        previewMemoSettingDialog = PreviewMemoSettingDialog(this@PreviewMemoActivity, R.style.PreviewMemoSettingDialog)
        previewMemoSettingDialog.memoSettingListener = object : PreviewMemoSettingDialog.IMemoSettingListener {
            override fun onFontSizeAdd() {
                val textSize = memoBean?.fontSize ?: DEFAULT_FONT_SIZE
                if (textSize < 18) {
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

            override fun onLineHeightAdd() {
                val lineHeight = memoBean?.lineHeight ?: DEFAULT_LINE_HEIGHT
                if (lineHeight < 20) {
                    val newLineHeight = lineHeight.plus(1F)
                    memoBean?.lineHeight = newLineHeight
                    setMemoLineHeight(newLineHeight)
                }
            }

            override fun onLineHeightReduce() {
                val lineHeight = memoBean?.lineHeight ?: DEFAULT_LINE_HEIGHT
                if (lineHeight > 5) {
                    val newLineHeight = lineHeight.minus(1F)
                    memoBean?.lineHeight = newLineHeight
                    setMemoLineHeight(newLineHeight)
                }
            }

            override fun onAlignLeft() {
                memoBean?.gravity = Gravity.START
                setMemoAlign(Gravity.START)
            }

            override fun onAlignCenter() {
                memoBean?.gravity = Gravity.CENTER_HORIZONTAL
                setMemoAlign(Gravity.CENTER_HORIZONTAL)
            }

            override fun onAlignRight() {
                memoBean?.gravity = Gravity.END
                setMemoAlign(Gravity.END)
            }

            override fun onDrawColor(color: String, type: Int) {
                //0代表文字，1代表背景
                if (0 == type) {
                    setMemoFontColor(color)
                    memoBean?.fontColor = color
                }
                if (1 == type) {
                    setMemoBackgroundColor(color)
                    memoBean?.backgroundColor = color
                }
            }
        }
    }

    private fun setMemoFontColor(color: String) {
        val firstChild = ll_preview_memo_content.getChildAt(0)
        if (firstChild is TextView) {
            ll_preview_memo_content.removeView(firstChild)
            val textView = TextView(this@PreviewMemoActivity)
            with(textView) {
                textAddTextChangeListener(this, (memoBean?.fontSize ?: DEFAULT_FONT_SIZE).plus(3F), color)
                setLineSpacing(firstChild.lineSpacingExtra, 1F)
                gravity = firstChild.gravity
                text = firstChild.text
            }
            ll_preview_memo_content.addView(textView, 0)
        }
        ll_preview_memo_content.forEachChildWithIndex { _, view ->
            if (view is TextView) {
                view.textColor = Color.parseColor(color)
                view.textSize = memoBean?.fontSize ?: DEFAULT_FONT_SIZE
            }
        }
        tv_preview_memo_sign.textColor = Color.parseColor(color)
    }

    private fun setMemoBackgroundColor(color: String) {
        rl_root_preview_memo.backgroundColor = Color.parseColor(color)
    }

    private fun setMemoFontSize(textSize: Float) {
        val firstChild = ll_preview_memo_content.getChildAt(0)
        if (firstChild is TextView) {
            ll_preview_memo_content.removeView(firstChild)
            val textView = TextView(this@PreviewMemoActivity)
            with(textView) {
                textAddTextChangeListener(this, textSize.plus(3F), memoBean?.fontColor
                        ?: DEFAULT_FONT_COLOR)
                setLineSpacing(firstChild.lineSpacingExtra, 1F)
                gravity = firstChild.gravity
                textColor = R.color.colorFont
                text = firstChild.text
            }
            ll_preview_memo_content.addView(textView, 0)
        }
        ll_preview_memo_content.forEachChildWithIndex { _, view ->
            if (view is TextView) {
                view.textSize = textSize
                view.textColor = Color.parseColor(memoBean?.fontColor
                        ?: DEFAULT_FONT_COLOR)
            }
        }
    }

    private fun setMemoLineHeight(lineHeight: Float) {
        ll_preview_memo_content.forEachChildWithIndex { _, view ->
            if (view is TextView) {
                view.setLineSpacing(lineHeight, 1F)
            }
        }
    }

    private fun setMemoAlign(align: Int) {
        ll_preview_memo_content.forEachChildWithIndex { _, view ->
            if (view is TextView) {
                view.gravity = align
            }
        }
    }

    override fun viewListener() {
        ib_back.setOnClickListener {
            app.memoBeanDao?.update(memoBean)
            this@PreviewMemoActivity.finish()
            overridePendingTransition(R.anim.anim_slide_no, R.anim.anim_slide_out_right)
        }
        rl_root_preview_memo.setOnClickListener {
            previewMemoSettingDialog.show()
        }
    }

    override fun onBackPressed() {
        app.memoBeanDao?.update(memoBean)
        super.onBackPressed()
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
                val lineHeight = if (memoBean?.lineHeight ?: 0F == 0F) {
                    DEFAULT_LINE_HEIGHT
                } else {
                    memoBean?.lineHeight ?: DEFAULT_LINE_HEIGHT
                }
                val gravity = if (memoBean?.gravity ?: DEFAULT_GRAVITY == 0) {
                    DEFAULT_GRAVITY
                } else {
                    memoBean?.gravity ?: DEFAULT_GRAVITY
                }
                val fontColor = if (memoBean?.fontColor ?: DEFAULT_FONT_COLOR == "") {
                    DEFAULT_FONT_COLOR
                } else {
                    memoBean?.fontColor ?: DEFAULT_FONT_COLOR
                }
                with(textView) {
                    if (isFirst) {
                        textAddTextChangeListener(this, fontSize.plus(3F), memoBean?.fontColor
                                ?: DEFAULT_FONT_COLOR)
                        isFirst = false
                    }
                    setLineSpacing(lineHeight, 1F)
                    setGravity(gravity)
                    textColor = Color.parseColor(fontColor)
                    textSize = fontSize
                    text = it
                }
                memoBean?.fontSize = fontSize
                memoBean?.lineHeight = lineHeight
                ll_preview_memo_content.addView(textView)
                tv_preview_memo_sign.setTextColor(Color.parseColor(fontColor))
            }
        }
        val backgroundColorForMemo = if (memoBean?.backgroundColor ?: DEFAULT_BACKGROUND_COLOR == "") {
            DEFAULT_BACKGROUND_COLOR
        } else {
            memoBean?.backgroundColor ?: DEFAULT_BACKGROUND_COLOR
        }
        rl_root_preview_memo.backgroundColor = Color.parseColor(backgroundColorForMemo)
    }

    private fun textAddTextChangeListener(textView: TextView, textSize: Float, color: String) {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString()
                val split = text?.split("\n")
                val firstLine = split?.get(0)
                if (firstLine != null) {
                    s.setSpan(ForegroundColorSpan(Color.parseColor(color)),
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