package com.example.yoshi.viewpagertodo1

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    lateinit var rawItemList: MutableLiveData<MutableList<ToDoItem>>
    lateinit var tagList: MutableList<String>

    fun initItems(_context: Context) {
        loadItem(_context)
        if (getAllItemNonNull().size == 0) rawItemList.value = makeDefaultList(_context)
    }
    fun deleteItem(index: Int) {
        getAllItemNonNull()[index].isDeleted = true
    }

    fun getAllItemNonNull(): MutableList<ToDoItem> {
        val itemList = rawItemList.value?.filterNot { it.isDeleted }
                ?: listOf(ToDoItem("null item", "test", System.currentTimeMillis()))
        return itemList.toMutableList()
    }

    fun getIndexOfItemFromId(id: Long): Int {
        val indexFirst = getAllItemNonNull().indexOfFirst { it.itemID == id }
        val indexLast = getAllItemNonNull().indexOfLast { it.itemID == id }
        if (indexFirst != indexLast) {
            Log.w("test", "id was not unifyed..{$indexFirst} and {$indexLast}")
        }
        return indexFirst
    }
    fun getItemListByPosition(_position: Int): MutableList<ToDoItem> {
        val filterStr = tagList[_position]
        val filteredList = getAllItemNonNull().filter { it.tagString.contains(filterStr) }
        val filteredNotDeleted = filteredList.filterNot { it.isDeleted }
        return filteredNotDeleted.toMutableList()
    }

    fun loadCurrentReward(_context: Context): Int {
        return loadIntFromPreference(REWARD, _context)
    }
    fun calculateReward(_context: Context) {

        val achievedList = getAllItemNonNull().filter { it.isDone && !(it.isDeleted) }
        var reward = loadCurrentReward(_context)
        // 完了したアイテムのRewardを加算し、isDeletedをチェックする。
        for (i in achievedList.indices) {
            reward += achievedList[i].reward
            achievedList[i].isDeleted = true
        }
        saveIntToPreference(REWARD, reward, _context = _context)

        val notYetList = getAllItemNonNull().filterNot { it.isDone }
        rawItemList.value = notYetList.toMutableList()
        saveRawItemList(_context)
        this.tagList = getTagListFromItemList(getAllItemNonNull())
    }
    fun loadItem(_context: Context) {
        val listContainingDeleted = loadListFromTextFile(_context, TODO_TEXT_FILE)
        rawItemList.value = listContainingDeleted.filterNot { it.isDeleted }.toMutableList()
    }
    fun loadItemsFromSdCard(_context: Context, uri: Uri) {
        _context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val pickedDir = DocumentFile.fromTreeUri(_context, uri)
        pickedDir?.let {
            try {
                rawItemList.value = loadListFromTextFileAtSdcard(_context, pickedDir)
            } catch (e: Exception) {
                Log.e("test", "${e.message} was occur at MainViewModel#loadItemFromSdcard")
            }
        }
    }

    fun saveRawItemList(_context: Context) {
        saveListToTextFile(_context, getAllItemNonNull())
    }
    fun saveItemsToSdCard(_context: Context, uri: Uri) {
        _context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        val pickedDir = DocumentFile.fromTreeUri(_context, uri)
        pickedDir?.let {
            saveListToTextFileAtSdcard(_context, pickedDir, getAllItemNonNull())
        }
    }
}