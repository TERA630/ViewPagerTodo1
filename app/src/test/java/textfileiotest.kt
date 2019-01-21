package com.example.yoshi.viewpagertodo1

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
    fun mergeTest() {
        val oneItem = loadListFromTextFile(RuntimeEnvironment.systemContext)
        val twoItem = getListFromContext()
        val result1 = mergeItems(oneItem, twoItem)
        val result2 = oneItem.addAll(twoItem)
        assert(false, )

    }

    fun convertItemsToMultiLines(_list: MutableList<ToDoItem>): String {
        val sb = StringBuilder()
        for (i in _list.indices) {
            val line = makeItemToOneLineText(_list[i])
            sb.append(line)
        }
        return sb.toString()
    }

}


