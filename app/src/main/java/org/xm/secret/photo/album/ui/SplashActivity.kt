package org.xm.secret.photo.album.ui

import android.os.Bundle
import com.allever.lib.common.app.BaseActivity
import com.allever.lib.common.util.ActivityCollector
import com.allever.lib.notchcompat.NotchCompat
import org.xm.secret.photo.album.R
import org.xm.secret.photo.album.function.password.PasswordConfig

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        NotchCompat.adaptNotchWithFullScreen(window)

        mHandler.postDelayed({
            PasswordConfig.secretCheckPass = false
            ActivityCollector.startActivity(this, MainActivity::class.java)
            finish()
        }, 2000)
    }
}