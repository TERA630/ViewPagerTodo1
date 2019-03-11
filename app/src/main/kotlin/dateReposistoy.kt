package com.example.yoshi.viewpagertodo1

import java.util.*

fun isAfterByDate(targetDateStr: String, baseDateStr: String): Boolean {
    val dateRegEx = Regex("""([12]\d{3})/(\d|1[012])/(\d|[12][0-9]|3[01])(.*)""")

    if (!((dateRegEx.matches(targetDateStr)) && (dateRegEx.matches(baseDateStr)))) {
        throw IllegalArgumentException("your $targetDateStr or $baseDateStr illegal dateString")
    }
    val matchingTargetDate = dateRegEx.find(targetDateStr)
    val (targetYear, targetMonth, targetDay) = matchingTargetDate!!.destructured
    val matchingBaseDate = dateRegEx.find(baseDateStr)
    val (baseYear, baseMonth, baseDay) = matchingBaseDate!!.destructured
    when {
        (targetYear > baseYear) -> return true
        (targetYear < baseYear) -> return false
        (targetYear == baseYear) -> {
            when {
                (targetMonth > baseMonth) -> return true
                (targetMonth < baseMonth) -> return false
                (targetMonth == baseMonth) -> {
                    when {
                        (targetDay > baseDay) -> return true
                        (targetDay == baseDay) -> return isAfterByClock(targetDateStr, baseDateStr)
                        (targetDay < baseDay) -> return false
                    }
                }
            }
        }
    }
    return false
}

fun isAfterByClock(targetTime: String, baseTime: String): Boolean {

    val timeRegEx = Regex("""([01][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])""")
    val targetTimeMatch = timeRegEx.find(targetTime)
    val (targetHour, targetMinute, targetSecond) = targetTimeMatch?.destructured
            ?: throw IllegalStateException("input was illegal at isAfter")

    val baseTimeMatch = timeRegEx.find(baseTime)
    val (baseHour, baseMinute, baseSecond) = baseTimeMatch?.destructured
            ?: throw IllegalStateException("input was illegal at isAfter")

    when {
        (targetHour > baseHour) -> return true  // ターゲット時刻(前の引数)が基準時間より後
        (targetHour < baseHour) -> return false // ターゲット時間(前の引数)が基準時間より前
        (targetHour == baseHour) -> {
            when {
                (targetMinute > baseMinute) -> return true
                (targetMinute < baseMinute) -> return false
                (targetMinute == baseMinute) -> {
                    when {
                        (targetSecond > baseSecond) -> return true
                        (targetSecond <= baseSecond) -> return false
                    }
                }
            }
        }
    }
    return false
}

fun getToday(): String {
    val cal = Calendar.getInstance(Locale.JAPAN)
    return "${cal.get(Calendar.YEAR)}/${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.DAY_OF_MONTH)}"
}

fun makeIDFromDate(): Long {
    val cal = Calendar.getInstance(Locale.JAPAN)
    val month = cal.get(Calendar.MONTH + 1)
    val day = cal.get(Calendar.DAY_OF_MONTH)
    val hour = cal.get(Calendar.HOUR)
    val minute = cal.get(Calendar.MINUTE)
    val second = cal.get(Calendar.SECOND)
    val id = System.currentTimeMillis()

    return id
}

