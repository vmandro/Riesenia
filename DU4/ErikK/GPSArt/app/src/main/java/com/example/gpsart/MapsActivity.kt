package com.example.gpsart

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationServices.FusedLocationApi
import com.google.android.gms.location.LocationSettingsResult
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_maps.*
import java.io.FileOutputStream

class MapsActivity : AppCompatActivity(),
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        ResultCallback<LocationSettingsResult>,
        OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val MY_PERMISSIONS_REQUEST_FINE_LOCATION = 111
    private val REQUEST_CHECK_SETTINGS = 222
    private val CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000
    lateinit var mGoogleApiClient: GoogleApiClient
    var lines : MutableList<Polyline> = listOf<Polyline>().toMutableList()
    var trasy : MutableList<MutableList<LatLng>> = listOf<MutableList<LatLng>>().toMutableList()
    var trasa : MutableList<LatLng> = listOf<LatLng>().toMutableList()
    var zoom = false
    var start = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        ),
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION
                )
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_FINE_LOCATION
            )
        }

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()

        mapFragment.getMapAsync(this)
    }

    override fun onStart() {
        super.onStart()
        mGoogleApiClient.connect()
        trasy.add(trasa)
    }

    override fun onResume() {
        super.onResume()
        mGoogleApiClient.connect()
    }

    override fun onPause() {
        super.onPause()
        if (mGoogleApiClient.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
            mGoogleApiClient.disconnect()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        val sf = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        sf.getMapAsync(this)

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton("No") { dialog, id -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(bundle: Bundle?) {
        val interval = (1 * 1000).toLong()
        val fastestInterval = (0.2 * 1000).toLong()
        val minDisplacement = 0f

        val mLocationRequest1 = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(interval)
                .setFastestInterval(fastestInterval)
                .setSmallestDisplacement(minDisplacement)

        FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest1, this)
    }

    override fun onLocationChanged(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        if (!zoom){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            zoom = true
        }
        if (start) {
            for (l in lines){
                l.remove()
            }
            lines.clear()
            trasa.add(latLng)
            for (t in trasy) {
                lines.add(mMap.addPolyline(PolylineOptions().addAll(t)))
            }
            changePos()
        }
    }

    fun changePos(){
        if(!inCenter()) {
            val cen = centerLatLong()
            mMap.moveCamera(CameraUpdateFactory.newLatLng(cen))
        }
        var count = 0
        var ret = zoomIn()
        while (ret && mMap.cameraPosition.zoom < 20){
            ret = zoomIn()
            count++
        }
        ret = zoomOut()
        while (ret){
            ret = zoomOut()
        }
    }

    fun inCenter() : Boolean {
        val bounds = mMap.getProjection().getVisibleRegion().latLngBounds
        for (t in trasy){
            for (l in t){
                if (!bounds.contains(l)){
                    return false
                }
            }
        }
        return true
    }

    fun centerLatLong() : LatLng {
        var lat = 0.0
        var long = 0.0
        var poc = 0.0
        for (t in trasy){
            for (l in t){
                lat += l.latitude
                long += l.longitude
                poc += 1.0
            }
        }
        return LatLng(lat / poc, long / poc)
    }

    fun zoomIn() : Boolean {
        val bounds = mMap.getProjection().getVisibleRegion().latLngBounds
        val zoom = mMap.cameraPosition.zoom
        for (t in trasy){
            for (l in t){
                if (!bounds.contains(l)){
                    return false
                }
            }
        }
        mMap.moveCamera(CameraUpdateFactory.zoomTo(zoom+1))
        return true
    }

    fun zoomOut() : Boolean {
        val bounds = mMap.getProjection().getVisibleRegion().latLngBounds
        val zoom = mMap.cameraPosition.zoom
        for (t in trasy){
            for (l in t){
                if (!bounds.contains(l)){
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(zoom-1))
                    return true
                }
            }
        }
        return false
    }

    override fun onResult(locationSettingsResult: LocationSettingsResult) {
        val status = locationSettingsResult.status
        when (status.statusCode) {
            LocationSettingsStatusCodes.SUCCESS -> { }
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                try {
                    status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                } catch (e: IntentSender.SendIntentException) {
                }
            }
            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> { }
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST)
            } catch (e: IntentSender.SendIntentException) {}
        }
    }

    override fun onConnectionSuspended(i: Int) {}

    fun start(v : View){
        if (start){
            buttonStart.text = "START"
            trasa = listOf<LatLng>().toMutableList()
            trasy.add(trasa)
        } else {
            buttonStart.text = "PAUSE"
        }
        start = !start
    }

    fun clear(v : View){
        for (l in lines){
            l.remove()
        }
        lines.clear()
        trasy.clear()
        trasa.clear()
        trasy.add(trasa)
    }

    fun save(v : View){
        val callback = GoogleMap.SnapshotReadyCallback{
            val view = findViewById(R.id.root) as View
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache(true)
            val bmp = Bitmap.createBitmap(it)
            view.isDrawingCacheEnabled = false
            val path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/mapScreen" + System.currentTimeMillis() + ".png"
            val stream = FileOutputStream(path)
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
            bmp.recycle()
            stream.close()
        }
        mMap.snapshot(callback)
    }
}