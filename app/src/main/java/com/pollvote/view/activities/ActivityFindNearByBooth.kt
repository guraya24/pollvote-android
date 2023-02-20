package com.pollvote.view.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.pollvote.R
import com.pollvote.utils.TimeUtil
import kotlinx.android.synthetic.main.activity_nearby_booths.*
import kotlinx.android.synthetic.main.booth_address_dialog.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class ActivityFindNearByBooth : BaseActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {
    lateinit var mMap: GoogleMap
    var latlngArray = ArrayList<LatLng>()
    lateinit var markerOptions: MarkerOptions
    var currentLatLng: LatLng? = null
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    var eventName = ""
    var eventAddress = ""
    var eventDate = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_nearby_booths)
        initUI()
    }

    private fun initUI() {

        eventName = intent.getStringExtra("eventName").toString()
        eventAddress = intent.getStringExtra("eventAddress").toString()
        eventDate = intent.getStringExtra("eventDate").toString()

        txt_event_title.text = eventName
        txt_booth_address.text = eventAddress
        txt_date.text = eventDate.let { TimeUtil.formatServerDateToLocal(it)}
        txt_home_address.text


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        /* img_drop.setOnClickListener {
             ll_address.startAnimation(AnimationUtils.loadAnimation(this, R.anim.move_bottom))
             ll_address.visibility = View.GONE
         }*/


        img_back.setOnClickListener {
            finish()
        }

        ll_booth_address.setOnClickListener {
            getLocationFromAddress(this, eventAddress)
        }
        ll_current_address.setOnClickListener {
            setCurrentLoc()
        }

        img_direction.setOnClickListener {
            val lat=currentLatLng?.latitude
            val lng=currentLatLng?.longitude
           // val uri = java.lang.String.format(Locale.ENGLISH, "geo:%f,%f", lat,lng)
            val uri="http://maps.google.com/maps?q=loc:" + lat + "," + lng
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map;

        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN

        mMap.uiSettings?.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        setUpMap()
        getLocationFromAddress(this, eventAddress)

        val newYork = LatLng(42.890303, -77.277890)  // this is New York
        val newYorkPark = LatLng(42.887788, -77.303441)  // this is New York
        val newYorkChambers = LatLng(42.871938, -77.280920)  // this is New York
        latlngArray.add(newYork)
        latlngArray.add(newYorkPark)
        latlngArray.add(newYorkChambers)

       // placeMarkerOnMap(latlngArray)
//         mMap.addMarker(MarkerOptions().position(myPlace).title("My Favorite City"))
//
//         mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace, 12.0f))
// 2


    }

    override fun onMarkerClick(marker: Marker): Boolean {

        /*if (marker.equals(markerOptions))
        {
            //handle click here
            Log.d("title",""+ marker.title);
            Log.d("position",""+ marker.position);
        }*/

        /*ll_address.startAnimation(AnimationUtils.loadAnimation(this, R.anim.move))
        ll_address.visibility = View.VISIBLE*/
        return false
    }

    private fun placeMarkerOnMap(location: LatLng) {


            markerOptions = MarkerOptions().position(location)

            markerOptions.icon(
                BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(resources, R.mipmap.ic_booth)
                )
            )

            mMap.addMarker(markerOptions)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14.0f))


    }

    private fun multiPlaceMarkerOnMap(location: ArrayList<LatLng>) {

        for (i in location.indices) {
            markerOptions = MarkerOptions().position(location[i])

            markerOptions.icon(
                BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(resources, R.mipmap.ic_booth)
                )
            )

            mMap.addMarker(markerOptions)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location[i], 14.0f))
        }

    }
    private fun setCurrentLoc() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mMap.isMyLocationEnabled = true


        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.

            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                //   placeMarkerOnMap(currentLatLng)

                onLocationChanged(location)
                /*markerOptions = MarkerOptions().position(currentLatLng)

                markerOptions.icon(
                    BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(resources, R.drawable.loc)
                    )
                )

                mMap.addMarker(markerOptions)*/

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f))
            }

        }
    }

    private fun setUpMap() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mMap.isMyLocationEnabled = true


        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.

            if (location != null) {
                lastLocation = location
//                val currentLatLng = LatLng(location.latitude, location.longitude)
                //   placeMarkerOnMap(currentLatLng)

                onLocationChanged(location)
                /*markerOptions = MarkerOptions().position(currentLatLng)

                markerOptions.icon(
                    BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(resources, R.drawable.loc)
                    )
                )

                mMap.addMarker(markerOptions)*/

            //    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f))
            }

        }
    }

    fun onLocationChanged(location: Location) {
        val geocoder: Geocoder
        val addresses: List<Address>?
        geocoder = Geocoder(this, Locale.getDefault())
        val latitude = location.latitude
        val longitude = location.longitude
        Log.e("latitude", "latitude--$latitude")
        try {
            Log.e("latitude", "inside latitude--$latitude")
            addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.size > 0) {
                val address = addresses[0].getAddressLine(0)
//                val city = addresses[0].locality
//                val state = addresses[0].adminArea
//                val country = addresses[0].countryName
//                val postalCode = addresses[0].postalCode
//                val knownName = addresses[0].featureName
                txt_home_address.setText(address)
            }
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }
    fun getLocationFromAddress(context: Context?, strAddress: String?): LatLng? {
        val coder = Geocoder(context)
        val address: List<Address>?

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            }
            val location = address[0]
            currentLatLng = LatLng(location.latitude, location.longitude)
            placeMarkerOnMap(currentLatLng!!)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return currentLatLng
    }
}