package com.example.yoshi.viewpagertodo1

import java.util.*

fun isValidAsDate(_string: String): Boolean {
    val validCheckEx = Regex("""([12]\d{3})/(\d|1[012])/(\d|[12][0-9]|3[01])""")
    return validCheckEx.matches(_string)
}

fun isAfterByDate(targetDateStr: String, baseDateStr: String): Boolean {
    val dateRegEx = Regex("""([12]\d{3})/(\d|1[012])/(\d|[12][0-9]|3[01]).*""")
    if (!((dateRegEx.matches(targetDateStr)) && (dateRegEx.matches(baseDateStr)))) {
        throw Exception("your $targetDateStr or $baseDateStr illegal dateString")
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
    val (targetHour, targetMinute, targeSecond) = targetTimeMatch?.destructured
            ?: throw IllegalStateException("input was illegal at isAfter")

    val baseTimeMatch = timeRegEx.find(baseTime)
    val (baseHour, baseMinute, baseSecond) = baseTimeMatch!!.destructured

    when {
        (targetHour > baseHour) -> return true  // ターゲット時刻(前の引数)が基準時間より後
        (targetHour < baseHour) -> return false // ターゲット時間(前の引数)が基準時間より前
        (targetHour == baseHour) -> {
            when {
                (targetMinute > baseMinute) -> return true
                (targetMinute < baseMinute) -> return false
                (targetMinute == baseMinute) -> {
                    when {
                        (targeSecond > baseSecond) -> return true
                        (targeSecond <= baseSecond) -> return false
                    }
                }
            }
        }
    }
    return false
}

fun isBefore(itemDate: String, baseDateStr: String): Boolean {
    val dateRegEx = Regex("""([12]\d{3})/(1[012]|\d)/(3[01]|[12]\d|\d)""")
    if (!((dateRegEx.matches(itemDate)) && (dateRegEx.matches(baseDateStr)))) {
        throw Exception("$itemDate or $baseDateStr illegal dateString")
    }
    val matchingTargetDate = dateRegEx.find(itemDate)
    val (iYear, iMonth, iDay) = matchingTargetDate!!.destructured
    val itemYear = iYear.toInt()
    val itemMonth = iMonth.toInt()
    val itemDay = iDay.toInt()
    val matchingBaseDate = dateRegEx.find(baseDateStr)
    val (bYear, bMonth, bDay) = matchingBaseDate!!.destructured
    val baseYear = bYear.toInt()
    val baseMonth = bMonth.toInt()
    val baseDay = bDay.toInt()
    when {
        (itemYear < baseYear) -> return true
        (itemYear > baseYear) -> return false
        (itemYear == baseYear) -> {
            when {
                (itemMonth < baseMonth) -> return true
                (itemMonth > baseMonth) -> return false
                (itemMonth == baseMonth) -> {
                    when {
                        (itemDay <= baseDay) -> return true
                        (itemDay > baseDay) -> return false
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

fun getCurrentTime(): String {
    var string = getToday()
    val cal = Calendar.getInstance(Locale.JAPAN)
    string += "/${cal.get(Calendar.HOUR)}/${cal.get(Calendar.MINUTE)}/${cal.get(Calendar.SECOND)}"
    return string
}