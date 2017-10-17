package yy.zpy.cc.memo.data

import yy.zpy.cc.greendaolibrary.bean.FolderBean
import kotlin.properties.Delegates

/**
 * Created by zpy on 2017/9/27.
 */
data class Folder(var count: Int = 0) {
    var check: Boolean = false
    var folderBean by Delegates.notNull<FolderBean>()

    init {
        folderBean = FolderBean()
    }
}