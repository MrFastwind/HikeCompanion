package io.github.mrfastwind.hikecompanion.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import io.github.mrfastwind.hikecompanion.FullscreenFragmentActivity
import io.github.mrfastwind.hikecompanion.R
import io.github.mrfastwind.hikecompanion.ViewModel.PrivateListViewModel
import io.github.mrfastwind.hikecompanion.utils.CourseUtilities
import io.github.mrfastwind.hikecompanion.utils.MapUtilities
import io.github.mrfastwind.hikecompanion.utils.ShareUtilities
import org.osmdroid.views.MapView

class EditableDetailsFragment : Fragment() {

    private val TAG: String = "EditableFragment"
    private val model:PrivateListViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.editable_details,container,false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(model.itemSelected.value==null){
            Log.e(TAG,"No Course is been set!")
        }
        val courseStages = model.itemSelected.value!!

        val map = view.findViewById<MapView>(R.id.details_mapview)
        MapUtilities.configureView(
            map,
            requireActivity(),
            true)

        val date = view.findViewById<TextView>(R.id.date_field)

        view.findViewById<View>(R.id.share_button).setOnClickListener { view1: View ->
            val shareIntent = Intent(Intent.ACTION_SEND)
            ShareUtilities.share(this.resources,model.itemSelected.value!!)
            shareIntent.putExtra(
                Intent.EXTRA_TEXT, ShareUtilities.share(this.resources,model.itemSelected.value!!)
            )
            shareIntent.type = "text/plain"
            val context = view1.context
            if (context != null && shareIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(Intent.createChooser(shareIntent, null))
            }
        }
        view.findViewById<View>(R.id.delete_button).setOnClickListener { view1: View ->
            model.deleteCourse(model.itemSelected.value!!)
            if (requireActivity()::class.simpleName==FullscreenFragmentActivity::class.simpleName){
                requireActivity().finish()
            }
            parentFragmentManager.popBackStack()

    }

        val name = view.findViewById<EditText>(R.id.name_field)
        name.addTextChangedListener{text: Editable? ->
            run {
                courseStages.course.name = text.toString()
                model.updateCourse(courseStages.course)
            }
        }
        val description = view.findViewById<EditText>(R.id.description_field)
            description.addTextChangedListener{text: Editable? ->
            run {
                courseStages.course.description = text.toString()
                model.updateCourse(courseStages.course)
            }
        }
        val publicSwitch = view.findViewById<SwitchCompat>(R.id.public_switch)
            publicSwitch.setOnCheckedChangeListener { _, isChecked ->
            run {
                courseStages.course.published = isChecked
                model.updateCourse(courseStages.course)
            }
        }

        val distance = view.findViewById<TextView>(R.id.distance_value)

        name.setText(courseStages.course.name)
        description.setText(courseStages.course.description)
        publicSwitch.isChecked=courseStages.course.published
        distance.text= CourseUtilities.courseLengthAsString(
            courseStages.getOrderedStages())

        model.itemSelected.observe(viewLifecycleOwner) { course ->
            name.setText(courseStages.course.name)
            description.setText(courseStages.course.description)
            publicSwitch.isChecked=courseStages.course.published
            distance.text= CourseUtilities.courseLengthAsString(
                courseStages.getOrderedStages())
            MapUtilities.loadPath(map,courseStages)
            distance.text= CourseUtilities.courseLengthAsString(course.getOrderedStages())
        }

    }
    private fun setUpObserver() {

    }
}