package io.github.mrfastwind.hikecompanion.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import io.github.mrfastwind.hikecompanion.FullscreenFragmentActivity
import io.github.mrfastwind.hikecompanion.R
import io.github.mrfastwind.hikecompanion.viewmodel.AddViewModel
import io.github.mrfastwind.hikecompanion.utils.CourseUtilities
import io.github.mrfastwind.hikecompanion.utils.MapUtilities
import org.osmdroid.views.MapView

class AddFragment : Fragment() {
    private val TAG: String = "AddFragment"
    private val model: AddViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_course,container,false)
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
            true
        )

        val date = view.findViewById<TextView>(R.id.date_field)


        view.findViewById<Button>(R.id.save_button).setOnClickListener { view1: View ->
            model.addCourse(courseStages)
            if (requireActivity()::class.simpleName== FullscreenFragmentActivity::class.simpleName){
                requireActivity().finish()
            }
            parentFragmentManager.popBackStack()

        }

        val name = view.findViewById<EditText>(R.id.name_field)
        name.addTextChangedListener{text: Editable? ->
            run {
                courseStages.course.name = text.toString()
                //model.updateCourse(courseStages.course)
            }
        }
        val description = view.findViewById<EditText>(R.id.description_field)
        description.addTextChangedListener{text: Editable? ->
            run {
                courseStages.course.description = text.toString()
                //model.updateCourse(courseStages.course)
            }
        }
        val publicSwitch = view.findViewById<SwitchCompat>(R.id.public_switch)
        publicSwitch.setOnCheckedChangeListener { _, isChecked ->
            run {
                courseStages.course.published = isChecked
                //model.updateCourse(courseStages.course)
            }
        }

        val distance = view.findViewById<TextView>(R.id.distance_value)

        name.setText(courseStages.course.name)
        description.setText(courseStages.course.description)
        publicSwitch.isChecked=courseStages.course.published
        distance.text= CourseUtilities.courseLengthAsString(
            courseStages.getOrderedStages()
        )

        model.itemSelected.observe(viewLifecycleOwner) { course ->
            name.setText(courseStages.course.name)
            description.setText(courseStages.course.description)
            publicSwitch.isChecked=courseStages.course.published
            distance.text= CourseUtilities.courseLengthAsString(
                courseStages.getOrderedStages()
            )
            MapUtilities.loadPath(map, courseStages)
            distance.text= CourseUtilities.courseLengthAsString(course.getOrderedStages())
        }

    }
}