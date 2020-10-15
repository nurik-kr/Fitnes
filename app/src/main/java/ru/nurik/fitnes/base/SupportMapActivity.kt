package ru.nurik.fitnes.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import ru.nurik.fitnes.R

abstract class SupportMapActivity : AppCompatActivity() {

    protected var mapView: MapView? = null
    protected var map: MapboxMap? = null

    abstract fun getResId(): Int // для вью
    abstract fun getMapViewId(): Int // для айди
    abstract fun onMapLoaded(mapBoxMap: MapboxMap, style: Style)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(applicationContext, getString(R.string.api_key))
        setContentView(getResId())
        mapView = findViewById(getMapViewId())
        mapView?.onCreate(savedInstanceState)

        mapView?.getMapAsync { mapBoxMap -> // загрузка карты
            map = mapBoxMap
            mapBoxMap.setStyle(Style.LIGHT) { style ->
                onMapLoaded(mapBoxMap, style)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }


}