package com.example.yoshi.viewpagertodo1

import android.content.Context
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import java.io.*

fun loadListFromTextFile(_context: Context): MutableList<ToDoItem> {
    val allLine = inputStreamToLines(_context.openFileInput(TODO_TEXT_FILE))
    return convertTextLineIntoItems(_lines = allLine)
}
fun loadListFromTextFileAtSdcard(_context: Context, _documentDir: DocumentFile): MutableList<ToDoItem> {
    val file = _documentDir.findFile(TODO_TEXT_FILE)
        file?.let {
            val inputStream = _context.contentResolver.openInputStream(file.uri)
            inputStream?.let {
                val lines = inputStreamToLines(inputStream)
                return convertTextLineIntoItems(lines)
            } ?: throw IOException("$TODO_TEXT_FILE inputStream could not established")
        } ?: throw FileNotFoundException("$TODO_TEXT_FILE was not found")
}

fun inputStreamToLines(_inputStream: java.io.InputStream): List<String> {
    return try {
        val isr = InputStreamReader(_inputStream)
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
            bw.write(makeItemsToOneLineText(_list[index]))
            bw.newLine()
        }
        bw.close()
    } catch (e: FileNotFoundException) {
        Log.e("test", "File not found at saveListToTextFile")
        e.printStackTrace()
    } catch (e: IOException) {
        Log.e("test", "IOException occur at saveListToTextFile")
        e.printStackTrace()
    }
}