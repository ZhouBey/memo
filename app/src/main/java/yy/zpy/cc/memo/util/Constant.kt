package yy.zpy.cc.memo.util

/**
 * Created by zpy on 2017/9/26.
 */
class Constant {
    companion object {
        val REQUEST_CODE_CHOOSE = 101
        val RESULT_CODE_CHOOSE = 102
        val MEMO_CROPPED_IMAGE_NAME = "MemoCropImage"
        val SP_SELECT_FOLDER = "select_folder" //int
        val ALL_MEMO = "全部便签"
        val REGEX_IMAGE_TAG = "<img\\sid=\"\\d*\"/>"
        val REGEX_IMAGE_ID_TAG = "(id)=(\"|')(.*?)(\"|')"
        val MEMO_PICTURES = "MemoCropPictures"

        val SP_COVER_PATH = "cover_path"
    }
}