package ru.nurik.fitnes.ui

import android.os.Bundle
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.android.synthetic.main.activity_main.*
import ru.nurik.fitnes.MyFireBase.NotificationUtils
import ru.nurik.fitnes.R

class MainActivity : BaseMapActivity() {
    override fun getResId() = R.layout.activity_main
    override fun getMapViewId() = R.id.mapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        fab.setOnClickListener {
            map?.setStyle(Style.DARK)

//            NotificationUtils.createNotification( для уведомления по кнопке
//                applicationContext,
//                "234234234234",
//                "11111111111"
//            )
        }
    }
}

