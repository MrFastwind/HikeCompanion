package io.github.mrfastwind.hikecompanion.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.*
import com.google.android.material.snackbar.BaseTransientBottomBar.BaseCallback
import com.google.android.material.snackbar.Snackbar
import io.github.mrfastwind.hikecompanion.R
import io.github.mrfastwind.hikecompanion.ViewModel.AddViewModel
import io.github.mrfastwind.hikecompanion.ViewModel.ImageViewModel
import io.github.mrfastwind.hikecompanion.ViewModel.PrivateListViewModel
import io.github.mrfastwind.hikecompanion.courses.Course
import io.github.mrfastwind.hikecompanion.courses.CourseStages
import io.github.mrfastwind.hikecompanion.courses.Picture
import io.github.mrfastwind.hikecompanion.courses.Stage
import io.github.mrfastwind.hikecompanion.utils.*
import io.github.mrfastwind.hikecompanion.utils.CourseUtilities.courseLength
import io.github.mrfastwind.hikecompanion.utils.CourseUtilities.format
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.util.*


class TrackerFragment: Fragment() {

    private lateinit var snackbar: Snackbar
    private var street: TextView? = null
    private val imageModel: ImageViewModel by activityViewModels()
    private val CAMERA_REQUEST: Int = 1888
    private lateinit var textCounter: TextView
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var initialized:Boolean=false
    private val MIN_DISTANCE: Int=10
    private lateinit var map: MapView
    private val viewModel: AddViewModel by activityViewModels()
    private val TAG: String = "TrackerFragment"
    private lateinit var course: Course
    private val stages: MutableList<Stage> = mutableListOf()
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastLocation: Location? = null

    private val snackbarCallback = SnackBarCallback()
    inner class SnackBarCallback : BaseCallback<Snackbar>() {
        private var undone: Boolean = false
        var photo: Bitmap? = null

        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            super.onDismissed(transientBottomBar, event)
            if(!undone){
                photo?.let { TrackerFragment@saveTempBitmap(it) }
            }
        }

        override fun onShown(transientBottomBar: Snackbar?) {
            super.onShown(transientBottomBar)
            undone = false
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tracking_view,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { activity ->
            requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission(), ActivityResultCallback { result ->
                    if (result) {
                        startLocationUpdates(activity)
                        Log.d("LAB", "PERMISSION GRANTED")
                    } else {
                        Log.d("LAB", "PERMISSION NOT GRANTED")
                        showDialog(activity)
                    }
                })

            course = Course(
                name = "Course Name",
                description = "",
                date = Calendar.getInstance().time.format(requireContext()))

            initializeLocation(activity)
            //startLocationUpdates(activity)
            view.findViewById<Button?>(R.id.save_button)?.setOnClickListener { onSave(activity) }
            view.findViewById<Button?>(R.id.cancel_button)?.setOnClickListener { onCancel(activity) }
            view.findViewById<ImageButton?>(R.id.action_image)?.setOnClickListener { capturePicture(activity) }
            textCounter = view.findViewById(R.id.distance_value)
            map = view.findViewById(R.id.mapview)
            snackbar = Snackbar.make(
                activity.findViewById(R.id.fragment_container),
                R.string.image_captured,
                Snackbar.LENGTH_SHORT
            ).setAction(R.string.undo){

            }
            snackbar.addCallback(snackbarCallback)

            //view.findViewById(R.id.street_field)?.
            MapUtilities.configureView(map,activity)
            initialized=true
        }
    }

    private fun capturePicture(activity: FragmentActivity) {
        dispatchTakePictureIntent()
    }

    private fun dispatchTakePictureIntent() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === CAMERA_REQUEST && resultCode === Activity.RESULT_OK) {
            val photo: Bitmap? = data!!.extras!!["data"] as Bitmap?
            val alertadd = AlertDialog.Builder(requireActivity())
            val factory = LayoutInflater.from(requireActivity())
            snackbarCallback.photo=photo
            snackbar.show()
//            val view: View = factory.inflate(R.layout.image_dialog, null)
//            alertadd.setView(view)
//            view.findViewById<ImageView>(R.id.dialog_imageview)?.setImageBitmap(photo)
//            alertadd.setPositiveButton(R.string.dialog_ok) { dlg, sumthin ->
//                saveTempBitmap(photo!!)
//            }
//            alertadd.setNegativeButton(R.string.dialog_cancel){ dialogInterface, i -> }
//            alertadd.show()
        }
    }

    private fun saveTempBitmap(bitmap: Bitmap) {
        if (isExternalStorageWritable()&& lastLocation!=null) {
            val picture = Picture(
                date = Calendar.getInstance().time.format(requireContext()),
                location = io.github.mrfastwind.hikecompanion.courses.Location(lastLocation!!)
            )
            if(ImageUtilities.saveImageInCache(bitmap,picture.id.toString(),requireContext())){
                imageModel.addPicture(picture)
            }
        } else {
            //prompt the user or do something
        }
    }

    /* Checks if external storage is available for read and write */
    private fun isExternalStorageWritable(): Boolean {
        val state: String = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }


    override fun onResume() {
        super.onResume()
        startLocationUpdates(requireActivity())
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun initializeLocation(activity: Activity){
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(activity)
        locationRequest = LocationRequest.create()
        locationRequest.interval = 1000
        //locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationCallback = object: LocationCallback() {
            private var line: Polyline?=null
            private var marker: Marker?  =null
            private val MIN_DISTANCE: Double= 10.0


            private fun locationFromGps(location: Location): io.github.mrfastwind.hikecompanion.courses.Location {
                return io.github.mrfastwind.hikecompanion.courses.Location(location.longitude,location.latitude)
            }

            private fun isFarEnough(it: Location): Boolean {
                return it.distanceTo(lastLocation)>=MIN_DISTANCE
            }

            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                if(!initialized){
                    return
                }
                p0.lastLocation?.let {
                    Log.d(TAG,"Location updated!")
                    if(marker==null){
                        marker=MapUtilities.createMarker(map,it)
                        line=MapUtilities.createPath(map)
                    }
                    if(lastLocation==null){
                        lastLocation=it
                        stages.add(Stage(course.id,
                            stages.size+1,
                            locationFromGps(it)
                        ))
                        MapUtilities.centerMap(map,io.github.mrfastwind.hikecompanion.courses.Location(lastLocation!!))
                    }else{
                        if(isFarEnough(it)) {
                            lastLocation=it
                            stages.add(Stage(course.id,stages.size+1,locationFromGps(it)))
                            line?.addPoint(GeoPoint(it))
                            MapUtilities.centerMap(map,io.github.mrfastwind.hikecompanion.courses.Location(lastLocation!!))
                        } else {
                            Log.v(TAG,"New location is too close, ignored")
                        }
                        marker?.updatePosition(it)
                        street?.text = getLocationName(it)
                    }
                    textCounter?.text= CourseUtilities.courseLengthAsString(stages)
                }
            }
        }
        startLocationUpdates(activity)
    }

    fun getLocationName(location:Location): String {
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            var geoResults: List<Address> = geocoder.getFromLocation(location.latitude,location.longitude,1)
            if (geoResults.isNotEmpty()) {
                val addr = geoResults[0]
                if(addr.thoroughfare!=null)return addr.thoroughfare
                if(addr.subLocality !=null)return addr.subLocality
            }
        } catch (e: java.lang.Exception) {
            Log.e(TAG,e.stackTraceToString())
        }
        return getString(R.string.unknown_location)
    }



    private fun onSave(activity: FragmentActivity) {
        if (courseLength(stages) <MIN_DISTANCE){
            AlertDialog.Builder(activity)
                .setMessage(R.string.course_length_error)
                .setCancelable(true)
                .setNeutralButton(R.string.dialog_ok) { _, _ -> Log.v(TAG, "Tried to save a to short course")}
                .create()
                .show()
            return
        }
        var courseStages = CourseStages(
            course
            ,stages)
        viewModel.setItemSelected(courseStages)
        clean()
        //send to details
        Utilities.insertFragment(activity,R.id.fragment_container,AddFragment())
    }

    private fun onCancel(activity: FragmentActivity){
        clean()
        activity.finish()
    }

    private fun clean() {
        parentFragmentManager.popBackStack()
        stopLocationUpdates()
    }

    private val permissions= arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private fun startLocationUpdates(activity: Activity) {
        val PERMISSION_REQUESTED = Manifest.permission.ACCESS_FINE_LOCATION
        if(checkPermission(activity)) {
            checkStatusGPS(activity)
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }else if(ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    PackageManager.PERMISSION_GRANTED.toString()
                ))
        {
            showDialog(activity)
        }else{
            requestPermissionLauncher!!.launch(PERMISSION_REQUESTED)
        }
    }

    private fun checkStatusGPS(activity: Activity) {
        val locationManager: LocationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            android.app.AlertDialog.Builder(activity)
                .setMessage("Your GPS is off, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_yes) { _, _ -> activity.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                .setNegativeButton(R.string.dialog_no) { dialog, _ -> dialog.cancel() }
                .create()
                .show()
        }
    }

    private fun checkPermission(activity: Activity): Boolean {
        for (it in permissions){
            if(ActivityCompat.checkSelfPermission(activity,it)!=PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(activity, it)){
                    showDialog(activity)
                }else{
                    requestPermissionLauncher!!.launch(it)
                }
            }
        }
        return true
    }
    private fun showDialog(activity: Activity) {
        android.app.AlertDialog.Builder(activity)
            .setMessage(R.string.dialog_permission_denied)
            .setCancelable(false)
            .setPositiveButton(R.string.dialog_ok) { _, _ -> activity.startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS)) }
            .setNegativeButton(R.string.dialog_cancel) { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

}

private fun Marker.updatePosition(point: Location) {
    this.position=GeoPoint(point)
    this.rotation=point.bearing
}
