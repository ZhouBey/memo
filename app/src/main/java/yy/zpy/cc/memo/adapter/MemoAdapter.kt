package yy.zpy.cc.memo.adapter

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.text.format.DateFormat
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.layout_memo_list_item.view.*
import yy.zpy.cc.greendaolibrary.bean.MemoBean
import yy.zpy.cc.memo.R
import java.util.*

/**
 * Created by zpy on 2017/10/10.
 */
class MemoAdapter(val data: List<MemoBean>, var block: (position: Int, type: Int) -> kotlin.Unit) : RecyclerView.Adapter<MemoAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = ViewHolder(parent?.inflate(R.layout.layout_memo_list_item)).itemClickListen { position, type ->
        block(position, type)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bind(position)
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) = with(itemView) {
            val item = data[position]
            val content = item.content
            val split = content.split("\n")
            val title = split[0]
            tv_item_title.text = title
            var body = content.substring(title.length, content.length)
            body = if (TextUtils.isEmpty(body)) title else body
            tv_item_content.text = body.trim()
            cvt_item_header.txt = content.substring(0, 1)
            cvt_item_header.color = resources.getColor(R.color.colorPrimary)
            cvt_item_header.invalidate()
            val time = Calendar.getInstance()
            time.timeInMillis = item.createTime
            tv_item_time.text = DateFormat.format("HH:mm", time).toString()
        }
    }
}