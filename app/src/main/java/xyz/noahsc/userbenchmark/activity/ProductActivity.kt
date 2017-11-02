package xyz.noahsc.userbenchmark.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.*
import xyz.noahsc.userbenchmark.data.CPUData

class ProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = intent.getParcelableExtra<CPUData>("data")
        verticalLayout {
            padding = dip(30)
            textView(if (data.cores != "") data.cores else "No data")
            textView(fun(): String {
                var ret = ""
                for(res in data.performance) {
                    ret += res+"\n"
                }
                return ret
            }.invoke())
            textView(data.scores.contentDeepToString())
        }
    }
}