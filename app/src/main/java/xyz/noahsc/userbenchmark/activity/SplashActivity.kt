package xyz.noahsc.userbenchmark.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.reflect.TypeToken
import xyz.noahsc.userbenchmark.data.CPUData
import xyz.noahsc.userbenchmark.data.GPUData
import xyz.noahsc.userbenchmark.data.prepareMap

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareMap(this.assets, arrayOf(
                Pair("CPU_DATA.json", object : TypeToken<HashMap<String, CPUData>>() {}.type),
                Pair("GPU_DATA.json", object : TypeToken<HashMap< String, GPUData>>() {}.type)
        ))
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }
}