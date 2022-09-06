package io.github.mrfastwind.hikecompanion.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import io.github.mrfastwind.hikecompanion.R
import java.io.File
import java.io.FileOutputStream

object ImageUtilities {
    private val extention: String="jpg"
    private val TAG: String = "ImageUtilities"

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

    fun getPictureAsDrawable(activity: Activity, uri: Uri): Drawable {
        getImageBitmap(activity, uri)?.let {
            return BitmapDrawable(activity.resources,it) }
        return activity.getDrawable(R.drawable.profile)!!
    }

    fun retrieveImageInCache(name: String, context: Context):Drawable? {
        val root: File = context.cacheDir
        val image =  getImageBitmap(context,Uri.parse("${context.cacheDir}/saved_images/$name.$extention"))
        return BitmapDrawable(context.resources,image)
    }

    fun getPicturePath(context: Context,name:String): Uri{
        return Uri.parse("${context.cacheDir}/saved_images/$name.$extention")
    }



    fun saveImageInCache(finalBitmap: Bitmap, name: String, context: Context): Boolean {
        val root: File = context.cacheDir
        return saveImageToPath(finalBitmap,name,"${context.cacheDir}/saved_images")
    }

    fun saveImageToPath(finalBitmap: Bitmap, name: String, dirPath :String): Boolean {
        val directory = File(dirPath)
        directory.mkdirs()
        val fileName = "$name.$extention"
        val file = File(dirPath, fileName)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            return true
        } catch (e: Exception) {
            Log.e(TAG,e.stackTraceToString())
        }
        return false
    }
}