package xyz.noahsc.userbenchmark.data

import android.content.Context
import com.opencsv.CSVReader
import android.util.Log
import java.io.*
import kotlinx.coroutines.experimental.*

fun readCSV(path: String, ctx: Context): ArrayList<HardwareData> {
    val rows: ArrayList<HardwareData> = ArrayList()

    try {
        val assetManager = ctx.assets
        val input = assetManager.open(path)
        val reader = CSVReader(InputStreamReader(input), ","[0], "'"[0], 2)
        while (true) {
            val column = reader.readNext() ?: break
            Log.w("UserBenchmarks", column.size.toString())
            if (column.size < 8 ){
                continue
            }
            val temp = HardwareData(column[1], column[2], column[3], column[4].toInt(), column[5].toFloat(), column[6].toInt(), column[7])
            rows.add(temp)
        }
        return rows
    } catch (e: IOException) {
        Log.w("UserBenchmark", e)
    } catch (e: IndexOutOfBoundsException) {
        rows.clear()
        rows.add(HardwareData("","","Error",0,0F,0,""))
    }

    return rows
}