package com.example.yoshi.viewpagertodo1

import android.content.Context
import android.content.Context.MODE_APPEND
import android.content.Context.MODE_PRIVATE
import android.os.Environment
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.json.JSON
import kotlinx.serialization.serializer
import java.io.*


const val ITEM_DATA = "toDoItems"
const val EARNED_POINT = "earnedPoint"
const val EMPTY_ITEM = "empty item"
const val TODO_TEXT_FILE = "toDoItems.txt"

@Serializable
data class ToDoItem constructor(
        var title: String = "thing to do",
        var reward: Int = 1,
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

    // manage ItemList
    fun makeDefaultList(_context: Context): MutableList<ToDoItem> {
        val res = _context.resources
        val defaultItemTitle = res.getStringArray(R.array.default_todoItem_title)
        val defaultItemStartDate = res.getStringArray(R.array.default_todoItem_startDate)
        val defaultItemTag = res.getStringArray(R.array.default_todoItem_tag)
        val toDoList = List(defaultItemTitle.size - 1) { index ->
            ToDoItem(title = defaultItemTitle[index], reward = 1,
                    isDone = false, hasStartLine = true, startLine = defaultItemStartDate[index], hasDeadLine = false, tagString = defaultItemTag[index])
        }
        return toDoList.toMutableList()
    }
    fun getTagListFromItemList(_list: MutableList<ToDoItem>): MutableList<String> {
        val rawTagList: List<String> = List(_list.size) { index -> _list[index].tagString }
        val result = rawTagList.distinct()
        return result.toMutableList()
    }

    // ToDoItem with textFile data/data/...
    fun saveListToTextFile(context: Context, _list: MutableList<ToDoItem>) {
        try {
            val fileOut = context.openFileOutput(TODO_TEXT_FILE, MODE_PRIVATE and MODE_APPEND)
            val osw = OutputStreamWriter(fileOut, "UTF-8")
            val bw = BufferedWriter(osw)
            var sb = StringBuilder()
            for (index in _list.indices) {
                bw.write(makeToDoItemToOneLineText(_list[index]))
                bw.newLine()
            }
            bw.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadListFromTextFile(context: Context): MutableList<ToDoItem> {

        val titleAndTagMatcher = "^(.+?),(.+?)(,.*)".toRegex()
        val allLine = readAllLineOfTextFile(context)

        var result = mutableListOf<ToDoItem>()
        for (i in allLine.indices) {
            var line = allLine[i]
            titleAndTagMatcher.matchEntire(line)
                    ?.destructured
                    ?.let { (titleStr, tag, subProperty) ->
                        val newItem = subPropertyExtractFromText(subProperty, ToDoItem(title = titleStr, tagString = tag))
                        result.add(newItem)
                    }
        }
        return result
    }

    fun loadListFromTextFileAtSdcard(_context: Context): MutableList<ToDoItem> {
        val titleAndTagMatcher = "^(.+?),(.+?)(,.*)".toRegex()
        val result: MutableList<ToDoItem> = mutableListOf()
        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath) // Download path

        if (isDirectoryFileContain(directory.listFiles())) {
            val filePath = StringBuilder(directory.absolutePath)
                    .append(TODO_TEXT_FILE).toString()
            val isr = InputStreamReader(_context.openFileInput(filePath))
            val br = BufferedReader(isr)
            val AllLine = br.readLines()
            for (i in AllLine.indices) {
                var line = AllLine[i]
                titleAndTagMatcher.matchEntire(line)
                        ?.destructured
                        ?.let { (titleStr, tag, subProperty) ->
                            val newItem = subPropertyExtractFromText(subProperty, ToDoItem(title = titleStr, tagString = tag))
                            result.add(newItem)
                        }
            }
            return result
        } else {
            throw java.lang.Exception(FileNotFoundException())
        }


    }

    fun isDirectoryFileContain(_fileList: Array<File>): Boolean {
        var foundFlag = false
        for (index in _fileList.indices) {
            if (_fileList[index].name == TODO_TEXT_FILE) {
                foundFlag = true
            }
        }
        return foundFlag
    }

    fun subPropertyExtractFromText(_text: String, _toDoItem: ToDoItem): ToDoItem {

        val startDateMatch = Regex("/d{4}///d{1,2}///d{1,2}～ ").find(_text)
        if (startDateMatch != null) {
            _toDoItem.hasStartLine = true
            _toDoItem.startLine = (Regex("/d{4}///d{1,2}///d{1,2}").find(startDateMatch.value))?.value ?: "1970/01/01"
        }
        val deadDateMatch = Regex(" ～/d{4}///d{1,2}//d{1,2} ").find(_text)
        if (deadDateMatch != null) {
            _toDoItem.hasDeadLine = true
            _toDoItem.deadLine = (Regex(" /d{4}///d{1,2}///d{1,2}").find(deadDateMatch.value))?.value ?: "1970/01/01"
        }
        val rewardMatch = Regex(",reward:/d").find(_text)
        if (rewardMatch != null) {
            _toDoItem.reward = (Regex("/d").find(rewardMatch.value))?.value?.toInt() ?: 1
        }
        return _toDoItem
    }

    fun readAllLineOfTextFile(context: Context): List<String> {
        return try {
            val fileIn = context.openFileInput(TODO_TEXT_FILE)
            val isr = InputStreamReader(fileIn)
            val br = BufferedReader(isr)
            val result = br.readLines()
            br.close()
            result
        } catch (e: FileNotFoundException) {
            Log.w("test", "File not found")
            emptyList()
        } catch (e: IOException) {
            Log.w("test", "File IO Exception occur")
            emptyList()
        }
    }

// ToDoItem[] から1行のテキストへ

    fun makeToDoItemToOneLineText(toDoItem: ToDoItem): String {
        val sb = StringBuilder(toDoItem.title)
                .append(",", toDoItem.tagString, ",")
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

    // MutableList
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

