package xyz.noahsc.userbenchmark.data

import android.content.Context
import au.com.bytecode.opencsv.CSVReader
import android.util.Log
import java.io.*
import kotlinx.coroutines.experimental.*

fun readCSV(path: String, ctx: Context): ArrayList<HardwareData> {
    val rows: ArrayList<HardwareData> = ArrayList()

    try {
        val assetManager = ctx.assets
        val input = assetManager.open(path)
        val reader = CSVReader(InputStreamReader(input))
        reader.readNext()
        while (true) {
            val column = reader.readNext() ?: break
            val temp = HardwareData(column[1], column[2], column[3], column[4].toInt(), column[5].toFloat(), column[6].toInt(), column[7])
            rows.add(temp)
        }
        return rows
    } catch (e: IOException){
        Log.w("UserBenchmark", e)
    }

    return rows
}