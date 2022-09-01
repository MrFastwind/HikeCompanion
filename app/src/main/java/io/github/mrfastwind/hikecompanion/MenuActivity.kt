package io.github.mrfastwind.hikecompanion

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.github.mrfastwind.hikecompanion.ui.fragments.PrivateFragment
import io.github.mrfastwind.hikecompanion.ui.fragments.PublicFragment
import io.github.mrfastwind.hikecompanion.utils.Utilities

class MenuActivity : AppCompatActivity() {

    private val addViewModel: ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContentView(R.layout.activity_menu)

        savedInstanceState?.let {
            loadFragment(PublicFragment())
        }
        val bottomNav = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_public -> {
                    loadFragment(PublicFragment())
                }
                R.id.navigation_private -> {
                    loadFragment(PrivateFragment())
                }

                else -> {return@setOnItemSelectedListener false}
            }
            return@setOnItemSelectedListener true
        }
        findViewById<FloatingActionButton?>(R.id.fab_add)?.setOnClickListener {
            Utilities.launchFullscreenActivity(this,"ADD")
            //Utilities.insertFragment(activity, AddFragment()
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

        if (count == 0) {
            super.onBackPressed()
            //additional code
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}