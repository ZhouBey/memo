package yy.zpy.cc.memo.data

import yy.zpy.cc.greendaolibrary.bean.MemoBean
import kotlin.properties.Delegates

/**
 * Created by zpy on 2017/10/17.
 */
data class Memo(var check: Boolean = false) {
    var memoBean by Delegates.notNull<MemoBean>()

    init {
        memoBean = MemoBean()
    }
}