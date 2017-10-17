package xyz.noahsc.userbenchmark.data

import android.util.Log

data class HardwareData(val partNum: String,
                        val brand: String,
                        val model: String,
                        val rank: Int,
                        val benchmark: Float,
                        val samples: Int,
                        val url: String)

fun filterDuplicateURLS(r: ArrayList<HardwareData>): ArrayList<HardwareData> {
    val out: ArrayList<HardwareData> = ArrayList()
    r.indices.forEach {
        if(it == 0 || r[it].url != r[it-1].url){
            out.add(r[it])
        }
    }

    return out
}

fun searchForSubstring(r: ArrayList<HardwareData>, s: String): ArrayList<HardwareData> {
    val out: ArrayList<HardwareData> = ArrayList()
    r.forEach{
        if((it.brand+it.model).contains(s, true)) {
            out.add(it)
        }
    }

    if(out.isEmpty()) {
        out.add(HardwareData("","No Results","", 0, 0F, 0,""))
    }

    return out
}
