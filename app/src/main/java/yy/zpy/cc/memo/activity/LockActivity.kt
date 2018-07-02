package yy.zpy.cc.memo.activity

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.Toast
import com.andrognito.pinlockview.PinLockListener
import kotlinx.android.synthetic.main.activity_lock.*
import org.jetbrains.anko.textColor
import qiu.niorgai.StatusBarCompat
import yy.zpy.cc.memo.R
import yy.zpy.cc.memo.interf.IBaseUI
import yy.zpy.cc.memo.logcat
import yy.zpy.cc.memo.util.Constant
import yy.zpy.cc.memo.util.MD5
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

/**
 * Created by zpy on 2018/6/27.
 */

open class LockActivity : BaseActivity(), IBaseUI {
    override fun getLayout(): Int = R.layout.activity_lock
    private var type = TYPE_VERIFY
    private var isAgain = true
    private var password = ""
    private var repeatPassword = ""
    private var fingerprintManager: FingerprintManager? = null
    private var cryptoObject: FingerprintManager.CryptoObject? = null

    companion object {
        const val TYPE_ADD = 1
        const val TYPE_VERIFY = 2
        const val TYPE = "type"
        const val KEY_NAME = "KEY_STORE_ALIAS"
    }

    override fun initView() {
        StatusBarCompat.setStatusBarColor(this, resources.getColor(R.color.colorPrimary))
        plv_memo.attachIndicatorDots(indicator_dots)
        indicator_dots.pinLength = 6
        type = intent.getIntExtra(TYPE, TYPE_VERIFY)
        if (TYPE_ADD == type) {
            //添加密码
            iv_fingerprint.visibility = View.GONE
            tv_tip_desc.text = "请输入6位密码"
        } else {
            //验证密码
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                iv_fingerprint.visibility = View.VISIBLE
                val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                fingerprintManager = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
                if (!keyguardManager.isKeyguardSecure) {
                    Toast.makeText(this, "Lock screen security not enabled in Settings", Toast.LENGTH_LONG).show()
                    finish()
                }

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Fingerprint authentication permission not enabled", Toast.LENGTH_LONG).show()
                    finish()
                }

                if (fingerprintManager?.hasEnrolledFingerprints() != true) {
                    Toast.makeText(this, "Register at least one fingerprint in Settings", Toast.LENGTH_LONG).show()
                    finish()
                }

                generateKey()
                if (cipherInit()) {
                    cryptoObject = FingerprintManager.CryptoObject(cipher)
                    if (null != fingerprintManager && null != cryptoObject) {
                        val helper = FingerprintHandler(this@LockActivity)
                        helper.startAuth(fingerprintManager!!, cryptoObject!!)
                    }
                } else {
                    Toast.makeText(this@LockActivity, "cipherInitFalse", Toast.LENGTH_SHORT).show()
                    iv_fingerprint.visibility = View.GONE
                }
            } else {
                iv_fingerprint.visibility = View.GONE

            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    inner class FingerprintHandler(private val context: Context) : FingerprintManager.AuthenticationCallback() {
        private var cancellationSignal: CancellationSignal? = null

        fun startAuth(manager: FingerprintManager, cryptoObject: FingerprintManager.CryptoObject) {
            cancellationSignal = CancellationSignal()

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            manager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
        }

        override fun onAuthenticationError(errMsgId: Int, errString: CharSequence) {
            logcat("Authentication error\n$errString")
            tv_tip_desc.text = errString
        }

        override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) {
            logcat("Authentication help\n$helpString")
            tv_tip_desc.text = helpString
        }

        override fun onAuthenticationFailed() {
            logcat("Authentication failed.")
            tv_tip_desc.text = "指纹识别失败"
            tv_tip_desc.textColor = resources.getColor(R.color.colorRedMsg)
        }

        override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
            tv_tip_desc.text = "验证成功"
            tv_tip_desc.textColor = resources.getColor(R.color.colorGreen)
            setResult(Constant.RESULT_OK_CODE)
            this@LockActivity.finish()

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
                            tv_tip_desc.textColor = resources.getColor(R.color.colorGreen)
                            app.putSpValue(Constant.SP_MEMO_LOCK_PASSWORD, MD5.md5(password))
                            setResult(Constant.RESULT_OK_CODE)
                            this@LockActivity.finish()
                        } else {
                            tv_tip_desc.text = "两次输入不一致"
                            tv_tip_desc.textColor = resources.getColor(R.color.colorRedMsg)
                        }
                    }
                } else {
                    val password = app.getSpValue(Constant.SP_MEMO_LOCK_PASSWORD, "")
                    if (password == MD5.md5(pin)) {
                        tv_tip_desc.text = "验证成功"
                        tv_tip_desc.textColor = resources.getColor(R.color.colorGreen)
                        setResult(Constant.RESULT_OK_CODE)
                        this@LockActivity.finish()
                    } else {
                        tv_tip_desc.text = "密码错误，请重试"
                        tv_tip_desc.textColor = resources.getColor(R.color.colorRedMsg)
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

    private var keyStore: KeyStore? = null
    private var cipher: Cipher? = null

    @RequiresApi(Build.VERSION_CODES.M)
    private fun generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val keyGenerator: KeyGenerator
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            keyStore?.load(null)
            keyGenerator.init(KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build())
            keyGenerator.generateKey()
        } catch (e: NoSuchAlgorithmException) {
//            throw RuntimeException("Failed to get KeyGenerator instance", e)
        } catch (e: NoSuchProviderException) {
//            throw RuntimeException("Failed to get KeyGenerator instance", e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun cipherInit(): Boolean {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7)
        } catch (e: NoSuchAlgorithmException) {
//            throw RuntimeException("Failed to get Cipher", e)
        } catch (e: NoSuchPaddingException) {
//            throw RuntimeException("Failed to get Cipher", e)
        }

        try {
            keyStore?.load(null)
            keyStore?.getKey(KEY_NAME, null)?.let {
                val key = it as SecretKey
                cipher?.init(Cipher.ENCRYPT_MODE, key)
            }
            return true
        } catch (e: KeyPermanentlyInvalidatedException) {
            return false
        } catch (e: KeyStoreException) {
//            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: CertificateException) {
//            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: UnrecoverableKeyException) {
//            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: IOException) {
//            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: NoSuchAlgorithmException) {
//            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: InvalidKeyException) {
//            throw RuntimeException("Failed to init Cipher", e)
        }
        return false
    }
}