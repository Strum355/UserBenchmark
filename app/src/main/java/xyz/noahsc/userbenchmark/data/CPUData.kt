package xyz.noahsc.userbenchmark.data

import android.os.Parcel
import android.os.Parcelable

data class CPUData(val cores: String,
                   val scores: ArrayList<String>,
                   val performance: ArrayList<String>,

                   override val url: String,
                   override val part: String,
                   override val brand: String,
                   override val rank: Int,
                   override val benchmark: Float,
                   override val samples: Int,
                   override val model: String) : Parcelable, Hardware {

    constructor(source: Parcel) : this(
            source.readString(),
            source.createStringArrayList(),
            source.createStringArrayList(),
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
        writeString(cores)
        writeStringList(scores)
        writeStringList(performance)
        writeString(url)
        writeString(part)
        writeString(brand)
        writeInt(rank)
        writeFloat(benchmark)
        writeInt(samples)
        writeString(model)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<CPUData> = object : Parcelable.Creator<CPUData> {
            override fun createFromParcel(source: Parcel): CPUData = CPUData(source)
            override fun newArray(size: Int): Array<CPUData?> = arrayOfNulls(size)
        }
    }
}