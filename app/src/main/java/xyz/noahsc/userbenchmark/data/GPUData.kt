package xyz.noahsc.userbenchmark.data

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.synthetic.main.details_page.*
import kotlinx.android.synthetic.main.gpu.*
import xyz.noahsc.userbenchmark.R
import xyz.noahsc.userbenchmark.activity.ProductActivity

data class GPUData(val subresults: ArrayList<String>,
                   val averages: ArrayList<String>,

                   override val url: String,
                   override val part: String,
                   override val brand: String,
                   override val rank: Int,
                   override val benchmark: Float,
                   override val samples: Int,
                   override val model: String) : Parcelable, Hardware {

    override fun applyDetails(prod: ProductActivity) {
        prod.detail_stub.apply {
            layoutResource = R.layout.gpu
            inflate()
        }

        with(this) {
            kotlin.arrayOf(prod.lighting, prod.reflection, prod.parallax, prod.mrender, prod.gravity, prod.splatting).forEachIndexed { i, v ->
                v.text = subresults[i]
            }

            prod.dx9.apply {
                text = this@GPUData.averages[0]
                setBackgroundResource(xyz.noahsc.userbenchmark.activity.numberToColor(this@GPUData.averages[0]))
            }

            prod.dx10.apply {
                text = this@GPUData.averages[1]
                setBackgroundResource(xyz.noahsc.userbenchmark.activity.numberToColor(this@GPUData.averages[1]))
            }
        }
    }

    constructor(source: Parcel) : this (
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