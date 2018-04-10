package gesture.dev.jokr.gesture

/**
 * Created by jokr on 09.02.18..
 */

import java.io.*
import java.util.*

fun main(args: Array<String>) {
    val `in` = Scanner(System.`in`)
    val n = `in`.nextInt()
    val doors = IntArray(n)
    for (doors_i in 0 until n) {
        doors[doors_i] = `in`.nextInt()
    }
    val result = revisedRussianRoulette(doors)
    for (i in result.indices) {
        var ending = ""
        if (i != result.size - 1)
            ending = " "
        print(result[i].toString() + ending)
    }
    println("")


    `in`.close()
}

fun revisedRussianRoulette(doors: IntArray): Array<Int> {
    val max = doors.groupBy { i: Int -> i == 1 }.count()
//    for (var i=0; i<doors.count(); i++) {
//
//    }
    return arrayOf(2, 3)
}
