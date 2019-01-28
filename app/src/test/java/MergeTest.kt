package com.example.yoshi.viewpagertodo1

import org.junit.Test
import org.junit.experimental.theories.Theories
import org.junit.runner.RunWith


@RunWith(Theories::class)
class MergeTest : Throwable() {

    val testData1 = listOf<caraProfile>(
            caraProfile("Tom", "1978/6/30"),
            caraProfile("Jerry", "1978/6/1"),
            caraProfile("Jibril", "1990/8/10"),
            caraProfile("Gil", "1990/5/18"),
            caraProfile("Iven", "1985/12/5"),
            caraProfile("Rasl", "1982/6/15"),
            caraProfile("Arma", "1983/12/11"),
            caraProfile("Abdo", "1990/11/8"),
            caraProfile("Hanuman", "1991/3/13"),
            caraProfile("Mabsuna", "1991/1/3"),
            caraProfile("Kuroe", "1993/2/4"),
            caraProfile("Hakim", "1994/12/8")

    )
    val testData2 = listOf<caraProfile>(
            caraProfile("Tom", "1978/6/30"),
            caraProfile("Jerry", "1978/6/1"),
            caraProfile("Jibril", "1990/8/10"),
            caraProfile("Gil", "1990/5/18"),
            caraProfile("Hakim", "1994/12/8")

    )

    @Test
    fun mergeTest() {
        mergeItemTest(testData1.toMutableList(), testData2.toMutableList())
    }


    fun mergeItemTest(oneItems: MutableList<caraProfile>, otherItems: MutableList<caraProfile>): MutableList<caraProfile> {

        val resultItem = emptyList<caraProfile>().toMutableList()

        val workItems = MutableList(oneItems.size) { index -> oneItems[index] }


        val duplicateItem: MutableMap<String, String> = emptyMap<String, String>().toMutableMap()

        workItems.addAll(otherItems)
        workItems.sortBy { it.name }
        for (i in workItems.indices) {
            var isDuplicated = false
            for (j in i + 1..workItems.lastIndex) {
                if (workItems[i].name == workItems[j].name) {
                    isDuplicated = true
                    if (duplicateItem.containsKey(workItems[j].name)) {
                        duplicateItem[workItems[j].name] += ",$j:${workItems[j].date}"
                    } else {
                        duplicateItem[workItems[j].name] = "$i:${workItems[i].date},$j:${workItems[j].date}"
                    }
                    //  [title] =  index:date, index: date....
                }
            }
            if (isDuplicated == false) {
                //　タイトルの重複がないものは結果に追加OK
                resultItem.add(workItems[i])
            }
        }
        return resultItem
    }

}

class caraProfile(
        val name: String = "default",
        val date: String = "1970/1/1")