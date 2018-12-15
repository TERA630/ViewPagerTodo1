package com.example.yoshi.viewpagertodo1

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private var rawItemList = MutableList(1) { ToDoItem() }
    val itemList = MutableLiveData<MutableList<FilteredToDoItem>>()
    var mReward: Int = 0
    lateinit var tagList: MutableList<String>
    private lateinit var mRepository: Repository
    var isOnlyFirstItemShown: Boolean = true

    fun initItems(_context: Context) {
        loadItem(_context)
        if ((rawItemList.size == 0)) {
            rawItemList = makeDefaultList(_context)
        }
        itemList.value = pickItemsToShow(rawItemList)
        tagList = getTagListFromItemList(getItemList())
        mRepository = Repository()
        mReward = mRepository.loadIntFromPreference(REWARD, _context)
    }

    fun deleteItem(index: Int, _context: Context) {
        val mList = getItemList()
        rawItemList.removeAt(mList[index].unFilter)
        updateItemsRelatedWithDeletedItem(mList[index].item.title, _context)
        saveItem(_context)
        itemList.value = pickItemsToShow(rawItemList)
    }

    fun findSucceedingItems(_title: String): MutableList<String> {
        val result = mutableListOf<String>()
        for (index in rawItemList.indices) {
            if (rawItemList[index].preceding == _title) result.add(rawItemList[index].title)
        }
        return result
    }
    fun getItemList(): MutableList<FilteredToDoItem> = itemList.value
            ?: mutableListOf(FilteredToDoItem(INDEX_WHEN_TO_MAKE_NEW_ITEM, ToDoItem(EMPTY_ITEM)))

    fun getItemListWithTag(filterStr: String): MutableList<FilteredToDoItem> {
        val filteredList = getItemList().filter { it.item.tagString.contains(filterStr) }
        return filteredList.toMutableList()
    }

    private fun getRawList(): MutableList<ToDoItem> {
        return rawItemList
    }

    fun calculateReward(_context: Context) {

        val achievedList = getRawList().filter { it.isDone }
        val notYetList = getRawList().filterNot { it.isDone }

        var getReward = 0
        for (i in achievedList.indices) {
            getReward += achievedList[i].reward
        }
        this.mReward += getReward
        mRepository.saveIntToPreference(REWARD, this.mReward, _context = _context)
        rawItemList = notYetList.toMutableList()
        this.tagList = getTagListFromItemList(getItemList())
        this.itemList.value = pickItemsToShow(notYetList)
    }

    fun loadItem(_context: Context) {
        rawItemList = loadListFromTextFile(_context)
    }

    fun loadItemsFromSdCard(_context: Context, uri: Uri) {

        _context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val pickedDir = DocumentFile.fromTreeUri(_context, uri)
        pickedDir?.let {
            rawItemList = loadListFromTextFileAtSdcard(_context, pickedDir)
            itemList.value = pickItemsToShow(rawItemList)
        }
    }
    private fun pickItemsToShow(rawList: List<ToDoItem>): MutableList<FilteredToDoItem> {
        val list = emptyList<FilteredToDoItem>().toMutableList()
        if (isOnlyFirstItemShown) {
            for (i in rawList.indices) {
                if (rawList[i].preceding == EMPTY_ITEM)  list.add(FilteredToDoItem(i, rawList[i].copy()))
            }
        } else {
            for (i in rawList.indices) list.add(FilteredToDoItem(i, rawList[i].copy()))
        }
        return list
    }
    fun saveItem(_context: Context) {
        saveListToTextFile(_context, rawItemList)
    }

    private fun updateItemsRelatedWithDeletedItem(_title: String, _context: Context) {
        for (i in rawItemList.indices) {
            if (rawItemList[i].succeeding == _title) rawItemList[i].succeeding = EMPTY_ITEM
            if (rawItemList[i].preceding == _title) rawItemList[i].preceding = EMPTY_ITEM
        }
    }
}
