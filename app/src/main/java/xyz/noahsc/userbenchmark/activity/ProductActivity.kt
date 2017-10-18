package xyz.noahsc.userbenchmark.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import org.jetbrains.anko.*

class ProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rank = intent.getStringExtra("rank")
        val samples = intent.getStringExtra("hardware")
        val hardware = intent.getStringExtra("samples")
        val perf = intent.getStringExtra("perf")
        verticalLayout {
            padding = dip(30)
            textView(rank)
            textView(samples)
            textView(hardware)
            textView(perf)
        }
    }
}