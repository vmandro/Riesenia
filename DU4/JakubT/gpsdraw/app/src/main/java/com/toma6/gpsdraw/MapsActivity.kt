package com.toma6.gpsdraw
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*
import java.io.FileOutputStream
import com.toma6.gpsdraw.R

class MapsActivity : AppCompatActivity(),
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
    GoogleMap.OnMapLoadedCallback,
    GoogleMap.SnapshotReadyCallback {

    val TAG = "TAG"
    private lateinit var mMap: GoogleMap
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var currentPosition : LatLng? = null
    private var currLocationMarker : Marker? = null
    private var running = false
    private var firstTime = true

    private val REQUEST_FINE_LOCATION = 111
    private val REQUEST_WRITE = 222
    private val CONNECTION_FAILURE_REQUEST = 333

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapa = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        //permissions
        if (ContextCompat.checkSelfPermission(this@MapsActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, // Activity
                    arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION),
                    REQUEST_FINE_LOCATION)
        }

        if (ContextCompat.checkSelfPermission(this@MapsActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, // Activity
                    arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE)
        }
        buildGoogleApi()
        mapa.getMapAsync(this)

        pause.setOnClickListener { running = false }
        start.setOnClickListener { running = true }
        delete.setOnClickListener {
            if(mMap != null) {
                mMap!!.clear()
                if (mGoogleApiClient!!.isConnected) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
                    mGoogleApiClient!!.disconnect()
                }
                setUpMapIfNeeded()
                mGoogleApiClient!!.connect()
                Toast.makeText(this, "map cleared", Toast.LENGTH_SHORT).show()
            }
        }
        save.setOnClickListener {
            try { mMap!!.setOnMapLoadedCallback(this) }
            catch (e: Exception) {
                e.printStackTrace()
            }
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildGoogleApi(){
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
    }

    private fun setUpMapIfNeeded() {
        val sf = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        sf.getMapAsync(this)
    }

    override fun onMapLoaded() {
        if (mMap != null) {
            mMap!!.snapshot(this)
        }
    }

    override fun onSnapshotReady(snapshot: Bitmap?) {
        var bitmap = snapshot
        try {
            val out = FileOutputStream("/mnt/sdcard/DCIM/mapa${System.currentTimeMillis()}.png")
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 90, out)
            Toast.makeText(this, "Screenshot Completed", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Screenshot Failed", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleNewLocation(location: Location) {
        val previousLatitude = currentPosition?.latitude ?: location.latitude
        val previousLongitude = currentPosition?.longitude ?: location.longitude
        val prevLng = LatLng(previousLatitude, previousLongitude)

        currentPosition = LatLng(location.latitude,location.longitude)
        if(currentPosition == null) {
            return
        }
        else{
            currLocationMarker?.remove()
            currLocationMarker = mMap.addMarker(MarkerOptions()
                .position(currentPosition!!)
                .title("I AM HERE")
                .snippet(currentPosition.toString())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)))
            if(firstTime) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition!!, 18f))
                firstTime = false
            }
        }
        if(running){
            val twoPoint = listOf(prevLng, currentPosition)
            val polyLineOptions = PolylineOptions()
            polyLineOptions.addAll(twoPoint)
            polyLineOptions.color(Color.MAGENTA)
            mMap!!.addPolyline(polyLineOptions)
        }

        val bounds = mMap!!.projection.visibleRegion.latLngBounds
        if(!bounds.contains(currentPosition))  {
            mMap!!.animateCamera(CameraUpdateFactory.zoomOut())
        }

    }

    override fun onStart() {
        super.onStart()
        mGoogleApiClient!!.connect()
    }

    override fun onResume() {
        super.onResume()
        mGoogleApiClient!!.connect()
    }

    override fun onPause() {
        super.onPause()
        if (mGoogleApiClient!!.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
            mGoogleApiClient!!.disconnect()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setUpMapIfNeeded()
        createLManager()
    }

    fun createLManager(){
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(bundle: Bundle?) {
        val interval = (10 * 1000).toLong()
        val fastestInteval = (1000).toLong()
        val smallestDisplacement = 0F
        val lR = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(interval)
            .setFastestInterval(fastestInteval)
            .setSmallestDisplacement(smallestDisplacement)
        LocationServices.FusedLocationApi.requestLocationUpdates(
            mGoogleApiClient,
            lR,
            this@MapsActivity
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_FINE_LOCATION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG,"PERMISSION GRANTED")
                } else {
                    Log.d(TAG,"PERMISSION DENIED")
                }
                return
            }
        }
    }
    override fun onConnectionSuspended(i: Int) {
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_REQUEST)
            } catch (e: IntentSender.SendIntentException) {
                e.printStackTrace()
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.errorCode)
        }
    }

    override fun onLocationChanged(location: Location) {
        handleNewLocation(location)
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
}