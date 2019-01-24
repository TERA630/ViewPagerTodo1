package com.example.yoshi.viewpagertodo1

import android.content.Context
import android.util.Log
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.assertj.core.api.Assertions.*
import java.io.BufferedWriter
import java.io.OutputStreamWriter

@RunWith(RobolectricTestRunner::class)
class SerializersTest : Throwable() {


    private fun getListFromContext(): MutableList<ToDoItem> {
        val mockedContext = RuntimeEnvironment.systemContext
        return makeDefaultList(mockedContext)
    }



    @Test
    fun mergeTest() {
        val oneItem = loadListFromTextFile(RuntimeEnvironment.systemContext)
        val twoItem = getListFromContext()
        val result1 = mergeItem(oneItem, twoItem)
        val result2 = MutableList(oneItem.size){ index-> oneItem[index].copy()}
        result2.addAll(twoItem)

        assertThat(result1).isIn(result2)
        assertThat(oneItem).isIn(result1)
        assertThat(twoItem).isIn(result1)

        saveListToFileAs("test_result1.txt",RuntimeEnvironment.systemContext,result1)
        saveListToFileAs("test_result2.txt",RuntimeEnvironment.systemContext,result2)

    }

    fun convertItemsToMultiLines(_list: MutableList<ToDoItem>): String {
        val sb = StringBuilder()
        for (i in _list.indices) {
            val line = makeItemToOneLineText(_list[i])
            sb.append(line)
        }
        return sb.toString()
    }

    fun saveListToFileAs(_name:String,context: Context, _list: MutableList<ToDoItem>) {
        try {
            val fileOut = context.openFileOutput(_name, Context.MODE_PRIVATE and Context.MODE_APPEND)
            val osw = OutputStreamWriter(fileOut, "UTF-8")
            val bw = BufferedWriter(osw)
            for (index in _list.indices) {
                bw.write(makeItemToOneLineText(_list[index]))
                bw.newLine()
            }
            bw.close()
        } catch (e: Exception) {
            Log.e("test", "${e.message} occur by ${e.cause} at saveListToFileAs")
            e.printStackTrace()
        }
    }

}


