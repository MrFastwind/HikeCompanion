package io.github.mrfastwind.hikecompanion.utils

import android.net.Uri
import java.util.*

object ServerResourses {
    fun getCourse(uuid: UUID): Uri {
        val DOMAIN = "example.com"
        return Uri.Builder().scheme("http")
            .authority(DOMAIN)
            .appendPath("course")
            .appendQueryParameter("id",uuid.toString())
            .build()
    }

}
