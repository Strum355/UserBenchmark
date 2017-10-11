package xyz.noahsc.userbenchmark.activity

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.widget.SearchView
import android.support.v7.widget.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import xyz.noahsc.userbenchmark.R
import xyz.noahsc.userbenchmark.data.*
import org.jetbrains.anko.*
import java.util.*

class MainActivity: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var current = ""
    private var cpu: ArrayList<HardwareData> = ArrayList()
    private var gpu: ArrayList<HardwareData> = ArrayList()
    private var ssd: ArrayList<HardwareData> = ArrayList()
    private var hdd: ArrayList<HardwareData> = ArrayList()
    private var usb: ArrayList<HardwareData> = ArrayList()
    private var ram: ArrayList<HardwareData> = ArrayList()

    private lateinit var recyclerView: RecyclerView

    private val files = arrayOf(
            "CPU_UserBenchmarks.csv",
            "GPU_UserBenchmarks.csv",
            "SSD_UserBenchmarks.csv",
            "HDD_UserBenchmarks.csv",
            "RAM_UserBenchmarks.csv",
            "USB_UserBenchmarks.csv"
    )

    private val queryListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextChange(newText: String?): Boolean {
            if(newText == null) {
                return false
            }
            when(current) {
                "cpu" -> {
                    recyclerView.adapter = DataAdapter(searchForSubstring(cpu, newText))
                }
                "gpu" -> {
                    recyclerView.adapter = DataAdapter(searchForSubstring(gpu, newText))
                }
                "ssd" -> {
                    recyclerView.adapter = DataAdapter(searchForSubstring(ssd, newText))
                }
                "hdd" -> {
                    recyclerView.adapter = DataAdapter(searchForSubstring(hdd, newText))
                }
                "usb" -> {
                    recyclerView.adapter = DataAdapter(searchForSubstring(usb, newText))
                }
                "ram" -> {
                    recyclerView.adapter = DataAdapter(searchForSubstring(ram, newText))
                }
            }
            return false
        }

        override fun onQueryTextSubmit(query: String?): Boolean {
            if(query == null) {
                return false
            }
            when(current) {
                "cpu" -> {
                    recyclerView.adapter = DataAdapter(searchForSubstring(cpu, query))
                }
                "gpu" -> {
                    recyclerView.adapter = DataAdapter(searchForSubstring(gpu, query))
                }
                "ssd" -> {
                    recyclerView.adapter = DataAdapter(searchForSubstring(ssd, query))
                }
                "hdd" -> {
                    recyclerView.adapter = DataAdapter(searchForSubstring(hdd, query))
                }
                "usb" -> {
                    recyclerView.adapter = DataAdapter(searchForSubstring(usb, query))
                }
                "ram" -> {
                    recyclerView.adapter = DataAdapter(searchForSubstring(ram, query))
                }
            }
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        doAsync {
            cpu = readCSV(files[0], applicationContext)
            gpu = readCSV(files[1], applicationContext)
            ssd = readCSV(files[2], applicationContext)
            hdd = readCSV(files[3], applicationContext)
            usb = readCSV(files[4], applicationContext)
            ram = readCSV(files[5], applicationContext)
        }

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

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(queryListener)
    }

    override fun onBackPressed() {
        val searchView = findViewById<SearchView>(R.id.searchView)
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else if (searchView.isInEditMode) {
            Log.w("test", "focused")
            searchView.clearFocus()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    private fun deepCopy(list: ArrayList<HardwareData>): ArrayList<HardwareData> {
        val ret = ArrayList<HardwareData>(list.size)
        list.forEach { ret.add(it) }
        return ret
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }

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
                current = ""
            }
            R.id.rank_desc -> {
                when(current){
                    "" -> toast("Must be in a hardware group!")
                    "cpu" -> {
                        val copy = deepCopy(cpu)
                        Collections.reverse(copy)
                        makeHardwareDataUI(copy)
                    }
                    "gpu" -> {
                        val copy = deepCopy(gpu)
                        Collections.reverse(copy)
                        makeHardwareDataUI(copy)
                    }
                    "ssd" -> {
                        val copy = deepCopy(ssd)
                        Collections.reverse(copy)
                        makeHardwareDataUI(copy)
                    }
                    "hdd" -> {
                        val copy = deepCopy(hdd)
                        Collections.reverse(copy)
                        makeHardwareDataUI(copy)
                    }
                    "usb" -> {
                        val copy = deepCopy(usb)
                        Collections.reverse(copy)
                        makeHardwareDataUI(copy)
                    }
                    "ram" -> {
                        val copy = deepCopy(ram)
                        Collections.reverse(copy)
                        makeHardwareDataUI(copy)
                    }
                }
                toast("List sorted from lowest to highest rank")
            }
            R.id.rank_asc -> {
                when(current){
                    "" -> toast("Must be in a hardware group!")
                    "cpu" -> {
                        makeHardwareDataUI(cpu)
                    }
                    "gpu" -> {
                        makeHardwareDataUI(gpu)
                    }
                    "ssd" -> {
                        makeHardwareDataUI(ssd)
                    }
                    "hdd" -> {
                        makeHardwareDataUI(hdd)
                    }
                    "usb" -> {
                        makeHardwareDataUI(usb)
                    }
                    "ram" -> {
                        makeHardwareDataUI(ram)
                    }
                }
                toast("List sorted from highest to lowest rank")
            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun makeHardwareDataUI(list: ArrayList<HardwareData>) {
        val rv = findViewById<RecyclerView>(R.id.recyclerView)
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setQuery("", false)
        searchView.isIconified = true
        doAsync {
            uiThread {
                rv.adapter = DataAdapter(filterDuplicateURLS(list))
            }
        }
    }
}

