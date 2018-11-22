package com.example.yoshi.viewpagertodo1

import android.content.Context
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import java.io.*

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

fun loadListFromTextFileAtSdcard(_context: Context, _documentDir: DocumentFile): MutableList<ToDoItem> {
    val titleAndTagMatcher = "^(.+?),(.+?)(,.*)".toRegex()
    val result: MutableList<ToDoItem> = mutableListOf()

    try {
        val file = _documentDir.findFile(TODO_TEXT_FILE)
        file?.let {
            val ins = InputStreamReader(_context.contentResolver.openInputStream(file.uri))
            val br = BufferedReader(ins)
            val allLine = br.readLines()
            br.close()
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
        } ?: throw Exception(FileNotFoundException("$TODO_TEXT_FILE was not found"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return result
}

fun readAllLineOfTextFile(_context: Context): List<String> {
    return try {
        val isr = InputStreamReader(_context.openFileInput(TODO_TEXT_FILE))
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