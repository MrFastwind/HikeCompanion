package io.github.mrfastwind.hikecompanion

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.github.mrfastwind.hikecompanion.ui.fragments.DetailsFragment
import io.github.mrfastwind.hikecompanion.ui.fragments.EditableDetailsFragment
import io.github.mrfastwind.hikecompanion.ui.fragments.PrivateFragment
import io.github.mrfastwind.hikecompanion.ui.fragments.PublicFragment
import io.github.mrfastwind.hikecompanion.utils.Utilities
import io.github.mrfastwind.hikecompanion.viewmodel.PrivateListViewModel
import io.github.mrfastwind.hikecompanion.viewmodel.PublicListViewModel

class MenuActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private val addViewModel: ViewModel by viewModels()
    var chipGroup:ChipGroup? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chipGroup = findViewById(R.id.chip_group)

        supportFragmentManager.addFragmentOnAttachListener { fragmentManager, fragment ->
            if(fragment::class.simpleName== PublicFragment::class.simpleName ||
                fragment::class.simpleName== PrivateFragment::class.simpleName){
                chipGroup?.visibility = View.VISIBLE
            }else{
                chipGroup?.visibility = View.GONE
            }
        }
        
        setContentView(R.layout.activity_menu)

        loadFragment(PrivateFragment())
        loadFragment(PublicFragment())

        savedInstanceState?.let {

            loadFragment(PublicFragment())
        }
        bottomNav = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNav.setOnItemSelectedListener {
            if(it.itemId==bottomNav.selectedItemId)return@setOnItemSelectedListener false

            when (it.itemId) {
                R.id.navigation_public -> {
                    loadFragment(PublicFragment())
                }
                R.id.navigation_private -> {
                    loadFragment(PrivateFragment())
                }

                else -> {return@setOnItemSelectedListener false}
            }
            chipGroup = findViewById<ChipGroup>(R.id.chip_group)
            chipGroup!!.visibility=View.VISIBLE
            return@setOnItemSelectedListener true
        }
    }

    private  fun loadFragment(fragment: Fragment){
        Utilities.insertFragment(this,fragment,fragment::class.simpleName!!)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if(item.itemId==R.id.app_bar_settings){
            val intent = Intent(this, SettingsActivity::class.java)
            this.startActivity(intent)
            return true
        }
        return false
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount

        val fragment = supportFragmentManager.fragments.last()

        if (fragment is DetailsFragment||
            fragment is EditableDetailsFragment){
            findViewById<ChipGroup>(R.id.chip_group).visibility=View.VISIBLE
        }else{
            findViewById<ChipGroup>(R.id.chip_group).visibility=View.GONE
        }

        if(fragment is PrivateFragment){
            findViewById<BottomNavigationView>(R.id.nav_view)?.selectedItemId =R.id.navigation_public
        }
        if(fragment is PublicFragment){
            fragment.activity?.finish()
        }

        super.onBackPressed()
    }
}