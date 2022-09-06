package io.github.mrfastwind.hikecompanion.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import io.github.mrfastwind.hikecompanion.courses.CourseStages
import io.github.mrfastwind.hikecompanion.courses.DemoCourses
import io.github.mrfastwind.hikecompanion.courses.Picture
import io.github.mrfastwind.hikecompanion.database.CourseDatabase
import io.github.mrfastwind.hikecompanion.database.PictureDAO

class ImageRepository(application: Application) {
    private val pictureDAO:PictureDAO
    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    var imageList: LiveData<List<Picture>>
    private var imagesOfCourse: MutableLiveData<List<Picture>>

    init {
        pictureDAO = application.let { CourseDatabase.getDatabase(it)?.pictureDAO() }!!
        //get remote Courses
        var offline=true
        imageList= pictureDAO.getAllPicture()
        if (offline){

            imageList= liveData { emit(DemoCourses.getImages(application.applicationContext)) }
            Log.d("Repository",imageList.value?.size.toString())
        }
        imageList = pictureDAO.getAllPicture()
        imagesOfCourse = MutableLiveData()
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    fun addImage(picture:Picture) {
        CourseDatabase.executor.execute { pictureDAO.addPicture(picture) }
    }

    fun loadImagesOfCourse(courseStages: CourseStages,distance:Double = 5.0): LiveData<List<Picture>>{
        val list:MediatorLiveData<List<Picture>> = MediatorLiveData()
        CourseDatabase.executor.execute {
            courseStages.stages.forEach {
                list.addSource(pictureDAO.getPictureByDistance(it.location, distance)){value->value}
            }
        }
        return imagesOfCourse
    }
}
