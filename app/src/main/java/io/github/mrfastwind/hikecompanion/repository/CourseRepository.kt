package io.github.mrfastwind.hikecompanion.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import io.github.mrfastwind.hikecompanion.courses.Course
import io.github.mrfastwind.hikecompanion.courses.CourseStages
import io.github.mrfastwind.hikecompanion.courses.DemoCourses
import io.github.mrfastwind.hikecompanion.database.CourseDatabase
import io.github.mrfastwind.hikecompanion.database.CourseDAO

class CourseRepository(application: Application) {
    private val courseDAO: CourseDAO
    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    var publicCourseList: LiveData<List<CourseStages>>
    val privateCourseList: LiveData<List<CourseStages>>

    init {
        courseDAO = application.let { CourseDatabase.getDatabase(it)?.courseDAO() }!!
        //get remote Courses
        var offline=true
        publicCourseList= courseDAO.getPublicCourseStages()
        if (offline){
            //var temp = MediatorLiveData<List<CourseStages>>()
            //temp.addSource(liveData { emit(DemoCourses.getCourses()) }){temp.value=it}
            //temp.addSource(publicCourseList){ temp.value = it }

            publicCourseList= liveData { emit(DemoCourses.getCourses(application.applicationContext)) }
            Log.d("Repository",publicCourseList.value?.size.toString())
        }

        privateCourseList= courseDAO.getCoursesWithStages
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    fun addCourse(course:Course) {
        CourseDatabase.executor.execute { courseDAO.addCourse(course) }
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