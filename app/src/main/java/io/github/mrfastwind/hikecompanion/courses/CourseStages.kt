package io.github.mrfastwind.hikecompanion.courses

import androidx.room.Embedded
import androidx.room.Relation

data class CourseStages(
    @Embedded val course: Course,
    @Relation(
        parentColumn = "id",
        entityColumn = "course"
    ) val stages: List<Stage>
){
    fun getOrderedStages(): List<Stage> {
        return stages.sortedBy { it.order }
    }
}