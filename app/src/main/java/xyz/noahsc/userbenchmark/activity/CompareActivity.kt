package xyz.noahsc.userbenchmark.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import xyz.noahsc.userbenchmark.R
import xyz.noahsc.userbenchmark.data.Hardware

class CompareActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = intent.getParcelableExtra<Hardware>("data")
        setContentView(R.layout.details_page)
        Log.w("test", data.toString())
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}