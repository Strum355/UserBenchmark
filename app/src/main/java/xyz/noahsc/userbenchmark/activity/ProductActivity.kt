package xyz.noahsc.userbenchmark.activity

import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.support.v7.widget.*
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import xyz.noahsc.userbenchmark.data.CPUData
import xyz.noahsc.userbenchmark.data.GPUData
import xyz.noahsc.userbenchmark.data.Hardware
import xyz.noahsc.userbenchmark.R
import kotlinx.android.synthetic.main.details_page.*
import kotlinx.android.synthetic.main.gpu.*

class ProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = intent.getParcelableExtra<Hardware>("data")

        when(data){
            is CPUData -> asCPU(data)
            is GPUData -> asGPU(data)
        }
    }

    private fun asCPU(data: CPUData){
        setContentView(R.layout.details_page)
        findViewById<Toolbar>(R.id.title).apply{ title = "${data.brand} ${data.model}" }

        val urlUnderline = SpannableString("View in Browser!").apply {
            setSpan(UnderlineSpan(), 0, this.length, 0)
        }

        url.apply {
            text = urlUnderline
            onClick {
                browse(data.url)
            }
        }

        rank.text = "${rank.text}${data.rank}"
    }

    private fun asGPU(data: GPUData) {
        setContentView(R.layout.details_page)
        findViewById<Toolbar>(R.id.title).apply{ title = "${data.brand} ${data.model}" }

        val urlUnderline = SpannableString("View in Browser!").apply {
            setSpan(UnderlineSpan(), 0, this.length, 0)
        }

        url.apply {
            text = urlUnderline
            onClick {
                browse(data.url)
            }
        }

        viewStub.apply {
            layoutResource = R.layout.gpu
            inflate()
        }

        rank.text = "${rank.text}${data.rank}"

        lighting.text   = data.subresults[0]
        reflection.text = data.subresults[1]
        parallax.text   = data.subresults[2]
        mrender.text    = data.subresults[3]
        gravity.text    = data.subresults[4]
        splatting.text  = data.subresults[5]

        dx9.apply{
            text = data.averages[0]
            setBackgroundResource(numberToColor(data.averages[0]))
        }
        dx10.apply {
            text = data.averages[1]
            setBackgroundResource(numberToColor(data.averages[1]))
        }
    }
}

fun numberToColor(s: String): Int {
    val num = s.split(delimiters = "%", limit = 2)[0].toFloat()

    if (num < 30) {
        return R.color.red
    }else if (num < 70) {
        return R.color.orange
    }
    return R.color.green
}