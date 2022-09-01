package io.github.mrfastwind.hikecompanion.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import io.github.mrfastwind.hikecompanion.courses.CourseStages
import io.github.mrfastwind.hikecompanion.courses.Location
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

object MapUtilities : IRequirements{
    private var permissionRequested: Boolean = false
    private const val TAG: String = "MapUtilities"
    private val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.INTERNET)

    fun loadMap(mapView: MapView,stages: CourseStages, activity: Activity){
        configureView(mapView,activity)
        loadPath(mapView,stages)
    }

    fun configureView(mapView: MapView, activity: Activity, movable:Boolean=false) {
        mapRequirements(activity)
//        if (Build.VERSION.SDK_INT >= 16)
//            mapView.setHasTransientState(true);
        Configuration.getInstance().userAgentValue = activity.applicationContext.applicationContext.packageName
        Configuration.getInstance().tileDownloadThreads = 2

        /*
        BingMapTileSource.setBingKey("AuEudoTLnv19qlN5oYBRpf0C87JTlomZ_8eJr45ZpGzFZdUMKH8lfWOzRaUQXxcK")
        var bing = BingMapTileSource(null)
        bing.style = BingMapTileSource.IMAGERYSET_ROAD
        */
        mapView.minZoomLevel=8.0
        mapView.maxZoomLevel=19.0
        mapView.controller.setZoom(18.0)
        if (!movable){
            mapView.setOnTouchListener { _, _ -> true }
        }
        mapView.isClickable=movable
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
    }

    fun createPath(mapView: MapView):Polyline{
        var coursePath = Polyline()
        mapView.overlays.add(coursePath)
        return coursePath
    }

    fun createPath(mapView: MapView,list:List<Location>):Polyline{
        var coursePath = Polyline()
        list.forEach { coursePath.addPoint(it.asGeoPoint()) }
        mapView.overlays.add(coursePath)
        return coursePath
    }

    fun loadPath(mapView: MapView,list:List<Location>): Boolean {
        var overlay=createPath(mapView,list)
        if (list.isNotEmpty()){
            mapView.addOnFirstLayoutListener{ _: View, _: Int, _: Int, _: Int, _: Int ->
                mapView.zoomToBoundingBox(overlay.bounds,false)
                mapView.invalidate()}
            return true
        }
        return false
    }

    fun loadPath(mapView: MapView,stages: CourseStages): Boolean {
        return loadPath(mapView,stages.getOrderedStages().map { it.location })
    }

    fun createMarker(mapView: MapView,location: Location,bearing:Float=0F): Marker {
        val marker = Marker(mapView)
        marker.position = location.asGeoPoint()
        marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_CENTER)
        mapView.overlays.add(marker)
        return marker
    }

    fun createMarker(mapView: MapView,location: android.location.Location,bearing: Float=0F): Marker {
        return createMarker(mapView, Location( location), location.bearing)
    }

    fun centerMap(mapView: MapView,location: Location){
        mapView.controller.animateTo(location.asGeoPoint())
    }

    private fun mapRequirements(activity: Activity){
        if(permissionRequested){
            Log.v(TAG,"Permission already issued")
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (hasPermissions(activity,permission)){
                Log.v(TAG, "Permission is granted");
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(activity, permission,1)
                permissionRequested = true
            }
        }
    }

    private fun hasPermissions(activity: Activity, permissions: Array<String>): Boolean {
        permissions.forEach { if (checkSelfPermission(activity,it)!=PackageManager.PERMISSION_GRANTED) return false }
        return true
    }

    override val permissions: Set<String>
        get() = permission.toSet()
}

private fun Location.asGeoPoint(): GeoPoint {
    return GeoPoint(latitude,longitude)
}
