package io.github.mrfastwind.hikecompanion.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import io.github.mrfastwind.hikecompanion.R
import io.github.mrfastwind.hikecompanion.ViewModel.AddViewModel
import io.github.mrfastwind.hikecompanion.courses.Course
import io.github.mrfastwind.hikecompanion.utils.Utilities.REQUEST_IMAGE_CAPTURE
import io.github.mrfastwind.hikecompanion.utils.Utilities.setUpToolbar
import org.json.JSONException
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddFragment : Fragment() {
    private var networkRegistered: Boolean = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var placeTIET: TextInputEditText
    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null
    private var requestingLocationUpdate = false
    private var requestQueue: RequestQueue? = null
    private lateinit var descriptionTIET: TextInputEditText
    private lateinit var networkCallback: NetworkCallback
    private var isNetworkConnected = false
    private var snackbar: Snackbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onStart() {
        super.onStart()
        if (requestingLocationUpdate) {
            registerNetworkCallback(requireActivity())
        }
    }

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdate) {
            startLocationUpdates(requireActivity())
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onStop() {
        super.onStop()
        requestQueue?.cancelAll(OSM_REQUEST_TAG)
        unregisterNetworkCallback(requireActivity())
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_travel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity: Activity? = activity
        if (activity != null) {
            snackbar = Snackbar.make(
                activity.findViewById(R.id.fragment_container),
                R.string.bar_no_internet,
                Snackbar.LENGTH_INDEFINITE
            ).setAction(R.string.settings) {
                val intent: Intent = Intent()
                intent.action = Settings.ACTION_WIRELESS_SETTINGS
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                activity.startActivity(intent)
            }
            networkCallback = object : NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    isNetworkConnected = true
                    snackbar!!.dismiss()
                    if (requestingLocationUpdate) {
                        startLocationUpdates(activity)
                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    isNetworkConnected = false
                    snackbar!!.show()
                }
            }
            requestQueue = Volley.newRequestQueue(activity)
            requestPermissionLauncher =
                registerForActivityResult(RequestPermission(), ActivityResultCallback { result ->
                    if (result) {
                        startLocationUpdates(activity)
                        Log.d("LAB", "PERMISSION GRANTED")
                    } else {
                        Log.d("LAB", "PERMISSION NOT GRANTED")
                        showDialog(activity)
                    }
                })
            initializeLocation(activity)
            setUpToolbar((activity as AppCompatActivity?)!!, getString(R.string.add_new_trip))
            view.findViewById<View>(R.id.capture_bottom).setOnClickListener {
                val takePictureIntent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                    activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
            view.findViewById<View>(R.id.gps_button).setOnClickListener {
                requestingLocationUpdate = true
                registerNetworkCallback(requireActivity())
                startLocationUpdates(activity)
            }
            val addViewModel: AddViewModel =
                ViewModelProvider((activity as ViewModelStoreOwner)).get(AddViewModel::class.java)
            val imageView = view.findViewById<ImageView>(R.id.picture_displayed_imageview)
            addViewModel.getImageBitMap().observe(viewLifecycleOwner) {
                    imageView.setImageBitmap(it) }

            placeTIET = view.findViewById(R.id.place_edittext)
            val dateTIET = view.findViewById<TextInputEditText>(R.id.date_edittext)
            descriptionTIET = view.findViewById(R.id.description_edittext)
            view.findViewById<View>(R.id.fab_add).setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    val bitmap: Bitmap? = addViewModel.getImageBitMap().value
                    val imageUriString: String
                    if (bitmap != null) {
                        imageUriString = saveImage(bitmap, activity).toString()
                    } else {
                        imageUriString = "ic_baseline_android_24"
                    }
                    if ((placeTIET.text != null) && (dateTIET.text != null) && (descriptionTIET.text != null)) {
                        addViewModel.addCourse(
                            Course(
                                name=placeTIET.text.toString(),
                                date=dateTIET.text.toString(),
                                description = descriptionTIET.text.toString(),
                            )
                        )
                        addViewModel.setImageBitmap(null)
                        activity.supportFragmentManager.popBackStack()
                    }
                }
            })
        }
    }

    @Suppress("DEPRECATION")
    private fun registerNetworkCallback(activity: Activity) {
        val connectivityManager: ConnectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            when{
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                    connectivityManager.registerDefaultNetworkCallback(networkCallback)
                    networkRegistered = true
                }
                else ->{
                    val networkInfo = connectivityManager.activeNetworkInfo
                    isNetworkConnected = networkInfo != null && networkInfo.isConnected
                }
            }
        } else {
            isNetworkConnected = false
        }
    }

    private fun unregisterNetworkCallback(activity: Activity) {
        val connectivityManager: ConnectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if(networkRegistered){
                    networkCallback?.let { connectivityManager.unregisterNetworkCallback(it)}
                }
            } else {
                snackbar!!.dismiss()
            }
        }
    }

    private fun saveImage(bitmap: Bitmap, activity: Activity): Uri? {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ITALY).format(Date())
        val name = "JPEG_$timestamp.jpg"
        val contentResolver = activity.contentResolver
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
        val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        Log.d("AddFragment", imageUri.toString())
        try {
            val outputStream = contentResolver.openOutputStream((imageUri)!!)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream!!.close()
        } catch (e: FileNotFoundException) {
            Log.e("AddFragment", e.toString())
        } catch (e: IOException) {
            Log.e("AddFragment", e.toString())
        }
        return imageUri
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.findItem(R.id.app_bar_search)?.isVisible = false
    }

    private fun initializeLocation(activity: Activity) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
        locationRequest = LocationRequest.create()
        locationRequest.interval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let {
                    if (isNetworkConnected) {
                        sendVolleyRequest(it.latitude.toString(), it.longitude.toString())
                        requestingLocationUpdate = false
                        stopLocationUpdates()
                    } else {
                        snackbar!!.show()
                    }
                }
                //String text = location.getLatitude() + ", " + location.getLongitude();
                //placeTIET.setText(text);

            }
        }
    }

    private fun sendVolleyRequest(latitude: String, longitude: String) {
        val url = ("http://nominatim.openstreetmap.org/reverse?lat="
                + latitude + "&lon=" + longitude + "&format=jsonv2&limit=1")
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null, { response ->
                try {
                    placeTIET.setText(response.get("name").toString())
                    descriptionTIET.setText(response.get("display_name").toString())
                    unregisterNetworkCallback(requireActivity())
                } catch (e: JSONException) {
                    placeTIET.setText("/")
                    e.printStackTrace()
                }
            }) { error -> Log.d("LAB-ADDFRAGMENT", error.toString()) }
        jsonObjectRequest.tag = OSM_REQUEST_TAG
        requestQueue?.add(jsonObjectRequest)
    }

    fun startLocationUpdates(activity: Activity) {
        val PERMISSION_REQUESTED = Manifest.permission.ACCESS_FINE_LOCATION
        if ((ActivityCompat.checkSelfPermission(activity, PERMISSION_REQUESTED)
                    == PackageManager.PERMISSION_GRANTED)
        ) {
            checkStatusGPS(activity)
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, PERMISSION_REQUESTED)) {
            showDialog(activity)
        } else {
            requestPermissionLauncher!!.launch(PERMISSION_REQUESTED)
        }
    }

    private fun showDialog(activity: Activity) {
        AlertDialog.Builder(activity)
            .setMessage(R.string.dialog_permission_denied)
            .setCancelable(false)
            .setPositiveButton(R.string.dialog_ok) { _, _ -> activity.startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS)) }
            .setNegativeButton(R.string.dialog_cancel) { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

    private fun checkStatusGPS(activity: Activity) {
        val locationManager: LocationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder(activity)
                .setMessage("Your GPS is off, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_yes) { _, _ -> activity.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                .setNegativeButton(R.string.dialog_no) { dialog, _ -> dialog.cancel() }
                .create()
                .show()
        }
    }

    companion object {
        private const val OSM_REQUEST_TAG = "OSM_REQUEST"
    }
}