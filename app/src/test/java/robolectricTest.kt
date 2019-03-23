package com.example.yoshi.viewpagertodo1

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GetIdTest : Throwable() {
    private val titleList = listOf("クランチ"
            , "フロントプランク", "バイシクルクランチ", "ニートゥチェスト", "リバースクランチ"
            , "ドラゴンフラッグ", "プッシュアップ", "ヒップスラスト", "ピークタッチ", "スクワット", "ゴーバック"
            , "ニーレイズ", "レッグレイズ", "グッドモーニング", "ランジ", "サイドプランク")
    private val testText1 = "売却積み込み,整理,15533434343,155334343459,memo:便座、ストライダー、フィットビット、靴乾燥機、体重計"
    private val testText2 = "内視鏡指導医申請,学会,12345678,12345678"

    var itemList: MutableList<ToDoItem> = emptyList<ToDoItem>().toMutableList()

    /*   private fun getListFromContext(): MutableList<ToDoItem> {
           val mockedContext = RuntimeEnvironment.systemContext
           return makeDefaultList(mockedContext)
       }*/
    @Test
    fun getIdTest() {
        for (i in titleList.indices) {
            val currentTime = System.currentTimeMillis()
            if ((itemList.firstOrNull { it.itemID == currentTime }) == null) {
                val newItem = ToDoItem(title = titleList[i], tagString = "運動", itemID = currentTime, upDatetime = currentTime)
                itemList.add(newItem)
            }
        }
        assertThat(itemList).doesNotHaveDuplicates()
    }

    @Test
    fun textItemConvertTest() {
        val testrawItem = ToDoItem("売却積み込み", "整理", 15533434343, 15533434343, memo = "便座、ストライダー、フィットビット、靴乾燥機、体重計")
        val testDecodedItem = makeItemToOneLineText(testrawItem)
        val testDecodedItem2 = convertTextListToItems(mutableListOf(testText1, testText2))
        assertThat(testDecodedItem).isEqualTo(testText1)
        assertThat(testDecodedItem2[0]).isEqualTo(testrawItem)

    }
}
