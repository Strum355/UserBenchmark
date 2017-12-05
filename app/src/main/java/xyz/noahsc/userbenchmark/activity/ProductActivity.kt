package xyz.noahsc.userbenchmark.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.CompoundButton
import kotlinx.android.synthetic.main.cpu.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import xyz.noahsc.userbenchmark.data.CPUData
import xyz.noahsc.userbenchmark.data.GPUData
import xyz.noahsc.userbenchmark.R
import kotlinx.android.synthetic.main.details_page.*
import kotlinx.android.synthetic.main.gpu.*
import xyz.noahsc.userbenchmark.data.Hardware

class ProductActivity : AppCompatActivity() {

    var toCompare: Hardware? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = intent.getParcelableExtra<Hardware>("data")
        toCompare = intent.getParcelableExtra<Hardware>("compare")

        setContentView(R.layout.details_page)
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
        if(data.model == toCompare?.model) {
            checkBox.isChecked = true
        }

        detail_rank.text = "${detail_rank.text}${data.rank}"

        detail_samples.text = "Samples: ${data.samples}"
        when(data){
            is CPUData -> asCPU(data)
            is GPUData -> asGPU(data)
        }

        checkBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(button: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    if (toCompare == null){
                        toCompare = data
                    }else{
                        startActivityForResult(intentFor<CompareActivity>("data1" to toCompare, "data2" to data), 1)
                    }
                }else{
                    toCompare = null
                }
            }
        })
    }

    override fun onBackPressed() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("compare", toCompare)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            toCompare = null
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun asCPU(data: CPUData) {
        detail_stub.apply {
            layoutResource = R.layout.cpu
            inflate()
        }

        single_int.text   = data.subresults[0]
        single_float.text = data.subresults[1]
        single_mixed.text = data.subresults[2]
        quad_int.text     = data.subresults[3]
        quad_float.text   = data.subresults[4]
        quad_mixed.text   = data.subresults[5]
        multi_int.text    = data.subresults[6]
        multi_float.text  = data.subresults[7]
        multi_mixed.text  = data.subresults[8]

        single_average.apply {
            text = data.scores[0]
            setBackgroundResource(numberToColor(data.scores[1]))
        }
        quad_average.apply {
            text = data.scores[1]
            setBackgroundResource(numberToColor(data.scores[1]))
        }
        multi_average.apply {
            text = data.scores[2]
            setBackgroundResource(numberToColor(data.scores[1]))
        }
    }

    private fun asGPU(data: GPUData) {
        detail_stub.apply {
            layoutResource = R.layout.gpu
            inflate()
        }

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