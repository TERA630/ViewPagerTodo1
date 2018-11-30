package com.example.yoshi.viewpagertodo1

import android.content.Context
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.json.JSON
import kotlinx.serialization.serializer

const val ITEM_DATA = "toDoItems"
const val REWARD = "reward"
const val EMPTY_ITEM = "empty item"
const val TODO_TEXT_FILE = "toDoItems.txt"
const val REQUEST_CODE_READ = 1

@Serializable
data class ToDoItem constructor(
        var title: String = "thing to do",
        var tagString: String = "home",
        var preceding: String = "nothing",
        var succeeding: String = "nothing",
        var reward: Int = 1,
        var isDone: Boolean = false,
        var isRoutine: Boolean = false,
        var hasStartLine: Boolean = true,
        var startLine: String = "----/--/--",
        var hasDeadLine: Boolean = false,
        var deadLine: String = "----/--/--",


        )
class FilteredToDoItem constructor(
        var unFilter: Int = 0,
        var item: ToDoItem = ToDoItem()
)

class Repository {
    private fun saveStringToPreference(_key: String, _string: String, _context: Context) {
        val preferenceEditor = _context.getSharedPreferences(_key, Context.MODE_PRIVATE).edit()
        preferenceEditor.putString(_key, _string)
        preferenceEditor.apply()
    }
    private fun loadStringFromPreference(_key: String, _context: Context): String {
        val preferences = _context.getSharedPreferences(_key, Context.MODE_PRIVATE)
        return preferences?.getString(_key, EMPTY_ITEM) ?: EMPTY_ITEM
    }
    fun saveIntToPreference(_key: String, _int: Int, _context: Context) {
        val preferenceEditor = _context.getSharedPreferences(_key, Context.MODE_PRIVATE).edit()
        preferenceEditor.putInt(_key, _int)
        preferenceEditor.apply()
    }
    fun loadIntFromPreference(_key: String, _context: Context): Int {
        val preferences = _context.getSharedPreferences(_key, Context.MODE_PRIVATE)
        return preferences?.getInt(_key, 0) ?: 0
    }
    // ToDoItem with preference
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
                Log.e("test", "${e.message} with ${e.cause}")
                makeDefaultList(_context)
            }
        }
    }
}

fun convertTextListToItems(_lines: List<String>): MutableList<ToDoItem> {
    val titleAndTagMatcher = "^(.+?),(.+?)(,.*)".toRegex()
    val result = mutableListOf<ToDoItem>()
    var line: String
    for (i in _lines.indices) {
        titleAndTagMatcher.matchEntire(_lines[i])
                ?.destructured
                ?.let { (titleStr, tag, subProperty) ->
                    result.add(subPropertyExtract(ToDoItem(title = titleStr, tagString = tag), subProperty))
                }
    }
    return result
}
    // manage ItemList
    fun makeDefaultList(_context: Context): MutableList<ToDoItem> {
        val res = _context.resources
        val defaultItemTitle = res.getStringArray(R.array.default_todoItem_title)
        val defaultItemTag = res.getStringArray(R.array.default_todoItem_tag)
        val toDoList = List(defaultItemTitle.size - 1) { index ->
            ToDoItem(title = defaultItemTitle[index], tagString = defaultItemTag[index])
        }
        return toDoList.toMutableList()
    }
    fun getTagListFromItemList(_list: MutableList<ToDoItem>): MutableList<String> {
        val rawTagList: List<String> = List(_list.size) { index -> _list[index].tagString }
        val result = rawTagList.distinct()
        return result.toMutableList()
    }

fun subPropertyExtract(_toDoItem: ToDoItem, _text: String): ToDoItem {

    val precedingMatch = Regex(",preceding:(.+)").find(_text) // preceding は　preceding: .... の形式
    precedingMatch?.let { _toDoItem.preceding = it.value }
    val succeedingMatch = Regex(",succeeding:(.+)").find(_text) // preceding は　succeeding: .... の形式
    succeedingMatch?.let { _toDoItem.succeeding = it.value }

    val startDateMatch = Regex(",[0-9]{4}/[0-9]{1,2}/[0-9]{1,2}～").find(_text) // startDate は　dddd/dd/dd～　の形式
        if (startDateMatch != null) {
            _toDoItem.hasStartLine = true
            _toDoItem.startLine = (Regex("[0-9]{4}/[0-9]{1,2}/[0-9]{1,2}").find(startDateMatch.value))?.value ?: "1970/01/01"
        }
    val deadDateMatch = Regex(" ～[0-9]{4}/[0-9]{1,2}/[0-9]{1,2} ").find(_text) // deadDate は　～dddd/dd/dd　の形式
        if (deadDateMatch != null) {
            _toDoItem.hasDeadLine = true
            _toDoItem.deadLine = (Regex(" [0-9]{4}/[0-9]{1,2}/[0-9]{1,2}").find(deadDateMatch.value))?.value ?: "1970/01/01"
        }
        val rewardMatch = Regex(",reward:[0-9]").find(_text)
        if (rewardMatch != null) {
            _toDoItem.reward = (Regex("[0-9]").find(rewardMatch.value))?.value?.toInt() ?: 1
        }
        return _toDoItem
    }


// ToDoItem[] から1行のテキストへ

fun makeItemToOneLineText(toDoItem: ToDoItem): String {
        val sb = StringBuilder(toDoItem.title)
                .append(",", toDoItem.tagString, ",")
    if ((toDoItem.preceding != "nothing") and (toDoItem.preceding != "")) {
        sb.append("preceding:", toDoItem.preceding, ",")
    }
    if ((toDoItem.succeeding != "nothing") and (toDoItem.succeeding != "")) {
        sb.append("succeeding:", toDoItem.succeeding, ",")
    }
        if (toDoItem.hasStartLine) {
            sb.append(toDoItem.startLine, "～")
        } else {
            sb.append("～")
        }
        if (toDoItem.hasDeadLine) {
            sb.append(toDoItem.deadLine)
        }
        sb.append(",reward:", toDoItem.reward)
        return sb.toString()
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