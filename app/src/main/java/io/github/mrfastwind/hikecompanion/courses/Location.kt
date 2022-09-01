package io.github.mrfastwind.hikecompanion.courses

import androidx.room.Ignore


fun Location(location: android.location.Location):Location{
    return Location(location.longitude,location.latitude)
}

class Location(
    val longitude:Double,
    val latitude:Double
){
    @Ignore
    private val androidLocation:android.location.Location = android.location.Location("")

    init {
        androidLocation.longitude=longitude
        androidLocation.latitude=latitude
    }

    fun distanceInMeters(point: Location): Float {
        return androidLocation.distanceTo(point.androidLocation)
    }
}

