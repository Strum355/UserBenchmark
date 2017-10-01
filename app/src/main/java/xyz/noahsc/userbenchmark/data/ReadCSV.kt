package xyz.noahsc.userbenchmark.data

import android.content.Context
import au.com.bytecode.opencsv.CSVReader
import android.util.Log
import java.io.*
import kotlinx.coroutines.experimental.*

fun readCSV(path: String, ctx: Context): String {
    try {
        val assetManager = ctx.assets
        val input = assetManager.open(path)
        val reader = CSVReader(InputStreamReader(input))
        var out = ""
        while (true) {
            val column = reader.readNext() ?: break

            out += column[4]+" "+column[3]+"\n"
        }
        return out
    } catch (e: IOException){
        Log.w("UserBenchmark", e)
    }

    return "Error"
}