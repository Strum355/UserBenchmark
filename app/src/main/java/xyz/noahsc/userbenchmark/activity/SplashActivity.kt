package xyz.noahsc.userbenchmark.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.reflect.TypeToken
import xyz.noahsc.userbenchmark.data.CPUData
import xyz.noahsc.userbenchmark.data.GPUData
import xyz.noahsc.userbenchmark.data.prepareLists

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prepareLists(this.assets, arrayOf(
                Pair("CPU_DATA.json", object : TypeToken<ArrayList<CPUData>>() {}.type),
                Pair("GPU_DATA.json", object : TypeToken<ArrayList<GPUData>>() {}.type)
        ))

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}