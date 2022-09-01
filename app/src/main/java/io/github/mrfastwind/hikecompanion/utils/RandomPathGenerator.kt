package io.github.mrfastwind.hikecompanion.utils

import io.github.mrfastwind.hikecompanion.courses.Location
import kotlin.random.Random

class RandomPathGenerator(location: Location, distance: Double=0.005): Iterator<Location> {
    private val ratio = 0.00001/1.11  // y : 0.00001 degree = x: 1.11 meters
    private var lastLocation:Location=location
    private val distance = distance*ratio

    override fun hasNext(): Boolean {
        return true
    }

    override fun next(): Location {
        var location = lastLocation
        lastLocation = newLocation(lastLocation,distance)
        return location
    }

    private fun newLocation(location: Location, distance: Double): Location {
        return Location(
            Random.nextDouble(location.longitude-distance,location.longitude+distance).coerceIn(-180.0,180.0),
            Random.nextDouble(location.latitude-distance,location.latitude+distance).coerceIn(-90.0,90.0)
        )
    }
}
