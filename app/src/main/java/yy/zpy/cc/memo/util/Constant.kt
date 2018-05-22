package yy.zpy.cc.memo.util

/**
 * Created by zpy on 2017/9/26.
 */
class Constant {
    companion object {
        const val REQUEST_CODE_CHOOSE = 101
//        const val RESULT_CODE_CHOOSE = 102
//        const val MEMO_CROPPED_IMAGE_NAME = "MemoCropImage"
//        const val SP_SELECT_FOLDER = "select_folder" //int
        const val ALL_MEMO = "全部便签"
        const val REGEX_IMAGE_TAG = "<img\\sid=\"\\d*\"/>"
        const val REGEX_IMAGE_ID_TAG = "(id)=(\"|')(.*?)(\"|')"
        const val MEMO_PICTURES = "MemoCropPictures"

        const val SP_COVER_PATH = "cover_path"
        const val MIN_KEYBOARD_HEIGHT_PX = 150

    }
}