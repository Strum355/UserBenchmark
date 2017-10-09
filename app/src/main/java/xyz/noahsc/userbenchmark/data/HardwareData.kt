package xyz.noahsc.userbenchmark.data

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class HardwareData(val partNum: String,
                        val brand: String,
                        val model: String,
                        val rank: Int,
                        val benchmark: Float,
                        val samples: Int,
                        val url: String) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readInt(),
            source.readFloat(),
            source.readInt(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(partNum)
        writeString(brand)
        writeString(model)
        writeInt(rank)
        writeFloat(benchmark)
        writeInt(samples)
        writeString(url)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<HardwareData> = object : Parcelable.Creator<HardwareData> {
            override fun createFromParcel(source: Parcel): HardwareData = HardwareData(source)
            override fun newArray(size: Int): Array<HardwareData?> = arrayOfNulls(size)
        }
    }
}

fun filterDuplicateURLS(r: ArrayList<HardwareData>?): ArrayList<HardwareData> {
    if (r == null) {
        return ArrayList<HardwareData>()
    }
    val out: ArrayList<HardwareData> = ArrayList()
    for (i in r.indices){
        if (i != 0 && r[i].url == r[i-1].url){
            continue
        }
        out.add(r[i])
    }

    return out
}

