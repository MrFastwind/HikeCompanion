package io.github.mrfastwind.hikecompanion.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.mrfastwind.hikecompanion.courses.Course
import io.github.mrfastwind.hikecompanion.courses.CourseStages
import io.github.mrfastwind.hikecompanion.repository.CourseRepository

class PrivateListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CourseRepository
    val itemSelected = MutableLiveData<CourseStages>()
    val courseItems: LiveData<List<CourseStages>>

    init {
        repository = CourseRepository(application)
        courseItems = repository.privateCourseList
    }

    fun getCourse(UUID: String):CourseStages?{
        return repository.getCourse(UUID)
    }

    fun updateCourse(course: Course){
        repository.updateCourse(course)
    }

    fun setItemSelected(course: CourseStages) {
        itemSelected.value = course
    }

    fun deleteCourse(course: CourseStages){
        repository.deleteCourse(course)
    }
}