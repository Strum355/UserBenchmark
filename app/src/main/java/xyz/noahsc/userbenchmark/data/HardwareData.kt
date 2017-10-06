package xyz.noahsc.userbenchmark.data

import android.content.Context
import android.support.v7.widget.CardView
import android.widget.TextView

data class HardwareData(val partNum: String,
                        val brand: String,
                        val model: String,
                        val rank: Int,
                        val benchmark: Float,
                        val samples: Int,
                        val url: String)

fun filterDuplicateURLS(r: ArrayList<HardwareData>): ArrayList<HardwareData> {
    val out: ArrayList<HardwareData> = ArrayList()
    for (i in r.indices){
        if (i != 0 && r[i].url == r[i-1].url){
            continue
        }
        out.add(r[i])
    }

    return out
}

