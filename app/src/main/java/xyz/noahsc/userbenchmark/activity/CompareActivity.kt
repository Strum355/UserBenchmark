package xyz.noahsc.userbenchmark.activity

import android.app.Activity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.compare_main.*
import kotlinx.android.synthetic.main.details_page.*
import xyz.noahsc.userbenchmark.R
import xyz.noahsc.userbenchmark.data.CPUData
import xyz.noahsc.userbenchmark.data.GPUData
import xyz.noahsc.userbenchmark.data.Hardware

class CompareActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data1 = intent.getParcelableExtra<Hardware>("data1")
        val data2 = intent.getParcelableExtra<Hardware>("data2")

        setContentView(R.layout.compare_main)

        when(data1){
            is CPUData -> asCPU(data1, data2 as CPUData)
        }

        row2.setOnClickListener {
            expandable_layout.toggle()
        }
        row3.setOnClickListener{
            expandable_layout1.toggle()
        }
        row4.setOnClickListener {
            expandable_layout2.toggle()
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun asCPU(data: CPUData, data1: CPUData) {
        with(data) {
            arrayOf(sc_int_1, sc_float_1, sc_mixed_1, qc_int_1, qc_float_1, qc_mixed_1, mc_int_1, mc_float_1, mc_mixed_1).forEachIndexed { i, v ->
                v.text = subresults[i].split(" ")[2].replace(",", "")
            }
            name1.text = "${brand} ${model}"
        }

        with(data1) {
            arrayOf(sc_int_2, sc_float_2, sc_mixed_2, qc_int_2, qc_float_2, qc_mixed_2, mc_int_2, mc_float_2, mc_mixed_2).forEachIndexed { i, v ->
                v.text = subresults[i].split(" ")[2].replace(",", "")
            }
            name2.text = "${brand} ${model}"
        }
    }
}