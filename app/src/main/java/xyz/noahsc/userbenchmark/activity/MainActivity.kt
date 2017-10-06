package xyz.noahsc.userbenchmark.activity

import android.content.Context
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.*
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import xyz.noahsc.userbenchmark.R
import xyz.noahsc.userbenchmark.data.*
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        when (id){
            R.id.cpu -> {
                makeHardwareDataUI("CPU_UserBenchmarks.csv", applicationContext, recyclerView)
            }
            R.id.gpu -> {
                makeHardwareDataUI("GPU_UserBenchmarks.csv", applicationContext, recyclerView)
            }
            R.id.ssd -> {
                makeHardwareDataUI("SSD_UserBenchmarks.csv", applicationContext, recyclerView)
            }
            R.id.hdd -> {
                makeHardwareDataUI("HDD_UserBenchmarks.csv",applicationContext, recyclerView)
            }
            R.id.usb -> {
                makeHardwareDataUI("USB_UserBenchmarks.csv", applicationContext, recyclerView)
            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun makeHardwareDataUI(file: String, appCtx: Context, rv: RecyclerView) {
        doAsync {
            val result = readCSV(file, appCtx)
            uiThread {
                rv.adapter = DataAdapter(filterDuplicateURLS(result))
            }
        }
    }
}

