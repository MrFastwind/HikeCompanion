package io.github.mrfastwind.hikecompanion.utils

import android.content.res.Resources
import io.github.mrfastwind.hikecompanion.R
import io.github.mrfastwind.hikecompanion.courses.CourseStages
import io.github.mrfastwind.hikecompanion.remote.ServerResourses

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
            ${resources.getString(R.string.course_message)}:${ServerResourses.getCourseUrl(courseStages.course.id)}
            """.trimIndent()
    }

}
