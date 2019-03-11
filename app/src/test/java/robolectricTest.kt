package com.example.yoshi.viewpagertodo1

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GetIdTest : Throwable() {
    var itemList: MutableList<ToDoItem> = emptyList<ToDoItem>().toMutableList()

    /*   private fun getListFromContext(): MutableList<ToDoItem> {
           val mockedContext = RuntimeEnvironment.systemContext
           return makeDefaultList(mockedContext)
       }*/
    @Test
    fun getIdTest() {
        for (i in 0..10) {
            val newItemID = makeIDFromDate()
            if ((itemList.firstOrNull { it.itemID == newItemID }) == null) {
                val newItem = ToDoItem(itemID = newItemID)
                itemList.add(newItem)
            }
        }
        assertThat(itemList).doesNotHaveDuplicates()
    }
}
