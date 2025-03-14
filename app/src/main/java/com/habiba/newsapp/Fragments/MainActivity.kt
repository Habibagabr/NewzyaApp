package com.habiba.newsapp.Fragments

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.habiba.newsapp.R
import com.habiba.newsapp.databinding.ActivityMainBinding
import com.habiba.newsapp.fragments.home

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var Displayedfragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Set Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.homeicon.setBackgroundResource(R.drawable.homeclicked)
        // Handle Menu Button Click
        binding.optionMenu.setOnClickListener { view ->
            showPopupMenu(view)
            binding.optionMenu.setBackgroundResource(R.drawable.menueclicked)

        }
        binding.homeicon.setOnClickListener {
            selectIcon(binding.homeicon)
            Displayedfragment= home()
            supportFragmentManager.beginTransaction()
                .replace(R.id.hostFragment, Displayedfragment) // Replace container with fragment
                .commit()
        }
        binding.searchicon.setOnClickListener {
            selectIcon(binding.searchicon)
            Displayedfragment= search()
            supportFragmentManager.beginTransaction()
                .replace(R.id.hostFragment, Displayedfragment) // Replace container with fragment
                .commit()
        }
        binding.favouriteicon.setOnClickListener {
            selectIcon(binding.favouriteicon)
            Displayedfragment= favourite()
            supportFragmentManager.beginTransaction()
                .replace(R.id.hostFragment, Displayedfragment) // Replace container with fragment
                .commit()
        }
        binding.settingicon.setOnClickListener {
            selectIcon(binding.settingicon)
            Displayedfragment= setting()
            supportFragmentManager.beginTransaction()
                .replace(R.id.hostFragment, Displayedfragment) // Replace container with fragment
                .commit()
        }

    }

    private fun showPopupMenu(anchor: View) {
        val popupMenu = PopupMenu(this, anchor)
        popupMenu.menuInflater.inflate(R.menu.menuoptions, popupMenu.menu)
        popupMenu.setOnDismissListener {
            binding.optionMenu.setBackgroundResource(R.drawable.menue) // Reset to default
        }
        // Handle menu item clicks
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_settings -> {
                    // Handle Settings Click
                    true
                }
                R.id.action_profile -> {
                    // Handle Profile Click
                    true
                }
                R.id.action_logout -> {
                    // Handle Logout Click
                    true
                }
                else -> false

            }
        }
        popupMenu.show()
    }

    // Function to reset all icons and highlight the selected one
    private fun selectIcon(selectedIcon: ImageView) {
        // Reset all icons to default (white background)
        binding.homeicon.setBackgroundResource(R.drawable.home)
        binding.searchicon.setBackgroundResource(R.drawable.search)
        binding.favouriteicon.setBackgroundResource(R.drawable.fav)
        binding.settingicon.setBackgroundResource(R.drawable.setting)

        // Set the correct clicked icon background
        when (selectedIcon.id) {
            R.id.homeicon -> selectedIcon.setBackgroundResource(R.drawable.homeclicked)
            R.id.searchicon -> selectedIcon.setBackgroundResource(R.drawable.searchclicked)
            R.id.favouriteicon -> selectedIcon.setBackgroundResource(R.drawable.favclicked)
            R.id.settingicon -> selectedIcon.setBackgroundResource(R.drawable.settingclicked)
        }
    }

}