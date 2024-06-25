package com.emergentes.misnotas.ui.splash_screen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.emergentes.misnotas.R
import com.emergentes.misnotas.ui.login.LoginActivity
import com.emergentes.misnotas.ui.main.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            // Vemos si hay una cuenta registrada
            val account = GoogleSignIn.getLastSignedInAccount(this@SplashScreenActivity)
            if (account != null) {
                val intent = Intent(
                    this@SplashScreenActivity,
                    MainActivity::class.java
                )
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
            } else {
                val intent = Intent(
                    this@SplashScreenActivity,
                    LoginActivity::class.java
                )
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
            }
        },2000)

    }
}