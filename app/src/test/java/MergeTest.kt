package com.example.yoshi.viewpagertodo1

import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith


@RunWith(Theories::class)

class caraProfile(
        val name: String,
        val date: String)

class MergeTest : Throwable() {

    @DataPoints

    val testData = listOf<caraProfile>(
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

    @Theory
    fun mergeTest(list1: List<caraProfile>, list2: List<caraProfile>) {


    }


}