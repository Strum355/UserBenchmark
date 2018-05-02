package xyz.noahsc.userbenchmark.data

import android.app.Activity
import android.os.Parcel
import android.os.Parcelable
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.apache.commons.text.similarity.FuzzyScore
import kotlin.collections.ArrayList

interface Hardware : Comparable<Hardware>, Parcelable {
    val benchmark: Float
    val rank:    Int
    val samples: Int
    val url:   String
    val part:  String
    val brand: String
    val model: String

    companion object {
        fun stuff() {
            
        }
    }

    override fun compareTo(other: Hardware) = compareValuesBy(this, other, { it.rank })
}

//TODO optimize in-place
fun searchForSubstring(r: ArrayList<Hardware>, s: String): ArrayList<Hardware> {
    if (s == "") {
        return r
    }

    val out = r.filter {
        FuzzySearch.partialRatio(s, "${it.brand} ${it.model}") > 80
    } as ArrayList<Hardware>

    if(out.isEmpty()) {
        out.add(object : Hardware{
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

            override fun writeToParcel(p0: Parcel?, p1: Int) {}
        })
    }
    return out
}