package xyz.noahsc.userbenchmark.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class GPUData(@SerializedName("subresults") val subresults: Array<String>,
                   @SerializedName("averages") val averages: Array<String>) : Parcelable {
    constructor(source: Parcel) : this(
            source.createStringArray(),
            source.createStringArray()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeStringArray(subresults)
        writeStringArray(averages)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<GPUData> = object : Parcelable.Creator<GPUData> {
            override fun createFromParcel(source: Parcel): GPUData = GPUData(source)
            override fun newArray(size: Int): Array<GPUData?> = arrayOfNulls(size)
        }
    }
}