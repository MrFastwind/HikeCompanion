package io.github.mrfastwind.hikecompanion.utils

import io.github.mrfastwind.hikecompanion.courses.Location
import org.osmdroid.util.GeoPoint
import kotlin.random.Random


fun RandomGeoPathGenerator(location:Location,distance:Double):RandomGeoPathGenerator{
    return RandomGeoPathGenerator(location.latitude,location.longitude,distance)
}
class RandomGeoPathGenerator(
    latitude: Double = Random.nextDouble(-90.0,90.0),
    longitude:Double = Random.nextDouble(-180.0,180.0),
    private val distance: Double = 5.0
    ):Iterator<Location> {

    private var point: GeoPoint

    init {
        point = GeoPoint(latitude,longitude)
    }

    override fun hasNext(): Boolean {
        return true
    }

    override fun next(): Location {
        val location = Location(point.latitude,point.longitude)
        point = point.destinationPoint(distance,Random.nextDouble(0.0,360.0))
        return location
    }
}