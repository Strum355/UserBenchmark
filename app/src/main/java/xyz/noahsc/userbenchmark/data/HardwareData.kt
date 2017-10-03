package xyz.noahsc.userbenchmark.data

import android.content.Context
import android.support.v7.widget.CardView
import android.widget.TextView

data class HardwareData(val partNum: String,
                        val brand: String,
                        val model: String,
                        val rank: Int,
                        val benchmark: Float,
                        val samples: Int,
                        val url: String)

fun dataToCard(h: HardwareData, ctx: Context): CardView {
    val card = CardView(ctx)
    val partNum = TextView(ctx)
    partNum.text = h.partNum
    for(textView in dataToTextViews(h, ctx)) {
        card.addView(textView)
    }

    return card
}

fun dataToTextViews(h: HardwareData, ctx: Context): Array<TextView> {
    val txt1 = TextView(ctx)
    txt1.text = h.partNum
    val txt2 = TextView(ctx)
    txt2.text = h.brand
    val txt3 = TextView(ctx)
    txt3.text = h.model
    val txt4 = TextView(ctx)
    txt4.text = h.rank.toString()
    val txt5 = TextView(ctx)
    txt5.text = h.benchmark.toString()
    val txt6 = TextView(ctx)
    txt6.text = h.samples.toString()
    val txt7 = TextView(ctx)
    txt7.text = h.url.toString()

    return arrayOf(txt1, txt2, txt3, txt4, txt5, txt6, txt7)
}

