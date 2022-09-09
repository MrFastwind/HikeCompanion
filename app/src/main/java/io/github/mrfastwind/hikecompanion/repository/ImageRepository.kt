package io.github.mrfastwind.hikecompanion.repository

import android.app.Application
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
    private var filteredList: MediatorLiveData<List<Picture>>? = null
    private val pictureDAO:PictureDAO
    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    private var online=false
    var imageList: LiveData<List<Picture>>
        private set
    var imageSourceMap: MutableMap<LiveData<List<Picture>>,List<Picture>> = mutableMapOf()
    private var imagesOfCourse: MutableLiveData<List<Picture>>

    init {
        pictureDAO = application.let { CourseDatabase.getDatabase(it)?.pictureDAO() }!!
        //get remote Courses
        val mediator= MediatorLiveData<MutableList<Picture>>()
        mediator.value= mutableListOf()
        val dbImages = pictureDAO.getAllPicture()
        mediator.addSource(dbImages){value->
            updateList(mediator, imageSourceMap, dbImages, value)
        }
        if (!online){
            val demo = liveData{emit(DemoCourses.getImages(application))}
            mediator.addSource(demo){
                updateList(mediator,imageSourceMap,demo,it)
            }
        }
        imageList = mediator as LiveData<List<Picture>>
        imageList=dbImages
        imagesOfCourse = MutableLiveData()
    }

    private fun <E> updateList(
        mediator: MediatorLiveData<MutableList<E>>,
        map: MutableMap<LiveData<List<E>>, List<E>>,
        liveData: LiveData<List<E>>,
        newValue: List<E>
    ){
        mediator.value?.removeAll(map[liveData].orEmpty())
        map[liveData] = newValue
        mediator.value?.addAll(map[liveData]!!)

    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    fun addImage(picture:Picture) {
        CourseDatabase.executor.execute { pictureDAO.addPicture(picture) }
    }


    fun loadImagesOfCourse(courseStages: CourseStages,distance:Double = 2000.0): LiveData<List<Picture>>{
        val list:MediatorLiveData<MutableList<Picture>> = MediatorLiveData()
        list.value= mutableListOf()
        val old= mutableMapOf<Int,List<Picture>>()
        CourseDatabase.executor.execute {
            courseStages.stages.forEachIndexed { index, stage ->

                list.value?.removeAll(old[index].orEmpty())
                list.addSource(pictureDAO.getPictureByDistance(stage.location, distance)){value->
                    list.value?.removeAll(old[index].orEmpty())
                    list.value?.addAll(value)
                    old[index] =value
                }
            }
        }
        return list as LiveData<List<Picture>>
    }

    fun getImagesOfCourse(courseStages: CourseStages,distance:Double = 20.0): LiveData<List<Picture>>{
        filteredList = MediatorLiveData()
        filteredList?.addSource(imageList){ value->
            var temp=value
                ?.filter { picture->
                    courseStages.stages.forEach {
                        if(it.location.distanceInMeters(picture.location)<=distance)
                            return@filter true
                    }
                    return@filter false
                }.orEmpty()
            temp
            filteredList?.value=temp
        }
        return filteredList!!
    }
}
