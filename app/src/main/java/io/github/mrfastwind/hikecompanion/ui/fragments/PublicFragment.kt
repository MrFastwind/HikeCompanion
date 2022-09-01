package io.github.mrfastwind.hikecompanion.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.github.mrfastwind.hikecompanion.R
import io.github.mrfastwind.hikecompanion.ViewModel.PublicListViewModel
import io.github.mrfastwind.hikecompanion.courses.CourseAdapter
import io.github.mrfastwind.hikecompanion.courses.OnItemListener
import io.github.mrfastwind.hikecompanion.utils.AddFragment
import io.github.mrfastwind.hikecompanion.utils.Utilities

class PublicFragment : Fragment(), OnItemListener, MenuProvider{
    private lateinit var adapter: CourseAdapter
    private val listViewModel: PublicListViewModel by activityViewModels()

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_public, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity
        if (activity != null) {
            (activity as AppCompatActivity).let { Utilities.setUpToolbar(it, getString(R.string.app_name)) }
            setRecyclerView(activity)
            listViewModel.courseItems.observe(activity) { adapter.setData(it) }
            val floatingActionButton = view.findViewById<FloatingActionButton>(R.id.fab_add)
            floatingActionButton?.let {
                it.setOnClickListener {
                    Utilities.launchFullscreenActivity(activity,"ADD")
                }
            }
        } else {
            Log.e(LOG_TAG, "Activity is null")
        }
    }

    /**
     * Method to set the RecyclerView and the relative adapter
     * @param activity the current activity
     */
    private fun setRecyclerView(activity: Activity) {
        val recyclerView = activity.findViewById<RecyclerView>(R.id.public_recycler_view)
        recyclerView?.let {
            recyclerView.setHasFixedSize(true)
            val listener: OnItemListener = this
            adapter = CourseAdapter(listener, activity)
            recyclerView.adapter = adapter
        }
    }
    override fun onItemClick(position: Int) {
        val activity: FragmentActivity? = activity
        if (activity != null) {
            (activity as AppCompatActivity?)?.let {
                var fragment = DetailsFragment()
                Utilities.insertFragment(
                    it, fragment,
                    DetailsFragment::class.java.simpleName
                )
            }
            listViewModel.setItemSelected(adapter.getItemSelected(position))
            //Utilities.launchDetailOnFullscreenActivity(activity,listViewModel.itemSelected.value!!.course.id.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        onCreateMenu(menu,inflater)
    }

    companion object {
        private const val LOG_TAG = "PublicFragment"
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        val item = menu.findItem(R.id.app_bar_search)
        val searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            /**
             * Called when the user submits the query. This could be due to a key press on the keyboard
             * or due to pressing a submit button.
             * @param query the query text that is to be submitted
             * @return true if the query has been handled by the listener, false to let the
             * SearchView perform the default action.
             */
            override fun onQueryTextSubmit(query: String): Boolean {
                onQueryTextChange(query)
                return false
            }

            /**
             * Called when the query text is changed by the user.
             * @param newText the new content of the query text field.
             * @return false if the SearchView should perform the default action of showing any
             * suggestions if available, true if the action was handled by the listener.
             */
            override fun onQueryTextChange(newText: String): Boolean {
                adapter.courseFilter.filter(newText);
                return true
            }
        })
    }
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false;
    }
}