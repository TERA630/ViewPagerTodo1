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
    private val testText1 = mutableListOf<String>("売却積み込み,整理,15533434343,155334343459,memo:便座、ストライダー、フィットビット、靴乾燥機、体重計",
        "銀行ログ確認,財務,155533434344,1533334345")

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
            System.out.println(currentTime)

        }
        assertThat(itemList).doesNotHaveDuplicates()
    }

    @Test
    fun textItemConvertTest() {

        val newItem = ToDoItem("売却積み込み","整理",1553434343,1553434343,memo = ":便座、ストライダー、フィットビット、靴乾燥機、体重計")
        val decordedItem = convertTextListToItems(testText1)

        assertThat(decordedItem[0]).isEqualTo(newItem)

        val item =  makeItemToOneLineText(newItem)
        assertThat(newItem).isEqualTo(item)
    }

}
