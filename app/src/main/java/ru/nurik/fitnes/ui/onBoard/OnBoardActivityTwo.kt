package ru.nurik.fitnes.ui.onBoard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.onboard_page.*
import ru.nurik.fitnes.R
import ru.nurik.fitnes.ui.main.MainActivity

class OnBoardActivityTwo : AppCompatActivity() {
    private val list = arrayListOf<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboard_page)
        setupViewpager()
        setupListener()
    }

    private fun setupListener() {
        OnviewPage.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (checkToPage(position)) {
                    Btngo.text = "Начать"
                } else {
                    Btngo.text = "Продолжить"
                }
            }
        })
        Btngo.setOnClickListener {
            if (checkToPage(OnviewPage.currentItem)) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                OnviewPage.currentItem += 1
            }
        }
    }

    private fun checkToPage(position: Int) = position == list.size - 1

    private fun setupViewpager() {
        val adapter = OnBoardAdapter(supportFragmentManager)
        OnviewPage.adapter = adapter
        list.add(OnBoardFragment.getInstance(DataOnBoard(R.drawable.fintes, "Добро пожаловать в наш ", "Фитнес Тренеровку")))
        list.add(OnBoardFragment.getInstance(DataOnBoard(R.drawable.fitnes, "В нашем приложении вы можете каждый день рассчитывать", "Свои шаги")))
        list.add(OnBoardFragment.getInstance(DataOnBoard(R.drawable.day, "Зарегистрируйтесь и пользуйтесь на здоровье", "С Уважением MD2001")))
        adapter.update(list)
        OnTabLT.setupWithViewPager(OnviewPage)
    }
}
