package xyz.noahsc.userbenchmark.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.style.UnderlineSpan
import kotlinx.android.synthetic.main.cpu.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import xyz.noahsc.userbenchmark.data.CPUData
import xyz.noahsc.userbenchmark.data.GPUData
import kotlinx.android.synthetic.main.details_page.*
import kotlinx.android.synthetic.main.gpu.*

import xyz.noahsc.userbenchmark.R.color.*
import xyz.noahsc.userbenchmark.R.layout.*
import xyz.noahsc.userbenchmark.data.ComparisonData
import xyz.noahsc.userbenchmark.data.Hardware

class ProductActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = intent.getParcelableExtra("data") as Hardware

        setContentView(details_page)
        toolbar.apply{ title = "${data.brand} ${data.model}" }
        setSupportActionBar(toolbar)

        url.apply {
            text = SpannableString("View in Browser!").apply {
                setSpan(UnderlineSpan(), 0, this.length, 0)
            }
            onClick {
                browse(data.url)
            }
        }

        if(data.model == ComparisonData.getCompareFirst()?.model) {
            checkBox.isChecked = true
        }

        detail_rank.text = "${detail_rank.text} ${data.rank}"

        detail_samples.text = "Samples: ${data.samples}"

        // TODO someday not have this here
        when(data){
            is CPUData -> asCPU(data)
            is GPUData -> asGPU(data)
        }

        checkBox.setOnCheckedChangeListener({ _, isChecked: Boolean ->
            if (isChecked) {
                if (ComparisonData.getCompareFirst() == null){
                    ComparisonData.setCompareFirst(data)
                }else{
                    ComparisonData.setCompareSecond(data)
                    startActivityForResult(intentFor<CompareActivity>(), 1)
                }
            }else{
                ComparisonData.setCompareFirst(null)
            }
        })
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK, intentFor<MainActivity>())
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            //to reset the two choices. Will revisit this decision at a later date
            checkBox.isChecked = false
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun asCPU(data: CPUData) {
        detail_stub.apply {
            layoutResource = cpu
            inflate()
        }

        with(data) {
            arrayOf(single_int, single_float, single_mixed, quad_int, quad_float, quad_mixed, multi_int, multi_float, multi_mixed).forEachIndexed { i, v ->
                v.text = subresults[i]
            }

            single_average.apply {
                text = scores[0]
                setBackgroundResource(numberToColor(scores[1]))
            }
            quad_average.apply {
                text = scores[1]
                setBackgroundResource(numberToColor(scores[1]))
            }
            multi_average.apply {
                text = scores[2]
                setBackgroundResource(numberToColor(scores[1]))
            }
        }
    }

    private fun asGPU(data: GPUData) {
        detail_stub.apply {
            layoutResource = gpu
            inflate()
        }

        with(data) {
            arrayOf(lighting, reflection, parallax, mrender, gravity, splatting).forEachIndexed { i, v ->
                v.text = subresults[i]
            }

            dx9.apply {
                text = data.averages[0]
                setBackgroundResource(numberToColor(data.averages[0]))
            }
            dx10.apply {
                text = data.averages[1]
                setBackgroundResource(numberToColor(data.averages[1]))
            }
        }
    }
}

fun numberToColor(s: String): Int {
    try {
        val num = s.split("%", ignoreCase = true, limit = 2)[0].toFloat()
        if (num < 30) {
            return red
        }else if (num < 70) {
            return orange
        }
        return green
    } catch(e: NumberFormatException) {
        return red
    }
}