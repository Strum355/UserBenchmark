package xyz.noahsc.userbenchmark.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList

interface Hardware: Comparable<Hardware>, Parcelable {
    val url: String
    val part: String
    val brand: String
    val model: String
    val rank: Int
    val benchmark: Float
    val samples: Int

    override fun compareTo(other: Hardware) = compareValuesBy(this, other, { it.rank })
}

fun filterDuplicateURLS(r: ArrayList<Hardware>): ArrayList<Hardware> {
    val out: ArrayList<Hardware> = ArrayList()
    r.indices.forEach {
        if(it == 0 || r[it].url != r[it-1].url){
            out.add(r[it])
        }
    }
    return out
}

fun searchForSubstring(r: ArrayList<Hardware>, s: String): ArrayList<Hardware> {
    val out: ArrayList<Hardware> = ArrayList()
    r.forEach{
        if((it.brand+it.model).contains(s, true)) {
            out.add(it)
        }
    }

    if(out.isEmpty()) {
        out.add(object :Hardware{
            override val benchmark: Float
                get() = 0F
            override val brand: String
                get() = ""
            override val model: String
                get() = "No Results"
            override val part: String
                get() = ""
            override val rank: Int
                get() = 0
            override val samples: Int
                get() = 0
            override val url: String
                get() = ""

            override fun describeContents(): Int {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun writeToParcel(p0: Parcel?, p1: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    return out
}






