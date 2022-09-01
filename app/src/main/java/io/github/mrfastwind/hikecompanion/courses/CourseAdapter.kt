package io.github.mrfastwind.hikecompanion.courses

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.mrfastwind.hikecompanion.R
import io.github.mrfastwind.hikecompanion.utils.MapUtilities
import java.util.*

/**
 * Adapter linked to the RecyclerView of the homePage
 */
class CourseAdapter(private val listener: OnItemListener, private val activity: Activity) :
    RecyclerView.Adapter<CourseViewHolder>() {
    private var courseList: List<CourseStages> = ArrayList()
    private var courseListNotFiltered: List<CourseStages> = ArrayList()

    /**
     *
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
     *
     * @param parent ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(
            R.layout.card_layout,
            parent, false
        )
        return CourseViewHolder(layoutView, listener)
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method should update the contents of the RecyclerView.ViewHolder.itemView to reflect
     * the item at the given position.
     *
     * @param holder ViewHolder which should be updated to represent the contents of the item at
     * the given position in the data set.
     * @param position position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val currentCourseItem = courseList[position]
        MapUtilities.loadMap(holder.courseMapView,currentCourseItem,activity)
        holder.courseTextView.text = currentCourseItem.course.name
        holder.dateTextView.text = currentCourseItem.course.date
    }

    override fun getItemCount(): Int {
        return courseList.size
    }

    public val courseFilter: Filter = object : Filter() {
        /**
         * Called to filter the data according to the constraint
         * @param constraint constraint used to filtered the data
         * @return the result of the filtering
         */
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: MutableList<CourseStages> = LinkedList<CourseStages>()

            //if you have no constraint --> return the full list
            if (constraint.isEmpty()) {
                filteredList.addAll(courseListNotFiltered)
            } else {
                //else apply the filter and return a filtered list
                val filterPattern = constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (item in courseListNotFiltered) {
                    if (item.course.description.lowercase(Locale.getDefault()).contains(filterPattern) ||
                        item.course.name.lowercase(Locale.getDefault()).contains(filterPattern)
                    ) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        /**
         * Called to publish the filtering results in the user interface
         * @param constraint constraint used to filter the data
         * @param results the result of the filtering
         */
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            val filteredList: MutableList<CourseStages> = LinkedList<CourseStages>()
            val result = results.values as List<*>
            for (it in result) {
                if (it is CourseStages) {
                    filteredList.add(it)
                }
            }
            //warn the adapter that the data are changed after the filtering
            updateCardListItems(filteredList)
        }
    }

    /**
     *
     * @return the Filter that should be applied to the search
     */
    fun updateCardListItems(filteredList: List<CourseStages>) {
        val diffCallback = CourseDiffCallback(courseList, filteredList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        courseList = ArrayList(filteredList)
        diffResult.dispatchUpdatesTo(this)
    }

    /**
     * Method that set the list in the Home
     * @param list the list to display in the home
     */
    fun setData(list: List<CourseStages>) {
        val diffCallback = CourseDiffCallback(courseList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        courseList= ArrayList(list)
        courseListNotFiltered = ArrayList(list)
        diffResult.dispatchUpdatesTo(this)
    }

    /**
     *
     * @param position the position of the item selected in the list displayed
     * @return the item selected
     */
    fun getItemSelected(position: Int): CourseStages {
        return courseList[position]
    }
}
