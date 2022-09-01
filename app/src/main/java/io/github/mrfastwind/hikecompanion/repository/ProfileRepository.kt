package io.github.mrfastwind.hikecompanion.repository

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import io.github.mrfastwind.hikecompanion.R
import io.github.mrfastwind.hikecompanion.utils.Utilities
import java.io.File


class ProfileRepository(application: Application) {

    private val profileUri: Uri?
    private var profileFile: File
    private var context: Context
    private var preferences: SharedPreferences

    init {
        preferences = application.applicationContext.getSharedPreferences(R.string.profile_file.toString(),Context.MODE_PRIVATE)
        context = application.applicationContext
        profileFile = File(context.filesDir.toString(), R.string.profile_image_path.toString())
        profileUri = Uri.fromFile(profileFile)
    }

    var username: String
        get() {
            return preferences.getString("username","user").toString()
        }
        set(value) {
            preferences.edit().putString("username",value).commit()
        }



    fun setPicture(value: Bitmap){
        if (!profileFile.exists()) {
            profileFile.createNewFile()
        }
        val fos = context.openFileOutput(R.string.profile_image_path.toString(),Context.MODE_PRIVATE)
        value.compress(Bitmap.CompressFormat.PNG, 0,fos)
        fos.flush()
        fos.close()
    }

    @JvmName("setPictureFromDrawable")
    fun setPicture(value: Drawable){
        setPicture(Utilities.drawableToBitmap(value))
    }

    fun getPictureAsDrawable(activity: Activity): Drawable {
        return Utilities.getPictureAsDrawable(activity,profileUri!!)
    }
}