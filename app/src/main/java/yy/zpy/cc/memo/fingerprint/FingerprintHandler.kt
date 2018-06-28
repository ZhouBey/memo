package yy.zpy.cc.memo.fingerprint

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import yy.zpy.cc.memo.logcat

/**
 * Created by zpy on 2018/6/5.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
class FingerprintHandler(private val context: Context) : FingerprintManager.AuthenticationCallback() {
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
    }

    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) {
        logcat("Authentication help\n$helpString")
    }

    override fun onAuthenticationFailed() {
        logcat("Authentication failed.")
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
        logcat("Authentication succeeded.")
    }
}
