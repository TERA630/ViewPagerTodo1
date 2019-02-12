package com.example.yoshi.viewpagertodo1

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var rawItemList = MutableList(1) { ToDoItem() }
    val itemList = MutableLiveData<MutableList<FilteredToDoItem>>()
    lateinit var tagList: MutableList<String>

    fun initItems(_context: Context) {
        loadItem(_context)
        if (rawItemList.size == 0) rawItemList = makeDefaultList(_context)
        pickItemsToShow(rawItemList)
        notifyRawItemListUpdated()
    }

    fun makeItemsDefault(_context: Context) {
        rawItemList = makeDefaultList(_context)
        notifyRawItemListUpdated()
    }

    private fun notifyRawItemListUpdated() {
        itemList.value = pickItemsToShow(rawItemList)
        tagList = getTagListFromItemList(getItemList())
    }
    fun deleteItem(index: Int, _context: Context) {
        val mList = getItemList()
        rawItemList.removeAt(mList[index].unFilter)
        saveRawItemList(_context)
        itemList.value = pickItemsToShow(rawItemList)
    }


    fun loadCurrentReward(_context: Context): Int {
        return loadIntFromPreference(REWARD, _context)
    }
    fun getItemList(): MutableList<FilteredToDoItem> = itemList.value
            ?: emptyList<FilteredToDoItem>().toMutableList()

    fun getItemListByPosition(_position: Int): MutableList<FilteredToDoItem> {
        val filterStr = tagList[_position]
        val filteredList = getItemList().filter { it.item.tagString.contains(filterStr) }
        return filteredList.toMutableList()
    }
    private fun getRawList(): MutableList<ToDoItem> {
        return rawItemList
    }

    fun calculateReward(_context: Context) {

        val rawList = getRawList()
        val achievedList = rawList.filter { it.isDone }

        var reward = loadCurrentReward(_context)
        for (i in achievedList.indices) {
            reward += achievedList[i].reward
        }
        saveIntToPreference(REWARD, reward, _context = _context)
        val notYetList = rawList.filterNot { it.isDone }
        rawItemList = notYetList.toMutableList()
        saveRawItemList(_context)
        this.tagList = getTagListFromItemList(getItemList())
        this.itemList.value = pickItemsToShow(this.rawItemList)
    }
    fun loadItem(_context: Context) {
        rawItemList = loadListFromTextFile(_context, TODO_TEXT_FILE)
    }
    fun loadItemsFromSdCard(_context: Context, uri: Uri) {
        _context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val pickedDir = DocumentFile.fromTreeUri(_context, uri)
        pickedDir?.let {
            try {
                rawItemList = loadListFromTextFileAtSdcard(_context, pickedDir)
                itemList.value = pickItemsToShow(rawItemList)
                tagList = getTagListFromItemList(getItemList())
            } catch (e: Exception) {
                Log.e("test", "${e.message} was occur at MainViewModel#loadItemFromSdcard")
            }
        }
    }

    private fun pickItemsToShow(rawList: List<ToDoItem>): MutableList<FilteredToDoItem> {
        return MutableList(rawList.size) { index -> FilteredToDoItem(index, rawList[index].copy()) }
    }

    fun saveRawItemList(_context: Context) {
        saveListToTextFile(_context, rawItemList)
    }

    fun saveItemsToSdCard(_context: Context, uri: Uri) {
        _context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        val pickedDir = DocumentFile.fromTreeUri(_context, uri)
        pickedDir?.let {
            saveListToTextFileAtSdcard(_context, pickedDir, rawItemList)
            itemList.value = pickItemsToShow(rawItemList)
        }
    }

}
