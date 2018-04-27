package yy.zpy.cc.memo.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_select_folder_item.view.*
import org.jetbrains.anko.imageResource
import yy.zpy.cc.memo.R
import yy.zpy.cc.memo.data.Folder


/**
 * Created by zpy on 2017/9/18.
 */
class FolderAdapter(var data: List<Folder>,
                    var isSelect: Boolean,
                    private var itemClickBlock: (position: Int, type: Int) -> kotlin.Unit,
                    private var itemLongClickBlock: (position: Int, type: Int) -> Unit) : RecyclerView.Adapter<FolderAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(parent.inflate(R.layout.dialog_select_folder_item))
        viewHolder.itemClickListen { position, type ->
            itemClickBlock(position, type)
        }
        viewHolder.itemLongClickListener { position, type ->
            itemLongClickBlock(position, type)
        }
        return viewHolder
    }

    override fun getItemCount() = data.size

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) = with(itemView) {
            val item = data[position]
            val name = item.folderBean.name
            val isCheck = item.check
            tv_select_folder_name.text = name
            iv_folder_icon.imageResource = (if (position == 0) R.drawable.ic_all_folder else R.drawable.ic_single_folder)
            if (isSelect) {
                iv_select_folder_check.visibility = View.VISIBLE
                tv_folder_count.visibility = View.GONE
                if (isCheck) {
                    iv_select_folder_check.setImageDrawable(resources.getDrawable(R.drawable.ic_select_folder, context.theme))
                } else {
                    iv_select_folder_check.setImageDrawable(null)
                }
            } else {
                iv_select_folder_check.visibility = View.GONE
                tv_folder_count.visibility = View.VISIBLE
                tv_folder_count.text = item.count.toString()
            }
        }
    }
}

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun <T : RecyclerView.ViewHolder> T.itemClickListen(event: (position: Int, type: Int) -> Unit): T {
    itemView.setOnClickListener {
        event.invoke(adapterPosition, itemViewType)
    }
    return this
}

fun <T : RecyclerView.ViewHolder> T.itemLongClickListener(event: (position: Int, type: Int) -> Unit): T {
    itemView.setOnLongClickListener {
        event.invoke(adapterPosition, itemViewType)
        true
    }
    return this
}