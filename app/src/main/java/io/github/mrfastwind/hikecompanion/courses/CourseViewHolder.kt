package io.github.mrfastwind.hikecompanion.courses

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.mrfastwind.hikecompanion.R
import org.osmdroid.views.MapView

/**
 * A ViewHolder describes an item view and the metadata about its place within the RecyclerView.
 */
class CourseViewHolder(itemView: View, listener: OnItemListener) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener {
    var courseMapView: MapView
    var courseTextView: TextView
    var dateTextView: TextView
    private val itemListener: OnItemListener

    init {
        courseMapView = itemView.findViewById(R.id.mapview)
        courseTextView = itemView.findViewById(R.id.place_textview)
        dateTextView = itemView.findViewById(R.id.date_textview)
        itemListener = listener
        itemView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        itemListener.onItemClick(adapterPosition)
    }
}
