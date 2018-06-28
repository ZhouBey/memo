package yy.zpy.cc.memo.util

/**
 * Created by zpy on 2017/9/26.
 */
class Constant {
    companion object {
        const val REQUEST_CODE_CHOOSE = 101
        const val ALL_MEMO = "全部便签"
        const val REGEX_IMAGE_TAG = "<img\\sid=\"\\d*\"/>"
        const val REGEX_IMAGE_ID_TAG = "(id)=(\"|')(.*?)(\"|')"
        const val MEMO_PICTURES = "MemoCropPictures"

        const val MIN_KEYBOARD_HEIGHT_PX = 150



        const val RESULT_OK_CODE = 200


        const val SP_COVER_PATH = "cover_path" //string
        const val SP_MEMO_LOCK_PASSWORD = "memo_lock_password"   //string
        const val SP_IS_MEMO_LOCK = "is_memo_lock"   //boolean

        const val LOCK_MEMO_CONTENT = "*********************************************************"
    }
}