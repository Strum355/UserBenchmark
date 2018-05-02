package xyz.noahsc.userbenchmark.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import xyz.noahsc.userbenchmark.data.CPUData
import xyz.noahsc.userbenchmark.data.GPUData
import xyz.noahsc.userbenchmark.data.HardwareMaps

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HardwareMaps.prepareMap(this.assets, arrayOf(
                Pair("CPU_DATA.json", CPUData::class),
                Pair("GPU_DATA.json", GPUData::class)
        ))
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }
}