package io.github.mrfastwind.hikecompanion.carousel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.github.mrfastwind.hikecompanion.courses.Picture
import io.github.mrfastwind.hikecompanion.utils.ImageUtilities
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

class CarouselAdapter(private val carouselView:ImageCarousel) {

    private var liveList: LiveData<List<Picture>>? = null
    private var items: MutableList<CarouselItem> = mutableListOf()

    private val updater: Observer<List<Picture>> = Observer { setList(it) }

    init {
    }

    fun setList(list: List<Picture>){
        items.clear()
        list.forEach {
            var item = CarouselItem(
                imageUrl = ImageUtilities.getPicturePath(carouselView.context,it.id.toString()).path
            )
            Log.d("", item.toString())
            items.add(item)
        }
        carouselView.setData(items)
    }

    fun setLiveList(list: LiveData<List<Picture>>){
        liveList?.removeObserver { updater}
        liveList = list
        list.observeForever { updater }
    }

}