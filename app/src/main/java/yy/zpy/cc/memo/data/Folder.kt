package yy.zpy.cc.memo.data

import yy.zpy.cc.greendaolibrary.bean.FolderBean

/**
 * Created by zpy on 2017/9/27.
 */
data class Folder(var count: Int = 0) : FolderBean() {
    var check: Boolean = false
}