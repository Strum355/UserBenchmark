package xyz.noahsc.userbenchmark.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import xyz.noahsc.userbenchmark.data.readCSV

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val files = arrayOf(
                "CPU_UserBenchmarks.csv",
                "GPU_UserBenchmarks.csv",
                "SSD_UserBenchmarks.csv",
                "HDD_UserBenchmarks.csv",
                "RAM_UserBenchmarks.csv",
                "USB_UserBenchmarks.csv"
        )

        val varName = arrayOf(
                "cpu",
                "gpu",
                "ssd",
                "hdd",
                "ram",
                "usb"
        )

        for (i in 0 until files.size-1) {
            val result = readCSV(files[i], applicationContext)
            savedInstanceState?.putParcelableArrayList(varName[i], result)
        }

        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }
}