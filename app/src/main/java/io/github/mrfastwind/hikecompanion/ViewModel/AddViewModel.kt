package io.github.mrfastwind.hikecompanion.ViewModel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.github.mrfastwind.hikecompanion.courses.Course
import io.github.mrfastwind.hikecompanion.courses.CourseStages
import io.github.mrfastwind.hikecompanion.courses.Stage
import io.github.mrfastwind.hikecompanion.repository.CourseRepository

class AddViewModel(application: Application) : AndroidViewModel(application) {
    private val imageBitMap: MutableLiveData<Bitmap> = MutableLiveData<Bitmap>()
    private val repository: CourseRepository
    val itemSelected = MutableLiveData<CourseStages>()

    init {
        repository = CourseRepository(application)
    }

    fun setImageBitmap(bitmap: Bitmap?) {
        imageBitMap.value = bitmap!!
    }

    fun getImageBitMap(): MutableLiveData<Bitmap> {
        return imageBitMap
    }

    fun addCourse(course: CourseStages) {
        repository.addFullCourse(course)
    }

    fun addCourse(course: Course) {
        repository.addFullCourse(CourseStages(course, ArrayList()))
    }
    fun updateCourse(course:Course){
        repository.updateCourse(course)
    }

    fun setItemSelected(course: CourseStages) {
        itemSelected.value = course
    }
}