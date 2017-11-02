package xyz.noahsc.userbenchmark.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class CPUData(@SerializedName("cores") val cores: String,
                   @SerializedName("scores") val scores: ArrayList<String>,
                   @SerializedName("performance") val performance: ArrayList<String>,

                   @SerializedName("url") override val url: String,
                   @SerializedName("part") override val partNum: String,
                   @SerializedName("brand") override val brand: String,
                   @SerializedName("rank") override val rank: Int,
                   @SerializedName("benchmark") override val benchmark: Float,
                   @SerializedName("samples") override val samples: Int,
                   @SerializedName("model") override val model: String) : Parcelable, HardwareData(url, partNum, brand, rank, benchmark, samples, model) {

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
        writeString(partNum)
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