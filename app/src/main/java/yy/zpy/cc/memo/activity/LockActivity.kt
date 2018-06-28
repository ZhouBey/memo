package yy.zpy.cc.memo.activity

import android.os.Bundle
import android.view.View
import com.andrognito.pinlockview.PinLockListener
import kotlinx.android.synthetic.main.activity_lock.*
import qiu.niorgai.StatusBarCompat
import yy.zpy.cc.memo.R
import yy.zpy.cc.memo.interf.IBaseUI
import yy.zpy.cc.memo.util.Constant

/**
 * Created by zpy on 2018/6/27.
 */

class LockActivity : BaseActivity(), IBaseUI {
    override fun getLayout(): Int = R.layout.activity_lock
    private var type = TYPE_VERIFY
    private var isAgain = true
    private var password = ""
    private var repeatPassword = ""

    companion object {
        const val TYPE_ADD = 1
        const val TYPE_VERIFY = 2
        const val TYPE = "type"
    }

    override fun initView() {
        StatusBarCompat.setStatusBarColor(this, resources.getColor(R.color.colorPrimary))
        plv_memo.attachIndicatorDots(indicator_dots)
        type = intent.getIntExtra(TYPE, TYPE_VERIFY)
        if (TYPE_ADD == type) {
            //添加密码
            iv_fingerprint.visibility = View.GONE
            tv_tip_desc.text = "请输入6位密码"
        } else {
            //验证密码
            iv_fingerprint.visibility = View.VISIBLE
        }
    }

    override fun show() {
    }

    override fun viewListener() {
        plv_memo.setPinLockListener(object : PinLockListener {
            override fun onEmpty() {
            }

            override fun onComplete(pin: String) {
                if (TYPE_ADD == type) {
                    if (isAgain) {
                        tv_tip_desc.text = "再次输入密码"
                        isAgain = false
                        password = pin
                        plv_memo.resetPinLockView()
                    } else {
                        repeatPassword = pin
                        if (password == repeatPassword) {
                            tv_tip_desc.text = "设置成功"
                            app.putSpValue(Constant.SP_MEMO_LOCK_PASSWORD, password)
                            setResult(Constant.RESULT_OK_CODE)
                            this@LockActivity.finish()
                        } else{
                            tv_tip_desc.text = "两次输入不一致"
                        }
                    }
                } else {
                    val password = app.getSpValue(Constant.SP_MEMO_LOCK_PASSWORD, "")
                    if (password == pin) {
                        tv_tip_desc.text = "验证成功"
                        setResult(Constant.RESULT_OK_CODE)
                        this@LockActivity.finish()
                    } else {
                        tv_tip_desc.text = "密码错误，请重试"
                        plv_memo.resetPinLockView()
                    }
                }
            }

            override fun onPinChange(pinLength: Int, intermediatePin: String?) {

            }
        })
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
}