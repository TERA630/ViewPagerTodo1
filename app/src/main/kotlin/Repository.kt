package com.example.yoshi.viewpagertodo1

import android.content.Context

const val REWARD = "reward"
const val EMPTY_ITEM = "empty item"
const val TODO_TEXT_FILE = "toDoItems.txt"
const val REQUEST_CODE_READ = 1
const val REQUEST_CODE_WRITE = 2


data class ToDoItem constructor(
        var title: String = "thing to do",
        var tagString: String = "home",
        var preceding: String = EMPTY_ITEM,
        var succeeding: String = EMPTY_ITEM,
        var reward: Int = 1,
        var isDone: Boolean = false,
        var isRoutine: Boolean = false,
        var hasStartLine: Boolean = true,
        var startLine: String = "----/--/--",
        var hasDeadLine: Boolean = false,
        var deadLine: String = "----/--/--"
        )
class FilteredToDoItem constructor(
        var unFilter: Int = 0,
        var item: ToDoItem = ToDoItem()
)


fun saveIntToPreference(_key: String, _int: Int, _context: Context) {
        val preferenceEditor = _context.getSharedPreferences(_key, Context.MODE_PRIVATE).edit()
        preferenceEditor.putInt(_key, _int)
        preferenceEditor.apply()
    }

fun loadIntFromPreference(_key: String, _context: Context): Int {
        val preferences = _context.getSharedPreferences(_key, Context.MODE_PRIVATE)
        return preferences?.getInt(_key, 0) ?: 0
}

fun buildPeriodTextFromItem(item: ToDoItem): String {
    val stringBuilder = if (item.hasStartLine) {
        StringBuilder(item.startLine + "～")
    } else {
        StringBuilder("～")
    }
    if (item.hasDeadLine) stringBuilder.append(item.deadLine)
    return stringBuilder.toString()
}
fun convertTextListToItems(_lines: List<String>): MutableList<ToDoItem> {
    val titleAndTagMatcher = "^(.+?),(.+?)(,.*)".toRegex()
    val result = mutableListOf<ToDoItem>()
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
fun getTagListFromItemList(_list: MutableList<FilteredToDoItem>): MutableList<String> {
    val rawTagList: List<String> = List(_list.size) { index -> _list[index].item.tagString }
    val result = rawTagList.distinct()
    return result.toMutableList()
}
fun makeDefaultList(_context: Context): MutableList<ToDoItem> {
    val res = _context.resources
    val defaultItemTitle = res.getStringArray(R.array.default_todoItem_title)
    val defaultItemTag = res.getStringArray(R.array.default_todoItem_tag)
    val toDoList = List(defaultItemTitle.size - 1) { index ->
        ToDoItem(title = defaultItemTitle[index], tagString = defaultItemTag[index])
    }
    return toDoList.toMutableList()
}
fun makeItemToOneLineText(toDoItem: ToDoItem): String {
    val sb = StringBuilder(toDoItem.title)
            .append(",", toDoItem.tagString, ",")
    if ((toDoItem.preceding != EMPTY_ITEM) and (toDoItem.preceding != "")) {
        sb.append("preceding:", toDoItem.preceding, ",")
    }
    if ((toDoItem.succeeding != EMPTY_ITEM) and (toDoItem.succeeding != "")) {
        sb.append("succeeding:", toDoItem.succeeding, ",")
    }
    val periodText = buildPeriodTextFromItem(toDoItem)
    sb.append(periodText)
    sb.append(",reward:", toDoItem.reward)
    if((toDoItem.isDone)) sb.append(",isDone,")
    return sb.toString()
}
fun makeListToCSV(_list: List<String>): String {
    val sb = StringBuffer()
    _list.joinTo(sb)
    return sb.toString()
}
fun subPropertyExtract(_toDoItem: ToDoItem, _text: String): ToDoItem {
    val precedingMatch = Regex("(,preceding:)(.+?)([,.*\n])").find(_text) // preceding は　preceding: .... の形式
    precedingMatch?.destructured?.let { (_, data, _) -> _toDoItem.preceding = data }
    val succeedingMatch = Regex("(,succeeding:)(.+?)([,.*\n])").find(_text) // preceding は　succeeding: .... の形式
    succeedingMatch?.destructured?.let { (_, data, _) -> _toDoItem.succeeding = data }
    val isDoneMatch = Regex(",isDone,").find(_text)
    isDoneMatch?.let { _toDoItem.isDone = true}

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
} // ToDoItem[] から1行のテキストへ