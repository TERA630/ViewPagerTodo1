package com.example.yoshi.viewpagertodo1

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GetIdTest : Throwable() {
    val titleList = listOf("クランチ"
            , "フロントプランク", "バイシクルクランチ", "ニートゥチェスト", "リバースクランチ"
            , "ドラゴンフラッグ", "プッシュアップ", "ヒップスラスト", "ピークタッチ", "スクワット", "ゴーバック"
            , "ニーレイズ", "レッグレイズ", "グッドモーニング", "ランジ", "サイドプランク")
    private val testText1 = "売却積み込み,整理,15533434343,15533434343,～,reward:1,memo:便座、ストライダー、フィットビット、靴乾燥機、体重計"
    private val testText2 = "内視鏡ESD,手術,12345,12345,～,reward:1"

    var itemList: MutableList<ToDoItem> = emptyList<ToDoItem>().toMutableList()

    /*   private fun getListFromContext(): MutableList<ToDoItem> {
           val mockedContext = RuntimeEnvironment.systemContext
           return makeDefaultList(mockedContext)
       }*/

    @Test
    fun textItemConvertTest() {
        val testRawItem = ToDoItem("売却積み込み", "整理", 15533434343, 15533434343, memo = "便座、ストライダー、フィットビット、靴乾燥機、体重計")
        val testDecodedItem = makeItemToOneLineText(testRawItem)
        val testDecodedItem2 = convertTextListToItems(mutableListOf(testText1, testText2))
        assertThat(testDecodedItem).isEqualTo(testText1)
        assertThat(testDecodedItem2[0]).isEqualTo(testRawItem)
        assertThat(testDecodedItem2[1]).isEqualTo(ToDoItem("内視鏡ESD", "手術", 12345, 12345))

    }
}
