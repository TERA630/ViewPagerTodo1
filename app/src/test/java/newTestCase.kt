package com.example.yoshi.viewpagertodo1

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test


class testCase : Throwable() {

    val titleList = listOf("クランチ"
            , "フロントプランク", "バイシクルクランチ", "ニートゥチェスト", "リバースクランチ"
            , "ドラゴンフラッグ", "プッシュアップ", "ヒップスラスト", "ピークタッチ", "スクワット", "ゴーバック"
            , "ニーレイズ", "レッグレイズ", "グッドモーニング", "ランジ", "サイドプランク")

    @Test
    fun makeList() {
        val list1 = MutableList(0) { ToDoItem() }
        val numberOfList1 = getRandomInt(15)
        for (i in 0..numberOfList1) {
            val title = getRandomTitle()
            val date = getRandomDate()
            val item = ToDoItem(title, "exercise", upDatetime = date)
            list1.add(item)
        }

        val list2 = MutableList(0) { ToDoItem() }
        val numberOfList2 = getRandomInt(15)

        for (i in 0..numberOfList2) {
            val title = getRandomTitle()
            val date = getRandomDate()
            val item = ToDoItem(title, "exercise", upDatetime = date)
            list2.add(item)
        }

        val list3 = mergeItem(list1, list2)

        assertThat(list3).doesNotHaveDuplicates()

    }

    private fun getRandomTitle(): String {
        val index = (Math.random() * titleList.size).toInt()
        return titleList[index]
    }

    private fun getRandomDate(): Long {

        val year = 1970 + getRandomInt(50)
        val month = getRandomInt(12)
        val day = getRandomInt(31)
        val hour = getRandomInt(24) - 1
        val minute = getRandomInt(60) - 1
        val second = getRandomInt(60) - 1

        val result = year * 10000000000 + month * 100000000 + day * 1000000 + hour * 10000 + minute * 100 + second

        return result
    }

    private fun getRandomInt(limit: Int): Int {
        return (Math.random() * limit).toInt() + 1
    }


}