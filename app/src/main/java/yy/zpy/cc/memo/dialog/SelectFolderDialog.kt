package yy.zpy.cc.memo.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import kotlinx.android.synthetic.main.dialog_select_dialog.*
import org.jetbrains.anko.dip
import yy.zpy.cc.memo.R
import yy.zpy.cc.memo.adapter.SelectFolderAdapter


/**
 * Created by zpy on 2017/9/18.
 */
class SelectFolderDialog(context: Context, themeResId: Int, var data: List<MutableMap<String,Any>>, var onClickListener: OnClickListener) : Dialog(context, themeResId) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_select_dialog)
        val lp = window.attributes
        lp.gravity = Gravity.TOP or Gravity.END
        lp.width = context.dip(240)
        lp.x = context.dip(10)
        lp.y = context.dip(80)
    }

    override fun onStart() {
        super.onStart()
        rv_dialog_folder.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        rv_dialog_folder.layoutManager = linearLayoutManager
        rv_dialog_folder.adapter = SelectFolderAdapter(data) { position, type -> onClickListener.itemClick(position, type) }
        val dividerItemDecoration = DividerItemDecoration(context, linearLayoutManager.orientation)
        dividerItemDecoration.setDrawable(context.resources.getDrawable(R.drawable.divider_item_decoration, context.theme))
        rv_dialog_folder.addItemDecoration(dividerItemDecoration)
        tv_new_folder.setOnClickListener {
            onClickListener.newFolderClick()
        }
    }

    interface OnClickListener {
        fun itemClick(position: Int, type: Int)
        fun newFolderClick()
    }
}