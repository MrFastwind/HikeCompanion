package io.github.mrfastwind.hikecompanion.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

object LiveDataUtils {

    fun <E> updateList(
        mediator: MediatorLiveData<MutableList<E>>,
        map: MutableMap<LiveData<List<E>>, List<E>>,
        liveData: LiveData<List<E>>,
        newValue: List<E>
    ){
        mediator.value?.removeAll(map[liveData].orEmpty())
        map[liveData] = newValue
        mediator.value?.addAll(map[liveData]!!)

    }
}