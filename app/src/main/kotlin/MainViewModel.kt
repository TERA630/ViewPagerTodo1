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
        notifyRawItemListUpdated()
    }

    fun notifyRawItemListUpdated() {
        itemList.value = pickItemsToShow(rawItemList)
        tagList = getTagListFromItemList(getItemList())
    }

    fun deleteItem(index: Int) {
        rawItemList[index].isDeleted = true
        notifyRawItemListUpdated()
    }
    fun loadCurrentReward(_context: Context): Int {
        return loadIntFromPreference(REWARD, _context)
    }

    private fun getItemList(): MutableList<FilteredToDoItem> = itemList.value
            ?: throw IllegalArgumentException("itemList was null.")

    fun getItemListByPosition(_position: Int): MutableList<FilteredToDoItem> {
        val filterStr = tagList[_position]
        val filteredList = getItemList().filter { it.item.tagString.contains(filterStr) }
        return filteredList.toMutableList()
    }

    fun calculateReward(_context: Context) {

        val achievedList = rawItemList.filter { it.isDone }
        var reward = loadCurrentReward(_context)
        // 完了したアイテムのRewardを加算し、isDeletedをチェックする。
        for (i in achievedList.indices) {
            reward += achievedList[i].reward
            achievedList[i].isDeleted = true
        }
        saveIntToPreference(REWARD, reward, _context = _context)

        val notYetList = rawItemList.filterNot { it.isDone }
        rawItemList = notYetList.toMutableList()
        saveRawItemList(_context)
        this.tagList = getTagListFromItemList(getItemList())
        this.itemList.value = pickItemsToShow(this.rawItemList)
    }
    fun loadItem(_context: Context) {
        val listContainingDeleted = loadListFromTextFile(_context, TODO_TEXT_FILE)
        rawItemList = listContainingDeleted.filterNot { it.isDeleted }.toMutableList()
    }
    fun loadItemsFromSdCard(_context: Context, uri: Uri) {
        _context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val pickedDir = DocumentFile.fromTreeUri(_context, uri)
        pickedDir?.let {
            try {
                rawItemList = loadListFromTextFileAtSdcard(_context, pickedDir)
                notifyRawItemListUpdated()
            } catch (e: Exception) {
                Log.e("test", "${e.message} was occur at MainViewModel#loadItemFromSdcard")
            }
        }
    }

    private fun pickItemsToShow(rawList: List<ToDoItem>): MutableList<FilteredToDoItem> {
        val listNotDeleted = rawList.filterNot { it.isDeleted }
        return MutableList(listNotDeleted.size) { index -> FilteredToDoItem(index, listNotDeleted[index].copy()) }
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
