package io.github.mrfastwind.hikecompanion.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import io.github.mrfastwind.hikecompanion.R
import io.github.mrfastwind.hikecompanion.viewmodel.ImageViewModel
import io.github.mrfastwind.hikecompanion.viewmodel.PublicListViewModel
import io.github.mrfastwind.hikecompanion.carousel.CarouselAdapter
import io.github.mrfastwind.hikecompanion.repository.ImageRepository
import io.github.mrfastwind.hikecompanion.utils.CourseUtilities
import io.github.mrfastwind.hikecompanion.utils.MapUtilities
import io.github.mrfastwind.hikecompanion.utils.ShareUtilities
import io.github.mrfastwind.hikecompanion.utils.Utilities
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.osmdroid.views.MapView

open class DetailsFragment : Fragment(), MenuProvider {
    private lateinit var imageRepository: ImageRepository
    private lateinit var carouselAdapter: CarouselAdapter
    private lateinit var carousel: ImageCarousel
    private lateinit var distancetextView: TextView
    private lateinit var placeTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var mapview: MapView
    private val publicListViewModel: PublicListViewModel by activityViewModels()
    private val imageViewModel: ImageViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity: Activity? = activity
        if (activity != null) {
            imageRepository = ImageRepository(activity.application)
            Utilities.setUpToolbar(activity as AppCompatActivity, "Details")
            placeTextView = view.findViewById(R.id.name_field)
            descriptionTextView = view.findViewById(R.id.description_field)
            dateTextView = view.findViewById(R.id.date_field)
            distancetextView = view.findViewById(R.id.distance_value)
            mapview = view.findViewById(R.id.details_mapview)
            carousel = view.findViewById(R.id.carousel)
            carouselAdapter = CarouselAdapter(carousel)

            MapUtilities.configureView(mapview,activity,true)

            setUpObserver()
            //Button
            view.findViewById<View>(R.id.share_button).setOnClickListener { view:View->share(view)}
        }
    }

    private fun share(view: View) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        ShareUtilities.sharePublicLink(this.resources,publicListViewModel.itemSelected.value!!)
        shareIntent.putExtra(
            Intent.EXTRA_TEXT, ShareUtilities.share(this.resources,publicListViewModel.itemSelected.value!!)
        )
        shareIntent.type = "text/plain"
        val context = view.context
        if (context != null && shareIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(Intent.createChooser(shareIntent, null))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        onCreateMenu(menu, inflater)
        }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.findItem(R.id.app_bar_search).isVisible = false
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return super.onOptionsItemSelected(menuItem)
    }

    private fun setUpObserver() {
        publicListViewModel.itemSelected.observe(viewLifecycleOwner) { course ->
            placeTextView.text = course.course.name
            descriptionTextView.text = course.course.description
            dateTextView.text = course.course.date
            MapUtilities.loadPath(mapview, course)
            distancetextView.text = CourseUtilities.courseLengthAsString(course.getOrderedStages())

            var pictures  =imageRepository.getImagesOfCourse(course)
            pictures.observe(viewLifecycleOwner){pictures ->
                carouselAdapter.setList(pictures)
            }
        }
    }
}