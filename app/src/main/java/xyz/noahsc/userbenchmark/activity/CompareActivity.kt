package xyz.noahsc.userbenchmark.activity

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.details_page.*
import xyz.noahsc.userbenchmark.R
import xyz.noahsc.userbenchmark.data.Hardware

class CompareActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data1 = intent.getParcelableExtra<Hardware>("data1")
        val data2 = intent.getParcelableExtra<Hardware>("data2")

        setContentView(R.layout.compare_main)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}