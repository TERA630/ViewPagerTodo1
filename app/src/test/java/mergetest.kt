package com.example.yoshi.viewpagertodo1

import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theory


class MergeTest : Throwable() {

    class Squads(
            val name: String,
            val date: String)

    @DataPoints

    val squadList = listOf(
            Squads("", ""),
            Squads("", ""),
            Squads("", ""),
            Squads("", ""),
            Squads("", "")

    )

    @Theory
    fun mergeCheck() {

    }

}