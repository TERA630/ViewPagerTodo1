package com.example.yoshi.viewpagertodo1

import android.content.Context
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import java.io.*

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
    } catch (e: NoSuchFileException) {
        Log.w("test", "File not found at inputStreamToLines")
        emptyList()
    } catch (e: Exception) {
        Log.w("test", "File IO Exception occur at inputStreamToLines")
        emptyList()
    }
}

fun loadListFromTextFile(_context: Context): MutableList<ToDoItem> {
    return try {
        val allLine = inputStreamToLines(_context.openFileInput(TODO_TEXT_FILE))
        convertTextListToItems(_lines = allLine)
    } catch (e: Exception) {
        Log.w("test", "${e.cause} bring {${e.message}")
        emptyList<ToDoItem>().toMutableList()
    }
}
fun loadListFromTextFileAtSdcard(_context: Context, _documentDir: DocumentFile): MutableList<ToDoItem> {
    val file = _documentDir.findFile(TODO_TEXT_FILE)
        file?.let {
            val inputStream = _context.contentResolver.openInputStream(file.uri)
            inputStream?.let {
                val lines = inputStreamToLines(inputStream)
                return convertTextListToItems(lines)
            } ?: throw IOException("$TODO_TEXT_FILE inputStream could not established")
        } ?: throw FileNotFoundException("$TODO_TEXT_FILE was not found")
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
    } catch (e: FileNotFoundException) {
        Log.e("test", "File not found at saveListToTextFile")
        e.printStackTrace()
    } catch (e: IOException) {
        Log.e("test", "IOException occur at saveListToTextFile")
        e.printStackTrace()
    }
}

fun saveListToTextFileAtSdcard(_context: Context){

}