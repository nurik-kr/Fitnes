package ru.nurik.fitnes.ui.onBoard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_on_board.*
import ru.nurik.fitnes.R
import ru.nurik.fitnes.data.PreferenceHelper
import ru.nurik.fitnes.ui.main.MainActivity

class OnBoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_board)
        setupListeners()
    }

    private fun setupListeners() {
        bntNext.setOnClickListener {
            PreferenceHelper.setIsFirstLaunch() // сохранет при 2ом запуске сразу открывает маинАктивити
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}