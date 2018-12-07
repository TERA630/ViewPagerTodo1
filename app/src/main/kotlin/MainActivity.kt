package com.example.yoshi.viewpagertodo1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.documentfile.provider.DocumentFile
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

        toolbar_layout.title = " "
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom)

        // Pager Adapter setup
        val startPage = intent.getIntExtra("returnPage", 0) // Detailから戻るときはComingPageへ 違えば0で
        val pagerAdapter = MainPagerAdapter(fragmentManager = supportFragmentManager, model = model)
        val viewPager = main_viewpager as ViewPager
        viewPager.adapter = pagerAdapter
        (main_tab as TabLayout).setupWithViewPager(viewPager)
        viewPager.setCurrentItem(startPage, true)

        // Drawer setup
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        val kUtils = KeyboardUtils()
        kUtils.hide(this)

        // set event handlers

        showOnlyFistItemOrNot.setOnCheckedChangeListener { _, isChecked ->
            model.isOnlyFirstItemShown = isChecked
        }
        mainActivity_fab.setOnClickListener {
            val shownPageNumber = viewPager.currentItem
            val shownTagText = model.tagList[shownPageNumber]
            Log.i("test", "now page is $shownPageNumber,and tag is $shownTagText")
            val intent = Intent(this@MainActivity.baseContext, DetailActivity::class.java)
            intent.putExtra("parentID", -1)
            intent.putExtra("tagString", shownTagText)
            intent.putExtra("comingPage", viewPager.currentItem)
            model.saveItem(this@MainActivity.applicationContext)
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        model.saveItem(this.applicationContext)
    }
    override fun onBackPressed() {
        model.saveItem(this.applicationContext)
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
            R.id.clearDone_and_getReward -> {
                model.calculateAchievedPoints(baseContext)
                return true
            }
            R.id.action_saveItem -> {
                model.saveItem(this@MainActivity.applicationContext)
                return true
            }
            R.id.action_loadItem -> {
                model.loadItem(this@MainActivity.applicationContext)
                return true
            }
            R.id.action_loadItem_FromSdCard -> {
                startStorageAccess(REQUEST_CODE_READ)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.save_to_text -> {
                model.saveItem(this@MainActivity.applicationContext)
            }
            R.id.load_from_text -> {
                model.loadItem(this@MainActivity.applicationContext)
            }
            R.id.load_from_sdcard -> {
                startStorageAccess(REQUEST_CODE_READ)
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun startStorageAccess(requestCode: Int) {
        val sm = getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val sv = sm.primaryStorageVolume
        val intent = sv.createAccessIntent(Environment.DIRECTORY_DOWNLOADS)
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) return
        data?.let {
            val uri = data.data
            uri?.let {
                model.loadItemsFromSdCard(this@MainActivity.baseContext, uri)
            } ?: Log.w("test", "uri in data of intent was null at onActivityResult..")
        } ?: Log.w("test", "data of intent was null at onActivityResult..")
    }
}
