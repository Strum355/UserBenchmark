package xyz.noahsc.userbenchmark.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class GPUData(val subresults: ArrayList<String>,
                   val averages: ArrayList<String>,

                   override val url: String,
                   override val part: String,
                   override val brand: String,
                   override val rank: Int,
                   override val benchmark: Float,
                   override val samples: Int,
                   override val model: String) : Parcelable, Hardware {

    constructor(source: Parcel) : this(
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
        writeStringList(subresults)
        writeStringList(averages)
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
        val CREATOR: Parcelable.Creator<GPUData> = object : Parcelable.Creator<GPUData> {
            override fun createFromParcel(source: Parcel): GPUData = GPUData(source)
            override fun newArray(size: Int): Array<GPUData?> = arrayOfNulls(size)
        }
    }
}