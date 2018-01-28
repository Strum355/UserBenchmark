package xyz.noahsc.userbenchmark.activity

import android.app.Activity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import kotlinx.android.synthetic.main.compare_main.*
import kotlinx.android.synthetic.main.compare_cpu.*
import org.jetbrains.anko.intentFor
import xyz.noahsc.userbenchmark.R.color.green
import xyz.noahsc.userbenchmark.R.color.red
import xyz.noahsc.userbenchmark.R.layout.compare_cpu
import xyz.noahsc.userbenchmark.R.layout.compare_main
import xyz.noahsc.userbenchmark.data.CPUData
import xyz.noahsc.userbenchmark.data.ComparisonData
import java.text.DecimalFormat
import kotlin.math.roundToInt

class CompareActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(compare_main)

        val data1 = ComparisonData.getCompareFirst()
        val data2 = ComparisonData.getCompareSecond()

        when(data1){
            is CPUData -> asCPU(data1, data2 as CPUData)
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK, intentFor<ProductActivity>())
        finish()
    }

    private fun asCPU(data: CPUData, data1: CPUData) {
        compare_stub.apply {
            layoutResource = compare_cpu
            inflate()
        }

        val diff = Array<Int>(data1.subresults.size*2, {0})

        with(data) {
            arrayOf(sc_int_1, sc_float_1, sc_mixed_1, qc_int_1, qc_float_1, qc_mixed_1, mc_int_1, mc_float_1, mc_mixed_1).forEachIndexed { i, v ->
                val num = subresults[i].split(" ")[2].replace(",", "").toFloat()
                val num2 = data1.subresults[i].split(" ")[2].replace(",", "").toFloat()

                val percent = (((maxOf(num, num2)/ minOf(num, num2))*100)-100).roundToInt()
                val span = SpannableStringBuilder()
                var col: ForegroundColorSpan? = null

                when {
                    num > num2 -> {
                        span.append("  +$percent%")
                        diff[i] = -percent
                        col = ForegroundColorSpan(ContextCompat.getColor(applicationContext, green))
                    }
                    num < num2 -> {
                        span.append("  ${-percent}%")
                        diff[i] = percent
                        col = ForegroundColorSpan(ContextCompat.getColor(applicationContext, red))
                    }
                }
                span.setSpan(col, 0, span.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                span.setSpan(RelativeSizeSpan(0.7f), 0, span.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                val format = DecimalFormat("####.#")
                v.text = TextUtils.concat(format.format(num), span)
            }
            name1.text = "${brand} ${model}"
        }

        with(data1) {
            arrayOf(sc_int_2, sc_float_2, sc_mixed_2, qc_int_2, qc_float_2, qc_mixed_2, mc_int_2, mc_float_2, mc_mixed_2).forEachIndexed { i, v ->
                val span = SpannableStringBuilder()
                when {
                    diff[i] > 0 -> {
                        span.append("  +${diff[i]}%")
                        span.setSpan(ForegroundColorSpan(ContextCompat.getColor(applicationContext, green)), 0, span.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    diff[i] < 0 -> {
                        span.append("  ${diff[i]}%")
                        span.setSpan(ForegroundColorSpan(ContextCompat.getColor(applicationContext, red)), 0, span.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                span.setSpan(RelativeSizeSpan(0.7f), 0, span.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                v.text = TextUtils.concat(subresults[i].split(" ")[2].replace(",", ""), span)
            }
            name2.text = "${brand} ${model}"
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
}