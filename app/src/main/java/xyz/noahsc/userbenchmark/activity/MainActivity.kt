package xyz.noahsc.userbenchmark.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.widget.SearchView
import android.support.v7.widget.*
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.parts_list_row.view.*
import xyz.noahsc.userbenchmark.R
import xyz.noahsc.userbenchmark.data.*
import org.jetbrains.anko.*
import xyz.noahsc.userbenchmark.listener.RecyclerItemClickListener
import java.io.InputStreamReader
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.mapOf

class MainActivity: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var current = ""
    // 0 - rank_asc
    // 1 - rank_desc
    private var state = 0
    private var toCompare: Hardware? = null

    private var cpuMap: HashMap<String, CPUData> = HashMap()
    private var gpuMap: HashMap<String, GPUData> = HashMap()

    private val recycler: RecyclerView by lazy {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext, LinearLayout.VERTICAL, false)
        }
    }

    var stringToMaps = mapOf<String, HashMap<String, Hardware>>()

    private val queryListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextChange(newText: String?): Boolean {
            if(newText == null || current == "" || stringToMaps[current] == null) {
                return false
            }
            val searched = searchForSubstring(ArrayList(cpuMap.values), newText).sorted()
            when(state) {
                0 -> recycler.adapter = DataAdapter(ArrayList(searched))
                1 -> recycler.adapter = DataAdapter(ArrayList(searched.reversed()))
            }
            return false
        }

        override fun onQueryTextSubmit(newText: String?): Boolean {
            if(newText == null || current == "" || stringToMaps[current] == null) {
                return false
            }
            val searched = searchForSubstring(ArrayList(cpuMap.values), newText).sorted()
            when(state) {
                0 -> recycler.adapter = DataAdapter(ArrayList(searched))
                1 -> recycler.adapter = DataAdapter(ArrayList(searched.reversed()))
            }
            return false
        }
    }

    private fun prepareMaps(): Map<String, HashMap<String, Hardware>> {
        val parser = Gson()
        val assetManager = this.assets

        var input = assetManager.open("CPU_DATA.json")
        val cpuDataType = object:TypeToken<HashMap<String, CPUData>>(){}.type
        cpuMap = parser.fromJson(InputStreamReader(input), cpuDataType)

        input = assetManager.open("GPU_MAP.json")
        val gpuDataType = object:TypeToken<HashMap<String, GPUData>>(){}.type
        gpuMap = parser.fromJson(InputStreamReader(input), gpuDataType)

        return mapOf(
                "cpu" to cpuMap as HashMap<String, Hardware>,
                "gpu" to gpuMap as HashMap<String, Hardware>
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        stringToMaps = prepareMaps()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar).apply { title = "" }
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close).apply { syncState() }
        drawer.addDrawerListener(toggle)

        val navigationView = findViewById<NavigationView>(R.id.navView)
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.setCheckedItem(R.id.home)

        searchView.apply { setOnQueryTextListener(queryListener) }
        setListener()
    }

    private fun setListener() {
        recycler.addOnItemTouchListener(RecyclerItemClickListener(applicationContext, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val splitText = view.hardware.text.toString().split(" ", ignoreCase = true, limit = 2)
                val content: Hardware? = stringToMaps[current]!![splitText[1]]
                if (content != null) {
                    startActivityForResult(intentFor<ProductActivity>("data" to content, "compare" to toCompare), 1)
                }
            }
        }))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            toCompare = data?.getParcelableExtra("compare")
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else if (searchView.isInEditMode) {
            searchView.clearFocus()
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.home -> {
                recycler.adapter = null
                current = ""
                toolbar.title = ""
                toCompare = null
            }
            R.id.cpu -> {
                makeHardwareUI(ArrayList(cpuMap.values.sorted()))
                current = "cpu"
                toolbar.title = "CPU"
                toCompare = null
            }
            R.id.gpu -> {
                makeHardwareUI(ArrayList(gpuMap.values.sorted()))
                current = "gpu"
                toolbar.title = "GPU"
                toCompare = null
            }
            /* R.id.ssd -> {
                 makeHardwareUI(ssd)
                 current = "ssd"
                 toolbar.title = "SSD"
             }
             R.id.hdd -> {
                 makeHardwareUI(hdd)
                 current = "hdd"
                 toolbar.title = "HDD"
             }
             R.id.usb -> {
                 makeHardwareUI(usb)
                 current = "usb"
                 toolbar.title = "USB"
             }
             R.id.ram -> {
                 makeHardwareUI(ram)
                 current = "ram"
                 toolbar.title = "RAM"
             }*/
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
                val currMap = stringToMaps[current] as HashMap<String, Hardware>
                val mapToList = ArrayList(currMap.values)
                makeHardwareUI(ArrayList(mapToList.sorted()))
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
                val currMap = stringToMaps[current] as HashMap<String, Hardware>
                val mapToList = ArrayList(currMap.values)
                makeHardwareUI(ArrayList(mapToList.sorted().reversed()))
                toast("List sorted from highest to lowest rank")
            }
            //R.id.app_bar_switch -> !base
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun makeHardwareUI(list: ArrayList<Hardware>) {
        searchView.apply {
            setQuery("", false)
            isIconified = true
        }

      /*  doAsync {
            //TODO check if this is even needed. Is input already filtered in the JSON?
            //Turns out all duplicates were already removed as far as i could see, keeping
            //this function just for future
            //filterDuplicateUrls(list)
            uiThread {*/
                recycler.adapter = DataAdapter(list)

     /*       }
        }*/
    }
}