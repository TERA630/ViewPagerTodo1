package com.example.yoshi.viewpagertodo1

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class GetIdTest : Throwable() {


    private fun getListFromContext(): MutableList<ToDoItem> {
        val mockedContext = RuntimeEnvironment.systemContext

        return makeDefaultList(mockedContext)
    }


}
