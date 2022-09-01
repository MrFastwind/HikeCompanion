package io.github.mrfastwind.hikecompanion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.mrfastwind.hikecompanion.ui.fragments.SettingsFragment

class SettingsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        //supportFragmentManager.beginTransaction().add(R.id.settings_fragment_container_view,SettingsFragment()).commit()
    }

}
