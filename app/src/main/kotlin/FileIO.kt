package com.example.yoshi.viewpagertodo1

import android.content.Context
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import java.io.*
import kotlin.math.log

fun deleteTextFile(_context: Context) {
    try {
        _context.deleteFile(TODO_TEXT_FILE)
    } catch (e: Exception) {
        Log.e("test", "${e.message} occur")
    }
}
fun inputStreamToLines(_inputStream: java.io.InputStream): List<String> {
    return try {
        val isr = InputStreamReader(_inputStream)
        val br = BufferedReader(isr)
        val result = br.readLines()
        br.close()
        result
    } catch (e: Exception) {
        Log.w("test", "${e.message} at inputStreamToLines")
        emptyList()
    }
}
fun loadListFromTextFile(_context: Context): MutableList<ToDoItem> {
    return try {
        val allLine = inputStreamToLines(_context.openFileInput(TODO_TEXT_FILE))
        convertTextListToItems(_lines = allLine)
    } catch (e: Exception) {
        Log.w("test", "${e.cause} bring {${e.message} at LoadListFromTextFile")
        emptyList<ToDoItem>().toMutableList()
    }
}
fun loadListFromTextFileAtSdcard(_context: Context, _documentDir: DocumentFile): MutableList<ToDoItem> {
    return try {
        val file = _documentDir.findFile(TODO_TEXT_FILE)
                ?: throw FileNotFoundException("$TODO_TEXT_FILE was not found")
        val inputStream = _context.contentResolver.openInputStream(file.uri)
        inputStream?.let {
            val lines = inputStreamToLines(inputStream)
            convertTextListToItems(lines)
        }
                ?: throw IOException("$TODO_TEXT_FILE inputStream could not established as LoadListFromTextFileAtSdcard")

    } catch (e: FileNotFoundException) {
        Log.i("test", "$TODO_TEXT_FILE was not found at sdcard, so load it at internal storage")
        loadListFromTextFile(_context)
    } catch (e: Exception) {
        Log.e("test", "${e.cause} by ${e.cause} loadListFromTextFileAtSdcard")
        emptyList<ToDoItem>().toMutableList()
    }
}

fun saveListToTextFile(context: Context, _list: MutableList<ToDoItem>) {
    try {
        val fileOut = context.openFileOutput(TODO_TEXT_FILE, Context.MODE_PRIVATE and Context.MODE_APPEND)
        val osw = OutputStreamWriter(fileOut, "UTF-8")
        val bw = BufferedWriter(osw)
        for (index in _list.indices) {
            bw.write(makeItemToOneLineText(_list[index]))
            bw.newLine()
        }
        bw.close()
    } catch (e: Exception) {
        Log.e("test", "${e.message} occur by ${e.cause} at saveListToTxtFileAtSdCard")
        e.printStackTrace()
    }
}

fun saveListToTextFileAtSdcard(_context: Context, _documentDir: DocumentFile, _list: MutableList<ToDoItem>) {

    val file = _documentDir.findFile(TODO_TEXT_FILE)
            ?: _documentDir.createFile("text", TODO_TEXT_FILE) ?: throw IOException()
    try {
        val outputStream = _context.contentResolver.openOutputStream(file.uri)
        val osw = OutputStreamWriter(outputStream, "UTF-8")
        val bw = BufferedWriter(osw)
        for (index in _list.indices) {
            bw.write(makeItemToOneLineText(_list[index]))
            bw.newLine()
        }
        bw.close()
    } catch (e: Exception) {
        Log.e("test", "${e.message} occur by ${e.cause} at saveListToTxtFileAtSdCard")
        e.printStackTrace()
    }
}