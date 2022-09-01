package io.github.mrfastwind.hikecompanion.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import io.github.mrfastwind.hikecompanion.FullscreenFragmentActivity
import io.github.mrfastwind.hikecompanion.R
import io.github.mrfastwind.hikecompanion.ui.fragments.PublicFragment

object Utilities {

    fun launchFullscreenActivity(activity: FragmentActivity, id: String){
        val intent = Intent(activity,FullscreenFragmentActivity::class.java)
        intent.putExtra(FullscreenFragmentActivity.FRAGMENT_KEY, id)
        activity.startActivity(intent)
    }

    fun launchTrackerOnFullscreenActivity(activity: FragmentActivity){
        val intent = Intent(activity,FullscreenFragmentActivity::class.java)
        intent.putExtra(FullscreenFragmentActivity.FRAGMENT_KEY, "ADD")
        activity.startActivity(intent)
    }

    fun launchDetailOnFullscreenActivity(activity: FragmentActivity, id: String){
        val intent = Intent(activity,FullscreenFragmentActivity::class.java)
        intent.putExtra(FullscreenFragmentActivity.FRAGMENT_KEY, "DETAILS")
        intent.putExtra(FullscreenFragmentActivity.UUID, id)
        activity.startActivity(intent)
    }

    fun launchEditableDetailOnFullscreenActivity(activity: FragmentActivity, id: String){
        val intent = Intent(activity,FullscreenFragmentActivity::class.java)
        intent.putExtra(FullscreenFragmentActivity.FRAGMENT_KEY, "EDITABLE_DETAILS")
        intent.putExtra(FullscreenFragmentActivity.UUID, id)

        activity.startActivity(intent)
    }

    const val REQUEST_IMAGE_CAPTURE = 1

    fun insertFragment(activity: FragmentActivity, containerId:Int, fragment: Fragment, tag: String) {
        val transaction = activity.supportFragmentManager.beginTransaction()

        // Replace whatever is in the fragment_container_view with this fragment
        transaction.replace(containerId, fragment, tag)

        //add the transaction to the back stack so the user can navigate back except for the HomeFragment
        if (fragment !is PublicFragment) {
            transaction.addToBackStack(tag)
        }
        // Commit the transaction
        transaction.commit()
    }

    fun insertFragment(activity: FragmentActivity,containerId:Int, fragment: Fragment) {
        insertFragment(activity,R.id.fragment_container,fragment,fragment::class.simpleName!!)
    }

    fun insertFragment(activity: FragmentActivity, fragment: Fragment, tag: String) {
        insertFragment(activity,R.id.fragment_container,fragment,tag)
    }

    fun insertFragment(activity: FragmentActivity, fragment: Fragment) {
        insertFragment(activity,fragment,fragment::class.simpleName!!)
    }

    fun setUpToolbar(activity: AppCompatActivity, title: String?) {
        val actionBar = activity.supportActionBar
        if (actionBar == null) {
            //create a toolbar that act as SupportActionBar
            val toolbar = Toolbar(activity)
            activity.setSupportActionBar(toolbar)
        } else {
            activity.supportActionBar!!.title = title
        }
    }

    /**
     * Utility to convert a drawable into a bitmap (to store the android icon as a bitmap)
     * @param drawable of the android icon
     * @return the bitmap of the drawable
     */
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * Utility Method that get the Bitmap from the URI where the image is stored
     * @param activity activity when the method is executed
     * @param currentPhotoUri the URI for the image to get
     * @return the bitmap contained in the URI
     */
    fun getImageBitmap(activity: Activity, currentPhotoUri: Uri?): Bitmap? {
        return getImageBitmap(activity.applicationContext,currentPhotoUri)
    }

    fun getImageBitmap(context: Context, currentPhotoUri: Uri?): Bitmap? {
        val resolver = context.contentResolver
        try {
            val stream = resolver.openInputStream(currentPhotoUri!!)
            val bitmap = BitmapFactory.decodeStream(stream)
            stream!!.close()
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getPictureAsDrawable(activity: Activity, uri:Uri):Drawable{
        Utilities.getImageBitmap(activity, uri)?.let {
            return BitmapDrawable(activity.resources,it) }
        return activity.getDrawable(R.drawable.profile)!!
    }
}