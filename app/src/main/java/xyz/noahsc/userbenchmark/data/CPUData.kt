package xyz.noahsc.userbenchmark.data

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.synthetic.main.cpu.*
import kotlinx.android.synthetic.main.details_page.*
import xyz.noahsc.userbenchmark.R
import xyz.noahsc.userbenchmark.activity.ProductActivity
import xyz.noahsc.userbenchmark.activity.numberToColor

data class CPUData(val cores: String,
                   val scores: ArrayList<String>,
                   val performance: ArrayList<String>,
                   val subresults: ArrayList<String>,

                   override val url: String,
                   override val part: String,
                   override val brand: String,
                   override val rank: Int,
                   override val benchmark: Float,
                   override val samples: Int,
                   override val model: String) : Parcelable, Hardware {

    override fun applyDetails(prod: ProductActivity) {
        prod.detail_stub.apply {
            layoutResource = R.layout.cpu
            inflate()
        }

        with(this) {
            arrayOf(prod.single_int, prod.single_float, prod.single_mixed, prod.quad_int, prod.quad_float, prod.quad_mixed, prod.multi_int, prod.multi_float, prod.multi_mixed).forEachIndexed { i, v ->
                v.text = subresults[i]
            }

            prod.single_average.apply {
                text = scores[0]
                setBackgroundResource(numberToColor(scores[1]))
            }
            prod.quad_average.apply {
                text = scores[1]
                setBackgroundResource(numberToColor(scores[1]))
            }
            prod.multi_average.apply {
                text = scores[2]
                setBackgroundResource(numberToColor(scores[1]))
            }
        }
    }

    constructor(source: Parcel): this(
            source.readString(),
            source.createStringArrayList(),
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
        writeStringList(subresults)
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