package io.github.mrfastwind.hikecompanion.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.mrfastwind.hikecompanion.courses.CourseStages
import io.github.mrfastwind.hikecompanion.courses.Picture
import io.github.mrfastwind.hikecompanion.repository.CourseRepository
import io.github.mrfastwind.hikecompanion.repository.ImageRepository

class ImageViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ImageRepository
    val pictures: LiveData<List<Picture>>

    init {
        repository = ImageRepository(application)
        pictures = repository.imageList
    }

    fun addPicture(picture: Picture) {
        repository.addImage(picture)
    }

    fun loadPictures(courseStages :CourseStages): LiveData<List<Picture>> {
        return repository.loadImagesOfCourse(courseStages)
    }

}
