package xyz.noahsc.userbenchmark.data

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

open class HardwareData(@SerializedName("url") open val url: String,
                        @SerializedName("part") open val partNum: String,
                        @SerializedName("brand") open val brand: String,
                        @SerializedName("rank") open val rank: Int,
                        @SerializedName("benchmark") open val benchmark: Float,
                        @SerializedName("samples") open val samples: Int,
                        @SerializedName("model") open val model: String)

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
        out.add(HardwareData("", "No Results", "", 0, 0F, 0, ""))
    }

    return out
}






