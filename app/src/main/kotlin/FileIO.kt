package com.example.yoshi.viewpagertodo1

import android.content.Context
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import java.io.*


fun isDirectoryFileContain(_fileList: Array<File>): Boolean {
    var foundFlag = false
    for (index in _fileList.indices) {
        if (_fileList[index].name == TODO_TEXT_FILE) {
            foundFlag = true
        }
    }
    return foundFlag
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

fun loadListFromTextFileAtSdcard(_context: Context, dir: DocumentFile): MutableList<ToDoItem> {
    val titleAndTagMatcher = "^(.+?),(.+?)(,.*)".toRegex()
    val result: MutableList<ToDoItem> = mutableListOf()

    try {
        val file = dir.findFile(TODO_TEXT_FILE)
        val ins = InputStreamReader(_context.contentResolver.openInputStream(file?.uri))
        val br = BufferedReader(ins)
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
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return result
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

fun saveListToTextFile(context: Context, _list: MutableList<ToDoItem>) {
    try {
        val fileOut = context.openFileOutput(TODO_TEXT_FILE, Context.MODE_PRIVATE and Context.MODE_APPEND)
        val osw = OutputStreamWriter(fileOut, "UTF-8")
        val bw = BufferedWriter(osw)
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

