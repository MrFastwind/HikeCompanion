package io.github.mrfastwind.hikecompanion.courses

import androidx.recyclerview.widget.DiffUtil

class CourseDiffCallback/**
 * Constructor that takes the two lists
 * @param oldCourseList the old list already displayed
 * @param newCourseList the new list to display
 */(private val oldCourseList: List<CourseStages>, private val newCourseList: List<CourseStages>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldCourseList.size
    }

    override fun getNewListSize(): Int {
        return newCourseList.size
    }

    /**
     *
     * @param oldItemPosition position of the old item
     * @param newItemPosition position of the new item
     * @return true if the two items are the same
     */
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldCourseList[oldItemPosition] == newCourseList[newItemPosition]
    }

    /**
     *
     * @param oldItemPosition position of the old item
     * @param newItemPosition position of the new item
     * @return true if the two item have the same content
     */
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldCourseList[oldItemPosition]
        val newItem = newCourseList[newItemPosition]
        return oldItem == newItem
    }
}
