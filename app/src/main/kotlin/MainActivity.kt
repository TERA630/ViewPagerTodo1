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
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

const val INDEX_WHEN_TO_MAKE_NEW_ITEM = 1
const val KEY_TAG_STR = "tagString"
const val KEY_TAG_LIST = "tagList"

const val REQUEST_CODE_SD_READ = 1
const val REQUEST_CODE_SD_WRITE = 2


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var model: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model = ViewModelProviders.of(this).get(MainViewModel::class.java)
        model.initItems(this)
        setUpAppBarWithDrawer(this.baseContext)
        setupFabAndViewPager(main_viewpager)
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom)
    }

    private fun appBarUpdate(context: Context){
        val sb = StringBuilder(context.resources.getString(R.string.achievement))
        sb.append(model.loadCurrentReward(_context = context))
        this.title = sb.toString()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) return
        if (data == null || data.data == null) {
            Log.w("test", "data of intent was null at onActivityResult..")
            return
        } else {
            val uri = data.data!!
            when (requestCode) {
                REQUEST_CODE_SD_READ -> {
                    model.loadItemsFromSdCard(this@MainActivity.baseContext, uri)
                    return
                }
                REQUEST_CODE_SD_WRITE -> {
                    model.saveItemsToSdCard(this, uri)
                    return
                }
                else ->
                    return
            }
        }
    }

    override fun onBackPressed() {
        model.saveRawItemList(this.applicationContext)
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
    // ドロワーアイテムのイベント処理
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.make_defaultData ->
                makeDefaultList(this.applicationContext)
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
    // メニューアイテムのイベント処理
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            R.id.clearDone_and_getReward -> {
                model.calculateReward(baseContext)
                appBarUpdate(this.baseContext)
                return true
            }
            R.id.action_saveItem -> {
                model.saveRawItemList(this@MainActivity.applicationContext)
                return true
            }
            R.id.action_loadItem -> {
                model.loadItem(this@MainActivity.applicationContext)
                return true
            }
            R.id.action_loadItem_FromSdCard -> {
                startStorageAccess(REQUEST_CODE_SD_READ)
                return true
            }
            R.id.action_saveItem_ToSdCard -> {
                startStorageAccess(REQUEST_CODE_SD_WRITE)
                return true
            }
            R.id.action_deleteFile -> {
                deleteTextFile(this@MainActivity.applicationContext)
                return true
            }
            R.id.action_start_DropBox -> {
                val intent = Intent(this@MainActivity.applicationContext, LoginActivity::class.java)
                startActivityForResult(intent, REQUEST_DROPBOX_UPLOAD)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    override fun onPause() {
        super.onPause()
        model.saveRawItemList(this.applicationContext)
    }
    private fun setUpAppBarWithDrawer(_context: Context) {
        setSupportActionBar(toolbar)
        toolbar.setTitleTextAppearance(this@MainActivity, R.style.Base_TextAppearance_AppCompat_Title)
        appBarUpdate(_context)
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun setupFabAndViewPager(_viewPager: ViewPager) {
        _viewPager.adapter = MainPagerAdapter(fragmentManager = supportFragmentManager, model = model)
        (main_tab as TabLayout).setupWithViewPager(_viewPager)
        val comingTag = intent.getStringExtra(KEY_TAG_STR)
                ?: model.tagList[0] // 開始するページの指示タグがなければTagリストの1番目から表示
        val indexOfStartPage = model.tagList.indexOfFirst { it == comingTag }
        _viewPager.setCurrentItem(indexOfStartPage, true)
        mainActivity_fab.setOnClickListener { startDetailActivity(_viewPager.currentItem, INDEX_WHEN_TO_MAKE_NEW_ITEM) }
    }
    private fun startStorageAccess(requestCode: Int) {
        val sm = getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val sv = sm.primaryStorageVolume
        val intent = sv.createAccessIntent(Environment.DIRECTORY_DOWNLOADS) // Intent.dataにURIが入るはず
        startActivityForResult(intent, requestCode)
    }
    fun startDetailActivity(_pageFrom: Int, _indexOfRawItem_ToEdit: Int) {
        val shownTagText = model.tagList[_pageFrom]
        val intent = Intent(this@MainActivity.baseContext, DetailActivity::class.java)
        intent.putExtra("parentID", _indexOfRawItem_ToEdit)
        intent.putExtra(KEY_TAG_STR, shownTagText)
        intent.putExtra(KEY_TAG_LIST, makeListToCSV(model.tagList))
        model.saveRawItemList(this@MainActivity.applicationContext)
        startActivity(intent)
    }
}
