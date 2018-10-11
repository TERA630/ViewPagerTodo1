package com.example.yoshi.viewpagertodo1

import android.content.Context
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.json.JSON
import kotlinx.serialization.serializer

const val ITEM_DATA = "toDoItems"
const val EARNED_POINT = "earnedPoint"
const val EMPTY_ITEM = "empty item"

@Serializable
data class ToDoItem constructor(
        var title: String = "thing to do",
        var reward: Float = 1.0f,
        var isDone: Boolean = false,
        var isRoutine: Boolean = false,
        var hasStartLine: Boolean = true,
        var startLine: String = "----/--/--",
        var hasDeadLine: Boolean = false,
        var deadLine: String = "----/--/--",
        var tagString: String = "home"
)
class FilteredToDoItem constructor(
        var unFilter: Int = 0,
        var item: ToDoItem = ToDoItem()
)

class Repository {
    private fun saveStringToPreference(_key: String, _string: String, context: Context) {
        val preferences = context.getSharedPreferences(_key, Context.MODE_PRIVATE)
        val preferenceEditor = preferences.edit()
        preferenceEditor.putString(_key, _string)
        preferenceEditor.apply()
    }

    private fun loadStringFromPreference(_key: String, context: Context): String {
        val preferences = context.getSharedPreferences(_key, Context.MODE_PRIVATE)
        return preferences?.getString(_key, EMPTY_ITEM) ?: EMPTY_ITEM
    }

    fun saveIntToPreference(_key: String, _int: Int, context: Context) {
        val preferences = context.getSharedPreferences(_key, Context.MODE_PRIVATE)
        val preferenceEditor = preferences.edit()
        preferenceEditor.putInt(_key, _int)
        preferenceEditor.apply()
    }

    fun loadIntFromPreference(_key: String, context: Context): Int {
        val preferences = context.getSharedPreferences(_key, Context.MODE_PRIVATE)
        return preferences?.getInt(_key, 0) ?: 0
    }

    fun saveListToPreference(_mList: MutableList<ToDoItem>, _context: Context) {
        val toDoSerializer = ToDoItem::class.serializer()
        val listSerializer = ArrayListSerializer(toDoSerializer)
        val serializedStringList = JSON.unquoted.stringify(listSerializer, _mList.toList())
        saveStringToPreference(ITEM_DATA, serializedStringList, _context)
    }

    fun loadListFromPreference(_context: Context): MutableList<ToDoItem> {
        val jsonString = loadStringFromPreference(ITEM_DATA, _context)

        return if (jsonString == EMPTY_ITEM) {
            makeDefaultList(_context)
        } else {
            return try {
                val toDoSerializer = ToDoItem::class.serializer()
                val listSerializer = ArrayListSerializer(toDoSerializer)
                JSON.unquoted.parse(listSerializer, jsonString).toMutableList()
            } catch (e: Exception) {
                Log.e("Error", "${e.message} with ${e.cause}")
                makeDefaultList(_context)
            }
        }
    }

    fun makeDefaultList(_context: Context): MutableList<ToDoItem> {
        val res = _context.resources
        val defaultItemTitle = res.getStringArray(R.array.default_todoItem_title)
        val defaultItemStartDate = res.getStringArray(R.array.default_todoItem_startDate)
        val defaultItemTag = res.getStringArray(R.array.default_todoItem_tag)
        val toDoList = List(defaultItemTitle.size - 1) { index ->
            ToDoItem(title = defaultItemTitle[index], reward = 1.0f,
                    isDone = false, hasStartLine = true, startLine = defaultItemStartDate[index], hasDeadLine = false, tagString = defaultItemTag[index])
        }
        return toDoList.toMutableList()
    }

    fun getTagListFromItemList(_list: MutableList<ToDoItem>): MutableList<String> {

        val rawTagList: List<String> = List(_list.size) { index -> _list[index].tagString }
        val result = rawTagList.distinct()
        // * ToDo 複数のタグをもつアイテムの処理
        return result.toMutableList()
    }
}

fun MutableList<FilteredToDoItem>.swap(oneIndex: Int, otherIndex: Int) {
    val temp = this[otherIndex]
    this[otherIndex] = this[oneIndex]
    this[oneIndex] = temp
}

fun MutableList<FilteredToDoItem>.filterByDate(dateStr: String): MutableList<FilteredToDoItem> {
    val list = emptyList<FilteredToDoItem>().toMutableList()

    for (i in this.indices) {
        if ((this[i].item.hasStartLine) && isBefore(dateStr, this[i].item.startLine)) {
            list.add(FilteredToDoItem(i, this[i].item.copy()))
        }
    }
    return list
}

/*
fun saveListToTextFile(_list:MutableList<ToDoItem>) {
    val src = File("text.txt").absoluteFile

    for (index in _list.indices) {
        val sb = StringBuilder(_list[index].title)
        sb.append(",", _list[index].tagString, ",")
        if (_list[index].hasStartLine) {
            sb.append(_list[index].startLine, "～")
        } else {
            sb.append("～")
        }
        if (_list[index].hasDeadLine) {
            sb.append(_list[index].deadLine)
        }
        sb.append(_list[index].reward)
    }
}
}*/
/*    fun loadListFromTextFile():MutableList<ToDoItem>{
        val textFile = File("items.text").absoluteFile
        textFile.bufferedReader().use() {
            val rawLine = textFile.readLines().filter(String::isNullOrEmpty)
            val item = emptyList<ToDoItem>().toMutableList()
            for(i in rawLine.indices){
            val decodedElement =  rawLine[i].split(",")
            item.add( ToDoItem(decodedElement[0],decodedElement[1],))
            }
        }
        return
    }

}*/