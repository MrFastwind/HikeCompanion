package io.github.mrfastwind.hikecompanion.utils

import android.content.res.Resources
import io.github.mrfastwind.hikecompanion.R
import io.github.mrfastwind.hikecompanion.courses.CourseStages

object ShareUtilities {
    fun share(resources: Resources, courseStages: CourseStages):String {
        return """
            ${resources.getString(R.string.app_name)}
            ${resources.getText(R.string.course_title)}: ${courseStages.course.name}
            ${resources.getText(R.string.date)}: ${courseStages.course.date}
            ${resources.getText(R.string.description)}: ${courseStages.course.description}
            ${resources.getString(R.string.id)}: ${courseStages.course.id}
            """.trimIndent()
    }

    fun sharePublicLink(resources: Resources, courseStages: CourseStages):String {
        return """
            ${resources.getString(R.string.app_name)}
            ${resources.getText(R.string.course_title)}: ${courseStages.course.name}
            Check it out to:${ServerResourses.getCourse(courseStages.course.id)}
            """.trimIndent()
    }

}
