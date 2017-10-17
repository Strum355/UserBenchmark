package xyz.noahsc.userbenchmark.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.widget.SearchView
import android.support.v7.widget.*
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import xyz.noahsc.userbenchmark.R
import xyz.noahsc.userbenchmark.data.*
import org.jetbrains.anko.*
import xyz.noahsc.userbenchmark.listener.RecyclerItemClickListener
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.mapOf

class MainActivity: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var current = ""
    // 0 - rank_asc | 1 - rank_desc
    private var state = 0
    private var cpu: ArrayList<HardwareData> = ArrayList()
    private var gpu: ArrayList<HardwareData> = ArrayList()
    private var ssd: ArrayList<HardwareData> = ArrayList()
    private var hdd: ArrayList<HardwareData> = ArrayList()
    private var usb: ArrayList<HardwareData> = ArrayList()
    private var ram: ArrayList<HardwareData> = ArrayList()

    private var cpuR: ArrayList<HardwareData> = ArrayList()
    private var gpuR: ArrayList<HardwareData> = ArrayList()
    private var ssdR: ArrayList<HardwareData> = ArrayList()
    private var hddR: ArrayList<HardwareData> = ArrayList()
    private var usbR: ArrayList<HardwareData> = ArrayList()
    private var ramR: ArrayList<HardwareData> = ArrayList()

    private lateinit var recyclerView: RecyclerView

    private val files = arrayOf(
            "CPU_UserBenchmarks.csv",
            "GPU_UserBenchmarks.csv",
            "SSD_UserBenchmarks.csv",
            "HDD_UserBenchmarks.csv",
            "RAM_UserBenchmarks.csv",
            "USB_UserBenchmarks.csv"
    )

    var stringToArray = mapOf<String, Array<ArrayList<HardwareData>>>()

    private val queryListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextChange(newText: String?): Boolean {
            if(newText == null || current == "" || stringToArray[current] == null) {
                return false
            }
            recyclerView.adapter = DataAdapter(searchForSubstring(stringToArray[current]!![state], newText))

            return false
        }

        override fun onQueryTextSubmit(newText: String?): Boolean {
            if(newText == null || current == "" || stringToArray[current] == null) {
                return false
            }

            recyclerView.adapter = DataAdapter(searchForSubstring(stringToArray[current]!![state], newText))
            return false
        }
    }

    private fun prepareLists(){
        cpu = readCSV(files[0], applicationContext)
        gpu = readCSV(files[1], applicationContext)
        ssd = readCSV(files[2], applicationContext)
        hdd = readCSV(files[3], applicationContext)
        usb = readCSV(files[4], applicationContext)
        ram = readCSV(files[5], applicationContext)

        cpuR = reverse(cpu)
        gpuR = reverse(gpu)
        ssdR = reverse(ssd)
        hddR = reverse(hdd)
        usbR = reverse(usb)
        ramR = reverse(ram)
    }

    private fun reverse(l: ArrayList<HardwareData>): ArrayList<HardwareData> {
        val out = deepCopy(l)
        Collections.reverse(out)
        return out
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        prepareLists()
        stringToArray = mapOf(
            "cpu" to arrayOf(cpu, cpuR),
            "gpu" to arrayOf(gpu, gpuR),
            "ssd" to arrayOf(ssd, ssdR),
            "hdd" to arrayOf(hdd, hddR),
            "ram" to arrayOf(ram, ramR),
            "usb" to arrayOf(usb, usbR)
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar).apply { title = "" }
        setSupportActionBar(toolbar)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close).apply { syncState() }
        drawer.addDrawerListener(toggle)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.setCheckedItem(R.id.home)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply { layoutManager = LinearLayoutManager(applicationContext, LinearLayout.VERTICAL, false) }

        findViewById<SearchView>(R.id.searchView).apply { setOnQueryTextListener(queryListener) }
        setListener()
    }

    private fun setListener() {
        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(applicationContext, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                startActivity(Intent(this@MainActivity, ProductActivity::class.java))
            }
        }))
    }

    override fun onBackPressed() {
        val searchView = findViewById<SearchView>(R.id.searchView)
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else if (searchView.isInEditMode) {
            searchView.clearFocus()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu) = true

    private fun deepCopy(list: ArrayList<HardwareData>): ArrayList<HardwareData> {
        val ret = ArrayList<HardwareData>(list.size)
        list.forEach { ret.add(it) }
        return ret
    }

    override fun onOptionsItemSelected(item: MenuItem) = true

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        when(id) {
            R.id.home -> {
                recyclerView.adapter = null
                current = ""
                toolbar.title = ""
            }
            R.id.cpu -> {
                makeHardwareDataUI(cpu)
                current = "cpu"
                toolbar.title = "CPU"
            }
            R.id.gpu -> {
                makeHardwareDataUI(gpu)
                current = "gpu"
                toolbar.title = "GPU"
            }
            R.id.ssd -> {
                makeHardwareDataUI(ssd)
                current = "ssd"
                toolbar.title = "SSD"
            }
            R.id.hdd -> {
                makeHardwareDataUI(hdd)
                current = "hdd"
                toolbar.title = "HDD"
            }
            R.id.usb -> {
                makeHardwareDataUI(usb)
                current = "usb"
                toolbar.title = "USB"
            }
            R.id.ram -> {
                makeHardwareDataUI(ram)
                current = "ram"
                toolbar.title = "RAM"
            }
            R.id.share -> {
                share("test")
            }
            R.id.rank_desc -> {
                state = 1
                when(current){
                    "" -> {
                        toast("Must be in a hardware group!")
                        return false
                    }
                }
                makeHardwareDataUI(stringToArray[current]!![state])
                toast("List sorted from lowest to highest rank")
            }
            R.id.rank_asc -> {
                state = 0
                when(current) {
                    "" -> {
                        toast("Must be in a hardware group!")
                        return false
                    }
                }
                makeHardwareDataUI(stringToArray[current]!![state])
                toast("List sorted from highest to lowest rank")
            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun makeHardwareDataUI(list: ArrayList<HardwareData>) {
        val rv = findViewById<RecyclerView>(R.id.recyclerView)
        findViewById<SearchView>(R.id.searchView).apply {
            setQuery("", false)
            isIconified = true
        }

        doAsync {
            uiThread {
                rv.adapter = DataAdapter(filterDuplicateURLS(list))
            }
        }
    }
}