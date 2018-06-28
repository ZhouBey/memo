package yy.zpy.cc.memo.activity

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_setting.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivityForResult
import yy.zpy.cc.memo.R
import yy.zpy.cc.memo.eventBus.AdapterNotifyDataEvent
import yy.zpy.cc.memo.interf.IBaseUI
import yy.zpy.cc.memo.util.Constant

/**
 * Created by zpy on 2018/6/27.
 */
class SettingActivity : BaseActivity(), IBaseUI {
    override fun getLayout(): Int = R.layout.activity_setting

    companion object {
        const val REQUEST_OPEN_LOCK_CODE = 101
        const val REQUEST_CLOSE_LOCK_CODE = 102
    }

    override fun initView() {
        switch_memo_lock.isClickable = false
    }

    override fun show() {
        val isLock = app.getSpValue(Constant.SP_IS_MEMO_LOCK, false)
        switch_memo_lock.isChecked = isLock
    }

    override fun viewListener() {
        ib_back.setOnClickListener {
            this.finish()
        }
        rl_memo_lock_setting.setOnClickListener {
            if (!switch_memo_lock.isChecked) {
                startActivityForResult<LockActivity>(REQUEST_OPEN_LOCK_CODE, LockActivity.TYPE to LockActivity.TYPE_ADD)
            } else {
                startActivityForResult<LockActivity>(REQUEST_CLOSE_LOCK_CODE, LockActivity.TYPE to LockActivity.TYPE_VERIFY)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        viewListener()
        show()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.anim_slide_no, R.anim.anim_slide_out_right)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (REQUEST_OPEN_LOCK_CODE == requestCode && Constant.RESULT_OK_CODE == resultCode) {
            switch_memo_lock.isChecked = true
            app.putSpValue(Constant.SP_IS_MEMO_LOCK, true)
            EventBus.getDefault().post(AdapterNotifyDataEvent())
            alert("开启成功，您可以在便签编辑界面为你的便签加锁了", "") {
                positiveButton("知道了") {

                }
            }.show()
        }
        if (REQUEST_CLOSE_LOCK_CODE == requestCode && Constant.RESULT_OK_CODE == resultCode) {
            switch_memo_lock.isChecked = false
            app.putSpValue(Constant.SP_IS_MEMO_LOCK, false)
            EventBus.getDefault().post(AdapterNotifyDataEvent())
        }
    }
}