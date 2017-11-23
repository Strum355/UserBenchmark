package xyz.noahsc.userbenchmark.data

import android.os.Parcel
import android.os.Parcelable
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

fun filterDuplicateUrls(r: ArrayList<Hardware>) {
    //TODO use stdlib. Thought it didnt work but we'll try again
    for(i in r.size-1 downTo 1){
        if(r[i].url == r[i-1].url){
            r.removeAt(i)
        }
    }
}

//TODO optimize in-place
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

            override fun describeContents() = 0

            override fun writeToParcel(p0: Parcel?, p1: Int) {
            }
        })
    }
    return out
}






