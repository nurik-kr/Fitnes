package ru.nurik.fitnes.base

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import com.mapbox.core.constants.Constants.PRECISION_6
import com.mapbox.geojson.LineString
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import ru.nurik.fitnes.R
import ru.nurik.fitnes.utils.MapUtils
import ru.nurik.fitnes.utils.PermissionUtils

abstract class BaseMapActivity : SupportMapActivity() {

    private var symbol: Symbol? = null
    private var symbolManager: SymbolManager? = null

    override fun onMapLoaded(
        mapBoxMap: MapboxMap,
        style: Style
    ) { // после загрузки карты и стиля выз эта функция
        setupListeners(mapBoxMap) // нажимая на карту вставляем маркер
        loadImages(style) // грузим картинку
        initSource(style) // грузим
        initLayer(style)
        mapView.let { symbolManager = SymbolManager(it!!, mapBoxMap, style) }
        if (PermissionUtils.requestLocationPermission(this)) //проверка на гео
            showUserLocation()
    }

    private fun initLayer(style: Style) { // рисует линию
        val layer = LineLayer(LINE_LAYER, LINE_SOURCE) // присвоили знач
        layer.setProperties( // параметры
            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
            PropertyFactory.lineWidth(4f), //ширина линии
            PropertyFactory.lineColor(Color.parseColor("#009688"))
        )
        style.addLayer(layer)
    }

    private fun initSource(style: Style) {
        style.addSource(GeoJsonSource(LINE_SOURCE)) // путь отрисовки пользователя
    }

    private fun getDirections(LatLng: LatLng) { // рисует линию между мест до маркера и считает
        val location = map?.locationComponent?.lastKnownLocation

        MapUtils.getDirections(location, LatLng) {
            val source = map?.style?.getSourceAs<GeoJsonSource>(LINE_SOURCE)
            if (source != null) {
                if (it?.geometry() != null) {
                    source.setGeoJson(LineString.fromPolyline(it.geometry()!!, PRECISION_6))
                }
            }
        }
    }

    private fun setupListeners(mapBoxMap: MapboxMap) { //клик на карту
        mapBoxMap.addOnMapClickListener {
            addMarker(it)
            getDirections(it)
            return@addOnMapClickListener true
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun loadImages(style: Style) {
        style.addImageAsync(
            MARKER_IMAGE,
            BitmapUtils.getBitmapFromDrawable(resources.getDrawable(R.drawable.ic_baseline_add_location_244))!!
        )
    }

    private fun addMarker(LatLng: LatLng) { // добавляем маркер
//        symbol?.let { symbolManager?.delete(it) } // при каждом дабвлении удаляется старый маркер и проверка что гео не ноль


        val symbolOptions = SymbolOptions()
            .withLatLng(LatLng)
            .withIconImage(MARKER_IMAGE)
        symbol = symbolManager?.create(symbolOptions) // для начала создаем symbolManager
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

            val latLng = MapUtils.locationToLatLng(location) // coordinate

            animateCamera(latLng) // камера зуум
        }
    }

    private fun animateCamera(latLng: LatLng) { // камера зуум
        val cm = CameraPosition.Builder()
            .target(latLng)
            .zoom(16.5)
            .build()

        map?.animateCamera(
            CameraUpdateFactory.newCameraPosition(cm), 5000
        )
    }

    companion object {
        const val MARKER_IMAGE = "MARKER_IMAGE"
        const val LINE_SOURCE = "LINE_SOURCE"
        const val LINE_LAYER = "LINE_LAYER"
    }
}