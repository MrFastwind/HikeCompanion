package io.github.mrfastwind.hikecompanion.utils

import androidx.core.view.forEach
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import io.github.mrfastwind.hikecompanion.R
import io.github.mrfastwind.hikecompanion.courses.CourseStages

private const val unit = 1000F

open class ChipFilters(private val group: ChipGroup) {

    private val listeners: MutableSet<()->Unit> = mutableSetOf()
    protected val filters = mutableMapOf<Int,(Float)->Boolean>(
        R.id.less_1 to {x->x<=unit},
        R.id.greater_1 to {x->x>=unit},
        R.id.less_10 to {x->x<=10*unit},
        R.id.greater_10 to {x->x>=10*unit}
    )

    protected val chips = mutableListOf<Chip>()

    init {
        group.forEach { child ->
            (child as? Chip)?.let { chip ->
                chips.add(chip)
                chip.setOnCheckedChangeListener { button, isChecked ->
                    if(chips.map{it.id}.contains(button.id)) listeners.forEach {it.invoke()}
                }
            }
        }
    }

    fun filter(course: CourseStages):Boolean{
        val distance = CourseUtilities.courseLength(course)
        val enabledFilters = filters.filter { filter ->
            chips.filter { it.isChecked }
                .map { it.id }
                .contains(filter.key)
        }
        if(enabledFilters.isEmpty()){return true}
        enabledFilters.forEach {filter ->
            if(filter.value.invoke(distance)) return true
        }
        return false
    }

    fun addOnChangeListener(predicate: ()->Unit){
        listeners.add(predicate)
    }

    fun removeOnChangeListener(predicate: ()->Unit){
        listeners.remove(predicate)
    }
}