package io.github.mrfastwind.hikecompanion.courses

import io.github.mrfastwind.hikecompanion.utils.RandomGeoPathGenerator
import io.github.mrfastwind.hikecompanion.utils.RandomPathGenerator
import java.time.Instant
import java.util.*
import kotlin.random.Random

object DemoCourses {

    fun getCourses(): List<CourseStages> {
        val courses = LinkedList<CourseStages>()
        for (i in 1..4){

            val course = Course(
                name = "Demo Course $i",
                description = "Demo Course",
                date = Calendar.getInstance().time.toString())

            val stages: MutableList<Stage> = LinkedList()
            //12.235725, 44.147436
            val pathGenerator = RandomGeoPathGenerator(12.235725, 44.147436,20.0)
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