package ru.nurik.fitnes.ui

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.core.constants.Constants.PRECISION_6
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
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
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.nurik.fitnes.R
import ru.nurik.fitnes.Utils.PermissionUtils

abstract class BaseMapActivity : AppCompatActivity() {

    abstract fun getResId(): Int // для вью
    abstract fun getMapViewId(): Int // для айди

    private var mapView: MapView? = null
    private var symbol: Symbol? = null
    private var client: MapboxDirections? = null

    protected var symbolManager: SymbolManager? = null

    protected var map: MapboxMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(applicationContext, getString(R.string.api_key))
        setContentView(getResId())
        mapView = findViewById(getMapViewId())
        mapView?.onCreate(savedInstanceState)

        mapView?.getMapAsync { mapBoxMap -> // загрузка карты
            map = mapBoxMap
            mapBoxMap.setStyle(Style.LIGHT) { style ->
                setupListeners(mapBoxMap) // нажимая на карту вставляем маркер
                loadImages(style) // грузим картинку
                initSource(style) // грузим
                initLayer(style)
                mapView.let { symbolManager = SymbolManager(it!!, mapBoxMap, style) }
                if (PermissionUtils.requestLocationPermission(this)) //проверка на гео
                    showUserLocation()
            }
        }
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

    protected fun getDirections(LatLng: LatLng) { // рисует линию между мест до маркера и считает
        val location = map?.locationComponent?.lastKnownLocation

        client = MapboxDirections.builder()
            .accessToken(getString(R.string.api_key))
            .origin(Point.fromLngLat(location?.longitude ?: 0.0, location?.latitude ?: 0.0))
            .overview(DirectionsCriteria.OVERVIEW_FULL)
            .destination(Point.fromLngLat(LatLng.longitude, LatLng.latitude))
            .profile(DirectionsCriteria.PROFILE_DRIVING) // какой путь нужен?:пешком , на машине и т.д
            .build()

        client?.enqueueCall(object : Callback<DirectionsResponse> {

            override fun onResponse(
                call: Call<DirectionsResponse>,
                response: Response<DirectionsResponse>
            ) {
                val currentRoute = response.body()?.routes()?.first() // для последней гео
                Toast.makeText(applicationContext,"До пункта назначения = "+currentRoute?.distance().toString() + "m",Toast.LENGTH_LONG).show()
                val source = map?.style?.getSourceAs<GeoJsonSource>(LINE_SOURCE)

                source.let { it?.setGeoJson(currentRoute?.geometry()?.
                let { it1 -> LineString.fromPolyline(it1, PRECISION_6) })
                }
            }

            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                Log.d("onFailure", "Error")
            }
        })

    }

    private fun setupListeners(mapBoxMap: MapboxMap) { //клик на карту
        mapBoxMap.addOnMapClickListener {
            addMarker(it)
            getDirections(it)
            return@addOnMapClickListener true
        }
    }

    private fun loadImages(style: Style) {
        style.addImageAsync(
            MARKER_IMAGE,
            BitmapUtils.getBitmapFromDrawable(resources.getDrawable(R.drawable.ic_baseline_add_location_244))!!
        )
    }

    private fun addMarker(LatLng: LatLng) { // добавляем маркер
                symbol?.let { symbolManager?.delete(it) } // при каждом дабвлении удаляется старый маркер и проверка что гео не ноль

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

            val latLng = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)

            val cm = CameraPosition.Builder()
                .target(latLng)
                .zoom(17.0)
                .build()

            map?.animateCamera(
                CameraUpdateFactory.newCameraPosition(cm), 3000
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

    companion object {
        const val MARKER_IMAGE = "MARKER_IMAGE"
        const val LINE_SOURCE = "LINE_SOURCE"
        const val LINE_LAYER = "LINE_LAYER"
    }
}