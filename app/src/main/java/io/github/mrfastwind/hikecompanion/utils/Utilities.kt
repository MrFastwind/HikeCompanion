package io.github.mrfastwind.hikecompanion.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import io.github.mrfastwind.hikecompanion.FullscreenFragmentActivity
import io.github.mrfastwind.hikecompanion.R
import io.github.mrfastwind.hikecompanion.ui.fragments.DetailsFragment
import io.github.mrfastwind.hikecompanion.ui.fragments.EditableDetailsFragment
import io.github.mrfastwind.hikecompanion.ui.fragments.PrivateFragment
import io.github.mrfastwind.hikecompanion.ui.fragments.PublicFragment

object Utilities {

    fun launchFullscreenActivity(activity: FragmentActivity, id: String){
        val intent = Intent(activity,FullscreenFragmentActivity::class.java)
        intent.putExtra(FullscreenFragmentActivity.FRAGMENT_KEY, id)
        activity.startActivity(intent)
    }

    fun launchTrackerOnFullscreenActivity(activity: FragmentActivity){
        val intent = Intent(activity,FullscreenFragmentActivity::class.java)
        intent.putExtra(FullscreenFragmentActivity.FRAGMENT_KEY, "ADD")
        activity.startActivity(intent)
    }

    fun launchDetailOnFullscreenActivity(activity: FragmentActivity, id: String){
        val intent = Intent(activity,FullscreenFragmentActivity::class.java)
        intent.putExtra(FullscreenFragmentActivity.FRAGMENT_KEY, "DETAILS")
        intent.putExtra(FullscreenFragmentActivity.UUID, id)
        activity.startActivity(intent)
    }

    fun launchEditableDetailOnFullscreenActivity(activity: FragmentActivity, id: String){
        val intent = Intent(activity,FullscreenFragmentActivity::class.java)
        intent.putExtra(FullscreenFragmentActivity.FRAGMENT_KEY, "EDITABLE_DETAILS")
        intent.putExtra(FullscreenFragmentActivity.UUID, id)

        activity.startActivity(intent)
    }

    const val REQUEST_IMAGE_CAPTURE = 1

    fun insertFragment(activity: FragmentActivity, containerId:Int, fragment: Fragment, tag: String) {
        val transaction = activity.supportFragmentManager.beginTransaction()
        val lastfragement = activity.supportFragmentManager.fragments.lastOrNull()

        if (lastfragement is DetailsFragment||
            lastfragement is EditableDetailsFragment){
            transaction.remove(lastfragement)
            activity.supportFragmentManager.popBackStack()
        }

        // Replace whatever is in the fragment_container_view with this fragment
        transaction.replace(containerId, fragment, tag)

        //add the transaction to the back stack so the user can navigate back except for the HomeFragment
        when(fragment){
            is PublicFragment->{}
            else->{transaction.addToBackStack(tag)}
        }


//        if (fragment !is PublicFragment) {
//            transaction.addToBackStack(tag)
//        }
        // Commit the transaction
        transaction.commit()
    }

    fun insertFragment(activity: FragmentActivity,containerId:Int, fragment: Fragment) {
        insertFragment(activity,R.id.fragment_container,fragment,fragment::class.simpleName!!)
    }

    fun insertFragment(activity: FragmentActivity, fragment: Fragment, tag: String) {
        insertFragment(activity,R.id.fragment_container,fragment,tag)
    }

    fun insertFragment(activity: FragmentActivity, fragment: Fragment) {
        insertFragment(activity,fragment,fragment::class.simpleName!!)
    }

    fun setUpToolbar(activity: AppCompatActivity, title: String?) {
        val actionBar = activity.supportActionBar
        if (actionBar == null) {
            //create a toolbar that act as SupportActionBar
            val toolbar = Toolbar(activity)
            activity.setSupportActionBar(toolbar)
        } else {
            activity.supportActionBar!!.title = title
        }
    }
}