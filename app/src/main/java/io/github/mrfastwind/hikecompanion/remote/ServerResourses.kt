package io.github.mrfastwind.hikecompanion.remote

import android.net.Uri
import java.util.*

object ServerResourses {
    const val DOMAIN = "example.com"
    fun getCourseUrl(uuid: UUID): Uri {

        return Uri.Builder().scheme("http")
            .authority(DOMAIN)
            .appendPath("course")
            .appendQueryParameter("id",uuid.toString())
            .build()
    }

}
