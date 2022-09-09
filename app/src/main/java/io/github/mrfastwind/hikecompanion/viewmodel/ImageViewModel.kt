package io.github.mrfastwind.hikecompanion.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import io.github.mrfastwind.hikecompanion.courses.CourseStages
import io.github.mrfastwind.hikecompanion.courses.Picture
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

    fun getImagesOfCourse(courseStages: CourseStages,distance:Double = 20.0): LiveData<List<Picture>>{
        val list: MediatorLiveData<List<Picture>> = MediatorLiveData()
        list.addSource(pictures){value->
            list.value=value
                ?.filter { picture->
                    courseStages.stages.forEach {
                        if(it.location.distanceInMeters(picture.location)<=distance)
                            return@filter true
                    }
                    return@filter false
                }.orEmpty()
            }
        return list
    }

}
