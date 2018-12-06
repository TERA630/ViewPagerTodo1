package com.example.yoshi.viewpagertodo1

import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private var rawItemList = MutableList(1) { ToDoItem() }
    val itemList = MutableLiveData<MutableList<FilteredToDoItem>>()
    var archievement: Int = 0
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
        archievement = mRepository.loadIntFromPreference(REWARD, _context)
    }

    fun deleteItem(index: Int) {
        val mList = getItemList()
        mList.removeAt(index)
        itemList.value = mList
    }

    fun getItemList(): MutableList<FilteredToDoItem> = itemList.value
            ?: mutableListOf(FilteredToDoItem(1, ToDoItem("Enter Item")))

    fun getItemListUnfilter(): MutableList<ToDoItem> {
        return MutableList(getItemList().size) { index -> getItemList()[index].item.copy() }
    }

    fun getItemListWithTag(filterStr: String): MutableList<FilteredToDoItem> {
        val filteredList = getItemList().filter { it.item.tagString.contains(filterStr) }
        return filteredList.toMutableList()
    }

    private fun getTagListFromItemList(_list: MutableList<FilteredToDoItem>): MutableList<String> {
        val rawTagList: List<String> = List(_list.size) { index -> _list[index].item.tagString }
        val result = rawTagList.distinct()
        return result.toMutableList()
    }
    fun onEditorActionDone(edit: TextView, actionId: Int, event: KeyEvent?): Boolean {
        Log.i("test", "onEditorActionDone Call")
        return when (actionId) {
            EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NONE, EditorInfo.IME_ACTION_NEXT, EditorInfo.IME_NULL -> {
                val keyboardUtils = KeyboardUtils()
                keyboardUtils.hide(edit.context, edit)
                true
            }
            else -> {
                false
            }
        }
    }

    fun calculateAchievedPoints(_context: Context) {

        val achievedList = getItemList().filter { it.item.isDone }
        val notYetList = getItemList().filterNot { it.item.isDone }
        val numberOfAchieved = achievedList.size
        Log.i("test", "number of achieved todo  was $numberOfAchieved")
        var getReward = 0
        for (i in achievedList.indices) {
            getReward += achievedList[i].item.reward
        }
        this.archievement = this.archievement + getReward
        mRepository.saveIntToPreference(REWARD, this.archievement, _context = _context)
        this.itemList.value = notYetList.toMutableList()
        this.tagList = getTagListFromItemList(getItemList())
    }

    fun loadItem(_context: Context) {
        rawItemList = loadListFromTextFile(_context)
    }

    private fun pickItemsToShow(rawList: List<ToDoItem>): MutableList<FilteredToDoItem> {
        val list = emptyList<FilteredToDoItem>().toMutableList()
        if (isOnlyFirstItemShown) {
            for (i in rawList.indices) {
                if (rawList[i].preceding != "nothing") {
                    list.add(FilteredToDoItem(i, rawList[i].copy()))
                }
            }
        } else {
            for (i in rawList.indices) {
                list.add(FilteredToDoItem(i, rawList[i].copy()))
            }
        }
        return list
    }

    fun saveItem(_context: Context) {
        saveListToTextFile(_context, getItemListUnfilter())
    }

}
