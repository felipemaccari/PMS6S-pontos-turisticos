package com.example.pontos_turisticos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.pontos_turisticos.activities.SettingsActivity
import com.example.pontos_turisticos.adapter.ListTouristSpotAdpter
import com.example.pontos_turisticos.adapter.OnListTouristSpotAdapterClickListener
import com.example.pontos_turisticos.dao.TouristSpotDatabaseHandler
import com.example.pontos_turisticos.databinding.ActivityMainBinding
import com.example.pontos_turisticos.entidades.TouristSpot
import com.example.pontos_turisticos.utils.ObjectUtils

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val touristSpotDatabaseHandler by lazy { TouristSpotDatabaseHandler(this) }
    private val listAdapter by lazy {
        ListTouristSpotAdpter(this, findAllTouristSpot(), object : OnListTouristSpotAdapterClickListener {
            override fun onItemClick(touristSpot: TouristSpot) {
                val intentNew = Intent(this@MainActivity, TouristSpotActivity::class.java)
                intentNew.putExtra("id", touristSpot._id)
                startActivity(intentNew)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            val intent = Intent(this, TouristSpotActivity::class.java)
            startActivity(intent)
        }

        binding.appBarMain.rview.adapter = listAdapter

//        binding.contentMain.rview.adapter = listAdapter
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    override fun onResume() {
        Log.i(this.localClassName, "onResume")
        super.onResume()

        val updatedTouristSpots = findAllTouristSpot()
        listAdapter.refresh(updatedTouristSpots)
    }

    fun findAllTouristSpot(): List<TouristSpot>{
        val cursor = touristSpotDatabaseHandler.findList()
        val touristSpots = mutableListOf<TouristSpot>()
        if (ObjectUtils.isNotEmpty(cursor)) {
            while (cursor!!.moveToNext()) {
                touristSpots.add(TouristSpot(touristSpotDatabaseHandler, cursor))
            }
        }
        return touristSpots
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }



}