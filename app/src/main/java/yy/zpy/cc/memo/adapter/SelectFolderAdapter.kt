package yy.zpy.cc.memo.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_select_folder_item.view.*
import yy.zpy.cc.memo.R


/**
 * Created by zpy on 2017/9/18.
 */
class SelectFolderAdapter(var data: List<String>) : RecyclerView.Adapter<SelectFolderAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bind(data[position])
    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = ViewHolder(parent?.inflate(R.layout.dialog_select_folder_item))

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bind(name: String) = with(itemView) {
            tv_select_folder_name.text = name
        }
    }
}

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}