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

    private var current = ""
    private var cpu: ArrayList<HardwareData>? = ArrayList()
    private var gpu: ArrayList<HardwareData>? = ArrayList()
    private var ssd: ArrayList<HardwareData>? = ArrayList()
    private var hdd: ArrayList<HardwareData>? = ArrayList()
    private var usb: ArrayList<HardwareData>? = ArrayList()
    private var ram: ArrayList<HardwareData>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.setCheckedItem(R.id.home)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        cpu = savedInstanceState?.getParcelableArrayList("cpu")
        gpu = savedInstanceState?.getParcelableArrayList("gpu")
        ssd = savedInstanceState?.getParcelableArrayList("ssd")
        hdd = savedInstanceState?.getParcelableArrayList("hdd")
        usb = savedInstanceState?.getParcelableArrayList("usb")
        ram = savedInstanceState?.getParcelableArrayList("ram")
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
        menuInflater.inflate(R.menu.sort, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when(current){
            "" -> toast("Must be in a hardware group!")
            "cpu" -> {
                when(id) {
                    R.id.sort_asc -> {
                        
                    }
                }
            }
        }

        return if (id == R.id.action_settings) {

            true
        } else super.onOptionsItemSelected(item)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val toolbar      = findViewById<Toolbar>(R.id.toolbar)

        when(id) {
            R.id.home -> {
                toolbar.title = ""
                current = ""
            }
            R.id.cpu -> {
                makeHardwareDataUI(cpu, applicationContext, recyclerView)
                toolbar.title = "CPU"
                current = "cpu"
            }
            R.id.gpu -> {
                makeHardwareDataUI(gpu, applicationContext, recyclerView)
                toolbar.title = "GPU"
                current = "gpu"
            }
            R.id.ssd -> {
                makeHardwareDataUI(ssd, applicationContext, recyclerView)
                toolbar.title = "SSD"
                current = "ssd"
            }
            R.id.hdd -> {
                makeHardwareDataUI(hdd,applicationContext, recyclerView)
                toolbar.title = "HDD"
                current = "hdd"
            }
            R.id.usb -> {
                makeHardwareDataUI(usb, applicationContext, recyclerView)
                toolbar.title = "USB"
                current = "usb"
            }
            R.id.ram -> {
                makeHardwareDataUI(ram, applicationContext, recyclerView)
                toolbar.title = "RAM"
                current = "ram"
            }
            R.id.share -> {
                share("test")
                current = ""
            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun makeHardwareDataUI(list: ArrayList<HardwareData>?, appCtx: Context, rv: RecyclerView) {
        doAsync {
            uiThread {
                rv.adapter = DataAdapter(filterDuplicateURLS(list))
            }
        }
    }
}

