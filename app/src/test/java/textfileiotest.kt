package com.example.yoshi.viewpagertodo1

import android.content.Context
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.internal.StringSerializer
import kotlinx.serialization.json.JSON
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment


@RunWith(RobolectricTestRunner::class)
class SerializersTest : Throwable() {


    fun getListFromContext(): MutableList<ToDoItem> {
        val mockedContext = RuntimeEnvironment.systemContext
        return makeDefaultList(mockedContext)
    }

    @Test
    fun stringSerializationTest() {
        // List <String>

        val list = getListFromContext()
        saveListToTextFile(RuntimeEnvironment.systemContext, list)

        val testStringList = listOf("dog", "cat", "bird", "fox")
        val s = StringSerializer
        val ls = ArrayListSerializer(s)
        val serializedStringList = JSON.unquoted.stringify(ls, testStringList)
        assertEquals("[dog,cat,bird,fox]", serializedStringList)
        val deserializedStringList = JSON.unquoted.parse(ls, serializedStringList)
        assertEquals(testStringList, deserializedStringList)
    }

    fun makeDefaultList(_context: Context): MutableList<ToDoItem> {
        val res = _context.resources
        val defaultItemTitle = res.getStringArray(R.array.default_todoItem_title)
        val defaultItemStartDate = res.getStringArray(R.array.default_todoItem_startDate)
        val defaultItemTag = res.getStringArray(R.array.default_todoItem_tag)
        val toDoList = List(defaultItemTitle.size - 1) { index ->
            ToDoItem(title = defaultItemTitle[index], reward = 1.0f,
                    isDone = false, hasStartLine = true, startLine = defaultItemStartDate[index], hasDeadLine = false, tagString = defaultItemTag[index])
        }
        return toDoList.toMutableList()
    }
}

