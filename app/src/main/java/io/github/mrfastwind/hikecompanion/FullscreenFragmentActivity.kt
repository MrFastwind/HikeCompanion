package io.github.mrfastwind.hikecompanion

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import io.github.mrfastwind.hikecompanion.ViewModel.PrivateListViewModel
import io.github.mrfastwind.hikecompanion.ViewModel.PublicListViewModel
import io.github.mrfastwind.hikecompanion.ui.fragments.DetailsFragment
import io.github.mrfastwind.hikecompanion.ui.fragments.EditableDetailsFragment
import io.github.mrfastwind.hikecompanion.ui.fragments.SettingsFragment
import io.github.mrfastwind.hikecompanion.ui.fragments.TrackerFragment
import io.github.mrfastwind.hikecompanion.utils.Utilities

class FullscreenFragmentActivity: AppCompatActivity() {
    private val publicmodel: PublicListViewModel by viewModels()
    private val CLASS_TAG: String= this::class.simpleName!!
    private val privatemodel: PrivateListViewModel by viewModels()


    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)
        requestedOrientation =SCREEN_ORIENTATION_USER_PORTRAIT

        intent.extras?.let {
            (it[FRAGMENT_KEY] as String?)?.let { fragment ->
                when(fragment){
                    "SETTINGS" -> {loadFragment(SettingsFragment())}
                    "ADD" -> {loadFragment(TrackerFragment())}
                    "DETAILS" -> {
                        if(it[UUID]==null){
                            Log.e(CLASS_TAG,"UUID not found")
                                return
                            }

                        val uuid = it[UUID]!!.toString()
                        val course = privatemodel.courseItems.value?.find { courseStages -> courseStages.course.id== java.util.UUID.fromString(uuid) }
                        if(course==null){
                            Log.w(CLASS_TAG,"Course not exist for UUID '$uuid'")
                            return
                        }
                        publicmodel.setItemSelected(course)
                        loadFragment(DetailsFragment())}
                    "EDITABLE_DETAILS" -> {
                        if(it[UUID]==null){
                            Log.e(CLASS_TAG,"UUID not found")
                            return
                        }
                        val uuid = it[UUID]!!.toString()
                        val course = privatemodel.courseItems.value?.find { courseStages -> courseStages.course.id== java.util.UUID.fromString(uuid) }
                        if(course==null){
                            Log.w(CLASS_TAG,"Course not exist for UUID '$uuid'")
                            return
                        }
                        privatemodel.setItemSelected(course)

                        loadFragment(EditableDetailsFragment())}
                    else ->{
                        Log.w(CLASS_TAG,"Unknown fragment key \'$fragment\' has been passed!")
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun loadFragment(fragment: Fragment){
        Utilities.insertFragment(this,R.id.fragment_container,fragment,fragment::class.simpleName!!)
    }

    companion object {
        val FRAGMENT_KEY: String = "Fragment"
        val UUID: String = "UUID"
    }
}