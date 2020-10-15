package ru.nurik.fitnes.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import ru.nurik.fitnes.R
import ru.nurik.fitnes.data.PreferenceHelper
import ru.nurik.fitnes.ui.main.MainActivity
import ru.nurik.fitnes.ui.onBoard.OnBoardActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Thread.sleep(2000)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({ // замарозка на 3сек
            selectActivity()
        }, 3000)
    }

    private fun selectActivity() {
        if (PreferenceHelper.getIsFirstLaunch()) {
            startActivity(Intent(applicationContext, OnBoardActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}