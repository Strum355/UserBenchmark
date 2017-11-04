package xyz.noahsc.userbenchmark.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import android.support.v7.widget.*
import android.text.SpannableString
import android.text.style.UnderlineSpan
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import xyz.noahsc.userbenchmark.data.CPUData
import xyz.noahsc.userbenchmark.data.GPUData
import xyz.noahsc.userbenchmark.data.Hardware
import xyz.noahsc.userbenchmark.R

class ProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = intent.getParcelableExtra<Hardware>("data")

        Log.w("test", data::class.toString())
        when(data){
            is CPUData -> asCPU(data)
            is GPUData -> asGPU(data)
        }
    }

    private fun asCPU(data: CPUData){
        setContentView(R.layout.details_page)

        val urlUnderline = SpannableString("View in Browser!").apply {
            setSpan(UnderlineSpan(), 0, this.length, 0)
        }

        findViewById<TextView>(R.id.url).apply {
            text = urlUnderline
            onClick {
                browse(data.url)
            }
        }
        findViewById<Toolbar>(R.id.title).apply{ title = "${data.brand} ${data.model}" }

        /*verticalLayout {
            padding = dip(30)
            textView {
                text = "${data.brand} ${data.model}"
                textSize = 24f
            }
            textView(if (data.cores != "") data.cores else "No data")
            textView(fun(): String {
                var ret = ""
                for(res in data.performance) {
                    ret += res+"\n"
                }
                return ret
            }.invoke())
            textView(data.scores.toString().slice(IntRange(1, data.scores.toString().length-2)))
        }*/
    }

    private fun asGPU(data: GPUData) {
        verticalLayout {
            padding = dip(30)
            textView(fun(): String {
                return "${data.averages[0]}${data.averages[1]}"
            }.invoke())
            textView(fun(): String {
                var s: String = ""

                for (i in 0..2) {
                    s += "${data.subresults[i]}${data.subresults[i + 3]}\n"
                }
                return s
            }.invoke())
        }
    }
}