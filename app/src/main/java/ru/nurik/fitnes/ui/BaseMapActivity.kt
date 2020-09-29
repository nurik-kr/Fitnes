package ru.nurik.fitnes.ui

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import ru.nurik.fitnes.R
import ru.nurik.fitnes.Utils.PermissionUtils

abstract class BaseMapActivity : AppCompatActivity() {

    abstract fun getResId(): Int // для вью
    abstract fun getMapViewId(): Int // для айди

    private var mapView: MapView? = null

    protected var map: MapboxMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(applicationContext, getString(R.string.api_key))
        setContentView(getResId())
        mapView = findViewById(getMapViewId())
        mapView?.onCreate(savedInstanceState)

        mapView?.getMapAsync { // загрузка карты
            map = it
            it.setStyle(Style.LIGHT) {
                if (PermissionUtils.requestLocationPermission(this)) //проверка на гео
                    showUserLocation()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionUtils.LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showUserLocation()
            }
        }
    }

    @SuppressLint("MissingPermission", "Range")
    private fun showUserLocation() {
        map?.style?.let {
            val locationComponent = map?.locationComponent
            locationComponent?.activateLocationComponent(
                LocationComponentActivationOptions.builder(applicationContext, it)
                    .build()
            )

            locationComponent?.isLocationComponentEnabled = true
            locationComponent?.cameraMode = CameraMode.TRACKING

            locationComponent?.renderMode = RenderMode.COMPASS

            val location = locationComponent?.lastKnownLocation

            val latLng = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)

            val cm = CameraPosition.Builder()
                .target(latLng)
                .zoom(17.0)
                .build()

            map?.animateCamera(
                CameraUpdateFactory.newCameraPosition(cm), 30000
            )
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