package io.github.mrfastwind.hikecompanion.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.mrfastwind.hikecompanion.courses.CourseStages
import io.github.mrfastwind.hikecompanion.repository.CourseRepository

class PublicListViewModel(application: Application) : AndroidViewModel(application) {
    val itemSelected = MutableLiveData<CourseStages>()
    val courseItems: LiveData<List<CourseStages>>

    init {
        val repository = CourseRepository(application)
        courseItems = repository.publicCourseList
    }

    fun setItemSelected(course: CourseStages) {
        itemSelected.value = course
    }
}