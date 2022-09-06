package io.github.mrfastwind.hikecompanion.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import io.github.mrfastwind.hikecompanion.courses.Course
import io.github.mrfastwind.hikecompanion.courses.CourseStages
import io.github.mrfastwind.hikecompanion.courses.DemoCourses
import io.github.mrfastwind.hikecompanion.database.CourseDatabase
import io.github.mrfastwind.hikecompanion.database.CourseDAO
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.merge
import okhttp3.internal.notify

class CourseRepository(application: Application) {
    private var online: Boolean = false
    private val courseDAO: CourseDAO
    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    var publicCourseList: LiveData<List<CourseStages>>
    val privateCourseList: LiveData<List<CourseStages>>

    init {
        courseDAO = application.let { CourseDatabase.getDatabase(it)?.courseDAO() }!!
        //get remote Courses

        var list = MediatorLiveData<MutableList<CourseStages>>()
        var demo = liveData { emit(DemoCourses.getCourses(application.applicationContext)) }
        var published =courseDAO.getPublicCourseStages()
        var oldlist = published.value.orEmpty()
        list.value= mutableListOf()
        list.addSource(demo){value->
                list.value?.removeAll(demo.value.orEmpty())
            if(!online){
                list.value?.addAll(value)
            }
        }
        list.addSource(published){value->
            list.value?.removeAll(oldlist)
            list.value?.addAll(value.orEmpty())
            oldlist=value.orEmpty()
        }
        publicCourseList= list as LiveData<List<CourseStages>>

        privateCourseList= courseDAO.getCoursesWithStages
    }

    fun addCourse(course:Course) {
        CourseDatabase.executor.execute {
            courseDAO.addCourse(course)
        }
    }

    fun addFullCourse(course: CourseStages){
        CourseDatabase.executor.execute { courseDAO.addCourseWithStages(course.course,course.stages) }
    }

    fun updateCourse(course: Course){
        CourseDatabase.executor.execute { courseDAO.updateCourse(course) }
    }

    fun getCourse(UUID:String): CourseStages? {
        return courseDAO.getCourse(UUID)
    }

    fun deleteCourse(course: CourseStages) {
        CourseDatabase.executor.execute {
            courseDAO.deleteCourse(course)
        }
    }
}