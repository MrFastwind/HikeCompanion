package io.github.mrfastwind.hikecompanion.courses

import android.content.Context
import io.github.mrfastwind.hikecompanion.utils.CourseUtilities.format
import io.github.mrfastwind.hikecompanion.utils.RandomGeoPathGenerator
import io.github.mrfastwind.hikecompanion.utils.RandomPathGenerator
import java.time.Instant
import java.util.*
import kotlin.random.Random

object DemoCourses {

    val location: Location= Location(44.147436,12.235725 )// Location(12.235725, 44.147436)


    fun getImages(context: Context): List<Picture>{
        return listOf(
            Picture(
                date = Calendar.getInstance().time.format(context),
                location = location
            )
        )
    }

    fun getCourses(context: Context): List<CourseStages> {
        val courses = LinkedList<CourseStages>()
        for (i in 1..4){

            val course = Course(
                name = "Demo Course $i",
                description = "Demo Course",
                date = Calendar.getInstance().time.format(context))

            val stages: MutableList<Stage> = LinkedList()
            //12.235725, 44.147436
            val pathGenerator = RandomGeoPathGenerator(location,20.0)
            for (y in 0..5){

                stages.add(
                    Stage(
                        course = course.id,
                        order =  y,
                        location = pathGenerator.next()
                    )
                )
            }

            val fullCourse = CourseStages(
                course,
                stages
            )
            courses.add(fullCourse)
        }
        return courses
    }
}
