package io.github.mrfastwind.hikecompanion.carousel

import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.github.mrfastwind.hikecompanion.R
import io.github.mrfastwind.hikecompanion.courses.Picture
import io.github.mrfastwind.hikecompanion.utils.ImageUtilities
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

class CarouselAdapter(private val carouselView:ImageCarousel) {

    private var liveList: LiveData<List<Picture>>? = null
    private var items: MutableList<CarouselItem> = mutableListOf()

    private val updater: Observer<List<Picture>> = Observer { setList(it) }

    init {
        carouselView.imageScaleType = ImageView.ScaleType.FIT_CENTER
    }

    fun setList(list: List<Picture>){
        items.clear()
        list.forEach {
            var item = CarouselItem(
                imageUrl = ImageUtilities.getPicturePath(carouselView.context,it.id.toString()).path
            )
            items.add(item)
        }
        if (items.isEmpty()){
            items.add(
                CarouselItem(
                    imageDrawable = R.drawable.baseline_image_not_supported_24,
                    caption= carouselView.resources.getString(R.string.images_not_found)
                )
            )
        }
        carouselView.setData(items)
    }

    fun setLiveList(list: LiveData<List<Picture>>){
        liveList?.removeObserver { updater}
        liveList = list
        if(list.value!=null){
            setList(list.value!!)
        }else{
            setList(listOf())
        }
        list.observeForever { updater }
    }

}