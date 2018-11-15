package com.example.yoshi.viewpagertodo1

import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class MainViewModel : ViewModel() {
    val itemList = MutableLiveData<MutableList<ToDoItem>>()
    var archievement: Int = 0
    lateinit var dateList: MutableList<String>
    lateinit var tagList: MutableList<String>
    var currentDate = "2018/8/26"
    lateinit var mRepository: Repository

    fun initItems(_context: Context) {
        mRepository = Repository()
        itemList.value = mRepository.loadListFromPreference(_context)
        archievement = mRepository.loadIntFromPreference(EARNED_POINT, _context)
        tagList = mRepository.getTagListFromItemList(getItemList())
        dateList = fetchRecentDate(context = _context)
        currentDate = dateList[0]
    }

    fun deleteItem(index: Int) {
        val mList = getItemList()
        mList.removeAt(index)
        itemList.value = mList
    }
    fun swapItem(fromPosition: Int, toPosition: Int) {
        val list = getItemList()
        val str = list[toPosition]
        list[toPosition] = list[fromPosition]
        list[fromPosition] = str
        itemList.value = list
    }
    fun getItemList(): MutableList<ToDoItem> = itemList.value
            ?: listOf(ToDoItem(EMPTY_ITEM)).toMutableList()

    fun getItemListWithTag(filterStr: String): MutableList<FilteredToDoItem> {
        val rawList = getItemList()
        val regFilterStr = Regex(filterStr)
        val filteredList: MutableList<FilteredToDoItem> = emptyList<FilteredToDoItem>().toMutableList()
        for (i in rawList.indices) {
            if (rawList[i].tagString.contains(regFilterStr)) {
                filteredList.add(FilteredToDoItem(i, rawList[i].copy()))
            }
        }
        return filteredList
    }

    fun getItemListCurrentWithTag(targetDate: String, filterStr: CharSequence): MutableList<FilteredToDoItem> {
        return if (filterStr == "") {
            getItemListWithDate(targetDate)
        } else {
            getItemListWithTag(filterStr.toString()).filterByDate(targetDate)
        }
    }

    private fun getItemListWithDate(targetDate: String): MutableList<FilteredToDoItem> {
        val rawList = getItemList()
        val result: MutableList<FilteredToDoItem> = emptyList<FilteredToDoItem>().toMutableList()
        for (i in 0..rawList.lastIndex) {
            if (isBefore(itemDate = rawList[i].startLine, baseDateStr = targetDate)) {
                result.add(FilteredToDoItem(i, rawList[i].copy()))
            }
        }
        return result
    }
    fun saveItemListToPreference(_context: Context){
        try{ mRepository.saveListToPreference(this.getItemList(),_context)}
        catch (e:Exception ){
            Log.e("test","error occur at saveItemListToPreference")
        }
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

/*    fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        Log.i("test", "spinner ${position}selected")
        currentDate = dateList[position]
    }*/

    fun calculateAchievedPoints(_context: Context) {

        val achievedList = getItemList().filter { it.isDone }
        val notYetList = getItemList().filterNot { it.isDone }
        val numberOfAchieved = achievedList.size
        Log.i("test", "number of achieved todo  was $numberOfAchieved")
        var getReward = 0
        for (i in achievedList.indices) {
            getReward += achievedList[i].reward.toInt()
        }
        Log.i("test", "reward was $getReward")
        this.archievement = this.archievement + getReward
        mRepository.saveIntToPreference(EARNED_POINT, this.archievement, context = _context)
        this.itemList.value = notYetList.toMutableList()
        this.tagList = mRepository.getTagListFromItemList(getItemList())
    }

}
