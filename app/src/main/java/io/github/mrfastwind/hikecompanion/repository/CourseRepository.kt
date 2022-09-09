package io.github.mrfastwind.hikecompanion.repository

import android.app.Application
import androidx.lifecycle.*
import io.github.mrfastwind.hikecompanion.courses.Course
import io.github.mrfastwind.hikecompanion.courses.CourseStages
import io.github.mrfastwind.hikecompanion.courses.DemoCourses
import io.github.mrfastwind.hikecompanion.database.CourseDatabase
import io.github.mrfastwind.hikecompanion.database.CourseDAO
import io.github.mrfastwind.hikecompanion.remote.RegistryClient
import io.github.mrfastwind.hikecompanion.utils.LiveDataUtils

class CourseRepository(application: Application) {
    private var online: Boolean = false
    private val client: RegistryClient = RegistryClient(application.applicationContext,online)
    private val courseDAO: CourseDAO
    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    var publicCourseList: LiveData<List<CourseStages>>
    val privateCourseList: LiveData<List<CourseStages>>

    init {
        courseDAO = application.let { CourseDatabase.getDatabase(it)?.courseDAO() }!!
        //get remote Courses

        val sourceMap: MutableMap<LiveData<List<CourseStages>>,List<CourseStages>> = mutableMapOf()

        var mediator = MediatorLiveData<MutableList<CourseStages>>()
        mediator.value= mutableListOf()
        var published =courseDAO.getPublicCourseStages()

        if(online){
            val remote = client.getCourses()
            mediator.addSource(remote){value->
                LiveDataUtils.updateList(mediator,sourceMap,remote,value)
            }
        }else{
            var demo = liveData { emit(DemoCourses.getCourses(application.applicationContext)) }
            mediator.addSource(demo){value->
                mediator.value?.removeAll(demo.value.orEmpty())
                if(!online){
                    mediator.value?.addAll(value)
                }
            }
        }

        mediator.addSource(published){value->
            LiveDataUtils.updateList(mediator,sourceMap,published,value)
        }

        publicCourseList= mediator as LiveData<List<CourseStages>>
        privateCourseList= courseDAO.getCoursesWithStages
    }

    fun addFullCourse(course: CourseStages){
        CourseDatabase.executor.execute {
            if(online){
                val result = client.pushCourse(course)
            }
            courseDAO.addCourseWithStages(course.course,course.stages) }
    }

    fun updateCourse(course: Course){
        CourseDatabase.executor.execute {
            if(online){
                val result = client.updateCourse(course)
            }
            courseDAO.updateCourse(course) }
    }

    fun deleteCourse(course: CourseStages) {
        CourseDatabase.executor.execute {
            if (online){
                val result = client.deleteCourse(course)
            }
            courseDAO.deleteCourse(course)
        }
    }
}
