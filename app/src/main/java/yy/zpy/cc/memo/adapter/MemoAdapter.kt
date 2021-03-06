package yy.zpy.cc.memo.adapter

import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.layout_memo_list_item.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.imageResource
import yy.zpy.cc.memo.R
import yy.zpy.cc.memo.activity.getDateDesc
import yy.zpy.cc.memo.data.Memo
import yy.zpy.cc.memo.logcat
import yy.zpy.cc.memo.util.Constant
import java.util.*
import java.util.regex.Pattern

/**
 * Created by zpy on 2017/10/10.
 */
class MemoAdapter(val data: List<Memo>, private var itemClickBlock: (position: Int, type: Int) -> Unit,
                  private var itemLongClickBlock: (position: Int, type: Int) -> Unit, private var needYear: Boolean = false) : RecyclerView.Adapter<MemoAdapter.ViewHolder>() {

    var isSetLock = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(parent.inflate(R.layout.layout_memo_list_item))
        viewHolder.itemClickListen { position, type ->
            itemClickBlock(position, type)
        }
        viewHolder.itemLongClickListener { position, type ->
            itemLongClickBlock(position, type)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    var hasSelect = false

    override fun getItemCount() = data.size

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) = with(itemView) {
            val item = data[position]
            val content = item.memoBean.content
            val split = content.split("\n")
            val title = split[0]
            tv_item_title.text = title.replace(Constant.REGEX_IMAGE_TAG.toRegex(), "image").trim()
            cvt_item_header.setText(content.substring(0, 1))
            cvt_item_header.color = resources.getColor(R.color.colorPrimary)
            val time = Calendar.getInstance()
            time.timeInMillis = item.memoBean.createTime
            val dateDesc = getDateDesc(time)
            if (TextUtils.isEmpty(dateDesc)) {
                if (needYear) {
                    tv_item_time.text = DateFormat.format("yyyy年MM月dd日 HH:mm", time).toString()
                } else {
                    tv_item_time.text = DateFormat.format("MM月dd日 HH:mm", time).toString()
                }
            } else {
                tv_item_time.text = String.format("%s%s", dateDesc, DateFormat.format("HH:mm", time).toString())
            }

            val isLock = data[position].memoBean.getIsLock()
            if (isLock && isSetLock) {
                tv_item_content.text = Constant.LOCK_MEMO_CONTENT
                iv_item_picture.visibility = View.VISIBLE
                iv_item_picture.imageResource = R.drawable.ic_lock
            } else {
                var body = content.substring(title.length, content.length)
                body = if (TextUtils.isEmpty(body)) title else body
                body.replace(Constant.REGEX_IMAGE_TAG.toRegex(), "<image>").trim()
                tv_item_content.text = body

                if (Pattern.compile(Constant.REGEX_IMAGE_TAG).matcher(content).find()) {
                    iv_item_picture.visibility = View.VISIBLE
                    iv_item_picture.imageResource = R.drawable.ic_memo_picture
                } else {
                    iv_item_picture.visibility = View.INVISIBLE
                }
            }
            if (!hasSelect) {
                val outValue = TypedValue()
                context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
                itemView.setBackgroundResource(outValue.resourceId)
            }
            if (item.check) {
                itemView.backgroundColor = resources.getColor(R.color.colorMemoSelected)
                cvt_item_header.setBitmap(BitmapFactory.decodeResource(resources, R.drawable.ic_memo_checked))
                cvt_item_header.color = resources.getColor(R.color.colorMemoCircleViewSelected)
            } else {
                val outValue = TypedValue()
                context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
                itemView.setBackgroundResource(outValue.resourceId)
            }
        }
    }
}