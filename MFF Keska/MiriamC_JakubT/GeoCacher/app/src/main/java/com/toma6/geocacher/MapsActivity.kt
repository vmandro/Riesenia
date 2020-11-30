package com.toma6.geocacher

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
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
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity(),
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    com.google.android.gms.location.LocationListener,
    ResultCallback<LocationSettingsResult>,
    OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var mGoogleApiClient: GoogleApiClient

    private var startLatitude : Double = 0.0
    private var startLongitude : Double = 0.0
    private var targetLatitude : Double = 0.0
    private var targetLongitude : Double = 0.0
    private var address : String = "address"
    lateinit var currLocationMarker : Marker
    val cacheLoc = Location("")
    lateinit var polyline : Polyline

    private val MY_PERMISSIONS_REQUEST_FINE_LOCATION = 111
    private val REQUEST_CHECK_SETTINGS = 222
    private val CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        startLatitude = intent.getDoubleExtra("START_LATITUDE", startLatitude)
        startLongitude = intent.getDoubleExtra("START_LONGITUDE", startLongitude)
        targetLatitude = intent.getDoubleExtra("TARGET_LATITUDE", targetLatitude)
        targetLongitude = intent.getDoubleExtra("TARGET_LONGITUDE", targetLongitude)
        address = intent.getStringExtra("ADDRESS").toString()
        addressLabel.text = address

        cacheLoc.latitude = targetLatitude
        cacheLoc.longitude = targetLongitude

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment

        //permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this@MapsActivity,
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
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isCompassEnabled
        mMap.uiSettings.isMyLocationButtonEnabled
        mMap.uiSettings.isZoomGesturesEnabled
        mMap.uiSettings.isRotateGesturesEnabled

        setUpMapIfNeeded()
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }
    }

    private fun setUpMapIfNeeded() {
        val sf = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        sf.getMapAsync(this)
    }

    override fun onStart() {
        super.onStart()
        mGoogleApiClient.connect()
    }

    override fun onPause() {
        super.onPause()
        if (mGoogleApiClient.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
            mGoogleApiClient.disconnect()
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

    override fun onResult(locationSettingsResult: LocationSettingsResult) {
        Log.d("TAG", "onResult")
        val status = locationSettingsResult.status
        when (status.statusCode) {
            LocationSettingsStatusCodes.SUCCESS -> {
            }
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                try {
                    status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                } catch (e: IntentSender.SendIntentException) {
                }
            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
            }
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d("TAG", "onConnectionFailed")
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST
                )
            } catch (e: IntentSender.SendIntentException) {
                e.printStackTrace()
            }
        } else {
            Log.i(
                "TAG",
                "Location services connection failed with code " + connectionResult.errorCode
            )
        }
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(bundle: Bundle?) {
        Log.d("TAG", "onConnected")
        val interval = (10 * 1000).toLong()   // 10 seconds, in milliseconds
        val fastestInterval = (1 * 1000).toLong()  // 1 second, in milliseconds
        val minDisplacement = 0f

        // Add a marker on start position and focus Camera on it
        val latLng = LatLng(startLatitude, startLongitude)
        val startPositionMarker = MarkerOptions()
        startPositionMarker.position(latLng)
        startPositionMarker.title("ME")
        startPositionMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        startPositionMarker.visible(false)
        currLocationMarker = mMap.addMarker(startPositionMarker)

        // Add a marker on cache position
        val targetPosition = LatLng(targetLatitude, targetLongitude)
        mMap.addMarker(
            MarkerOptions()
                .position(targetPosition)
                .title("CACHE IS HERE")
                .snippet("geocache")
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.cache))
                .flat(true)
        )


        val options = PolylineOptions().width(5f).add(latLng).add(
            LatLng(
                targetLatitude,
                targetLongitude
            )
        )
        polyline = this.mMap.addPolyline(options)
        polyline.isVisible = false


        val mLocationRequest1 = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(interval)
            .setFastestInterval(fastestInterval)
            .setSmallestDisplacement(minDisplacement)

        FusedLocationApi
            .requestLocationUpdates(mGoogleApiClient, mLocationRequest1, this@MapsActivity)
    }
    private fun handleNewLocation(location: Location) {
        Log.d("TAG", location.toString())

        //merge distance between cache and my position
        val actualLoc = Location("")
        actualLoc.latitude = location.latitude
        actualLoc.longitude = location.longitude

        val distanceInMeters = actualLoc.distanceTo(cacheLoc).toInt()
        val bearing: Float = actualLoc.bearingTo(cacheLoc)

        val latLng = LatLng(location.latitude, location.longitude)
        if (currLocationMarker != null) {
            currLocationMarker.remove()
            val cameraPosition = CameraPosition.Builder()
                .target(latLng)
                .zoom(15f)
                .bearing(bearing)
                .build()
            mMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(cameraPosition))
        }


        //update marker
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("ME")
        markerOptions.snippet(distanceInMeters.toString() + "m to goal")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        currLocationMarker = mMap.addMarker(markerOptions)
        currLocationMarker.showInfoWindow()

        polyline.remove()
        val options = PolylineOptions().width(5f).color(R.color.purple_500).add(latLng).add(
            LatLng(
                targetLatitude,
                targetLongitude
            )
        )
        polyline = this.mMap.addPolyline(options)

        if(distanceInMeters <= 2) {
            inAreaLabel.text = "CACHE IN $distanceInMeters m AREA"
        } else inAreaLabel.text = distanceInMeters.toString() + "m from GEOCACHE"
    }
    override fun onLocationChanged(location: Location) {
        handleNewLocation(location)
    }
    override fun onConnectionSuspended(i: Int) {
    }
}