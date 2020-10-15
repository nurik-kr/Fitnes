package ru.nurik.fitnes.ui.main

import android.content.Intent
import android.os.Bundle
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.android.synthetic.main.activity_main.*
import ru.nurik.fitnes.R
import ru.nurik.fitnes.base.BaseMapActivity
import ru.nurik.fitnes.ui.MyForegroundService

class MainActivity : BaseMapActivity() {
    override fun getResId() = R.layout.activity_main
    override fun getMapViewId() = R.id.mapView

    private val intenT by lazy { // ленивая инициализация, метод сработает только когда его вызовут
        val intent = Intent(this, MyForegroundService::class.java)
        intent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        fab.setOnClickListener {
            map?.setStyle(Style.DARK)
//            startForegroundService()
        }
        btnStart.setOnClickListener {
            startForegroundService()
        }
        btnStop.setOnClickListener {
            stopService(intenT)
        }
    }


    fun startForegroundService() {
        startService(intenT)
    }

    override fun onDestroy() {
        stopService(intenT)
        super.onDestroy()
    }
}

