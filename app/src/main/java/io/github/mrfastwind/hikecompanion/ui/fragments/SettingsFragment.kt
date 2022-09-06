package io.github.mrfastwind.hikecompanion.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import io.getstream.avatarview.AvatarView
import io.getstream.avatarview.coil.loadImage
import io.github.mrfastwind.hikecompanion.R
import io.github.mrfastwind.hikecompanion.repository.ProfileRepository
import io.github.mrfastwind.hikecompanion.utils.ImageUtilities
import io.github.mrfastwind.hikecompanion.utils.Utilities


class SettingsFragment() : Fragment(), MenuProvider {
    private var avatarView: AvatarView? = null
    private val IMAGE_CODE: Int = 1
    private lateinit var intentReceiver: ActivityResultLauncher<String>
    private lateinit var repository: ProfileRepository

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity: Activity? = activity
        if (activity != null) {
            repository = ProfileRepository(activity.application)
            Utilities.setUpToolbar((activity as AppCompatActivity?)!!, getString(R.string.settings))
            val textInputLayout = view.findViewById<TextInputLayout>(R.id.username_textinput)
            val editText = textInputLayout.editText
            val textView = view.findViewById<TextView>(R.id.username_textview)
            editText!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    textView.text = s
                    repository.username=s.toString()
                }
            })
            textView.text = repository.username

            val avatarView = view.findViewById<AvatarView>(R.id.profile_image)
            this.avatarView = avatarView
            avatarView.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                if(hasFocus) avatarView.loadImage(repository.getPictureAsDrawable(activity))
            }
            avatarView.loadImage(repository.getPictureAsDrawable(activity))
            avatarView.setOnClickListener {
                val intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                ).setType("image/")
                startActivityForResult(intent,IMAGE_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CODE) {
            data?.data?.let {
                ImageUtilities.getImageBitmap(requireContext(), it)?.let {
                    repository.setPicture(it)
                    avatarView?.let { avatarView -> avatarView.loadImage(it) }

                }
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        menu.findItem(R.id.app_bar_search).isVisible = false
        menu.findItem(R.id.app_bar_settings).isVisible = false
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return super.onContextItemSelected(menuItem)
    }
}