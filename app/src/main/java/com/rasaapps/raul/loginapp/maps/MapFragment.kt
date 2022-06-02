package com.rasaapps.raul.loginapp.maps

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.dynamic.SupportFragmentWrapper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.rasaapps.raul.loginapp.R

class MapFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)

        // use SupportMapFragment for using in fragment
        // instead of activity  MapFragment = activity   SupportMapFragment = fragment
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.frg) as SupportMapFragment?
        mapFragment!!.getMapAsync { mMap ->
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            mMap.clear() // clear old markers

            val googlePlex = CameraPosition.builder()
                .target(LatLng(-23.51541, -46.61779))
                .zoom(10f)
                .bearing(0f)
                .tilt(45f)
                .build()

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 5000, null)


            mMap.addMarker(MarkerOptions()
                .position(LatLng(-23.51541, -46.61779))
                .title("Shopping Center Norte")
                .snippet("Perto dos caras da Ericsson EDB"))


        }

        return rootView
    }

}