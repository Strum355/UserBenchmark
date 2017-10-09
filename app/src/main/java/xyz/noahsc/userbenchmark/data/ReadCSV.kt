package xyz.noahsc.userbenchmark.data

import android.content.Context
import android.util.Log
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import java.io.*

fun readCSV(path: String, ctx: Context): ArrayList<HardwareData> {
    val rows: ArrayList<HardwareData> = ArrayList()

    try {
        val assetManager = ctx.assets
        val input = assetManager.open(path)
        val parser = CSVParserBuilder().withSeparator(","[0]).withIgnoreQuotations(true).build()
        val reader = CSVReaderBuilder(InputStreamReader(input)).withSkipLines(1).withCSVParser(parser).build()
        while (true) {
            val column = reader.readNext() ?: break
            if (column.size < 8 ){
                continue
            }
            val temp = HardwareData(column[1], column[2], column[3], column[4].toInt(), column[5].toFloat(), column[6].toInt(), column[7])
            rows.add(temp)
        }
        return rows
    } catch (e: Exception) {
        rows.clear()
        rows.add(HardwareData("", "", "Error", 0, 0F, 0, ""))
    }

    return rows
}