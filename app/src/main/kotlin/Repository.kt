package com.example.yoshi.viewpagertodo1

import android.content.Context

const val REWARD = "reward"
const val EMPTY_ITEM = "empty item"
const val TODO_TEXT_FILE = "toDoItems.txt"

data class ToDoItem constructor(
        var title: String = "thing to do",
        var tagString: String = "home",
        var reward: Int = 1,
        var isDone: Boolean = false,
        var hasStartLine: Boolean = true,
        var startLine: String = "----/--/--",
        var hasDeadLine: Boolean = false,
        var deadLine: String = "----/--/--",
        var memo: String = EMPTY_ITEM,
        var upDatetime: Long = 19700101000000L
        )

class FilteredToDoItem constructor(
        var unFilter: Int = 0,
        var item: ToDoItem = ToDoItem()
)

fun buildPeriodText(item: ToDoItem): String {
    val stringBuilder =
            if (item.hasStartLine) StringBuilder(item.startLine + "～")
            else StringBuilder("～")
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
fun loadIntFromPreference(_key: String, _context: Context): Int {
    val preferences = _context.getSharedPreferences(_key, Context.MODE_PRIVATE)
    return preferences?.getInt(_key, 0) ?: 0
}
fun loadStringFromPreference(_key: String, _context: Context): String? {
    val preferences = _context.getSharedPreferences(_key, Context.MODE_PRIVATE)
    return preferences?.getString(_key, null)
}

fun makeDefaultList(_context: Context): MutableList<ToDoItem> {
    val res = _context.resources
    val defaultItemTitle = res.getStringArray(R.array.default_todoItem_title)
    val defaultItemTag = res.getStringArray(R.array.default_todoItem_tag)
    val toDoList = List(defaultItemTitle.size - 1) { index ->
        ToDoItem(title = defaultItemTitle[index], tagString = defaultItemTag[index], hasStartLine = true, startLine = getToday(), upDatetime = getCurrentTime())
    }
    return toDoList.toMutableList()
}
fun makeItemToOneLineText(toDoItem: ToDoItem): String {
    val sb = StringBuilder(toDoItem.title)
            .append(",", toDoItem.tagString, ",")

    val periodText = buildPeriodText(toDoItem)
    sb.append(periodText)
    sb.append(",reward:", toDoItem.reward)

    if ((toDoItem.isDone)) sb.append(",isDone")
    if ((toDoItem.memo) != EMPTY_ITEM) sb.append(",memo:", toDoItem.memo)

    return sb.toString()
}
fun makeListToCSV(_list: List<String>): String {
    val sb = StringBuffer()
    _list.joinTo(sb)
    return sb.toString()
}

fun mergeItem(oneItems: MutableList<ToDoItem>, otherItems: MutableList<ToDoItem>): MutableList<ToDoItem> {

    val resultItem = MutableList(0) { ToDoItem() }
    val workItems = MutableList(oneItems.size) { index -> oneItems[index].copy() }
    val duplicateItemTitle = emptyList<ToDoItem>().toMutableList()

    workItems.addAll(otherItems)
    workItems.sortBy { it.title }

    for (i in workItems.indices) {
        var isDuplicated = false
        val itemHasSameTitle = workItems.filter { it.title == workItems[i].title }
        if (itemHasSameTitle.size == 1) {      //　タイトルの重複がないものは結果にそのまま追加
            resultItem.add(workItems[i])
        } else {
            duplicateItemTitle.add(workItems[i].copy())
            resultItem.add(getNewestItem(duplicateItemTitle))
        }
    }
    return resultItem
}

fun getNewestItem(_list: MutableList<ToDoItem>): ToDoItem {
    _list.sortBy { it.upDatetime }
    return _list[0]
}
fun saveIntToPreference(_key: String, _int: Int, _context: Context) {
    val preferenceEditor = _context.getSharedPreferences(_key, Context.MODE_PRIVATE).edit()
    preferenceEditor.putInt(_key, _int)
    preferenceEditor.apply()
}

fun saveStringToPreference(_key: String, _string: String, _context: Context) {
    val preferenceEditor = _context.getSharedPreferences(_key, Context.MODE_PRIVATE).edit()
    preferenceEditor.putString(_key, _string).apply()
}

fun subPropertyExtract(_toDoItem: ToDoItem, _text: String): ToDoItem {
    val memoMatch = Regex("(,memo:)(.+?)").find(_text)
    memoMatch?.destructured?.let { (_, data) -> _toDoItem.memo = data }

    val isDoneMatch = Regex(",isDone,").find(_text)
    isDoneMatch?.let { _toDoItem.isDone = true}

    val startDateMatch = Regex(",[0-9]{4}/[0-9]{1,2}/[0-9]{1,2}～").find(_text)
    if (startDateMatch != null) {
        _toDoItem.hasStartLine = true
        _toDoItem.startLine = (Regex("[0-9]{4}/[0-9]{1,2}/[0-9]{1,2}").find(startDateMatch.value))?.value ?: "1970/01/01"
    }
    val deadDateMatch = Regex(" ～[0-9]{4}/[0-9]{1,2}/[0-9]{1,2} ").find(_text)
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