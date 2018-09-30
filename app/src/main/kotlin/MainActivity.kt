package com.example.yoshi.viewpagertodo1

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var model: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        model = ViewModelProviders.of(this).get(MainViewModel::class.java)
        model.initItems(this)
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom)

        //
        achievePoint.text = "達成：${model.earnedPoints}"
        getAchieve.setOnClickListener {
            model.calculateAchievedPoints()
            achievePoint.text = "達成：${model.earnedPoints}"

            val repository = Repository()
            repository.saveListToPreference(model.getItemList(), this.baseContext)
            repository.saveIntToPreference(EARNED_POINT, model.earnedPoints, this@MainActivity.baseContext)
        }
        // Pager Adapter setup
        val pagerAdapter = MainPagerAdapter(fragmentManager = supportFragmentManager, model = model)
        val viewPager = main_viewpager as ViewPager
        viewPager.adapter = pagerAdapter
        (main_tab as TabLayout).setupWithViewPager(viewPager)

        // Drawer setup
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
    }
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                val repository = Repository()
                val list = repository.makeDefaultList(this@MainActivity.baseContext)
                model.itemList.value = list
                repository.saveListToPreference(list, this@MainActivity.baseContext)
                Log.i("test", "Make default list by menu.")
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
