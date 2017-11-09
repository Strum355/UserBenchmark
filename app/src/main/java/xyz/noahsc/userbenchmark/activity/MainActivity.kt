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
import android.view.View
import android.widget.LinearLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.parts_list_row.*
import kotlinx.android.synthetic.main.parts_list_row.view.*
import xyz.noahsc.userbenchmark.R
import xyz.noahsc.userbenchmark.data.*
import org.jetbrains.anko.*
import xyz.noahsc.userbenchmark.listener.RecyclerItemClickListener
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.mapOf

class MainActivity: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var current = ""
    // 0 - rank_asc | 1 - rank_desc
    private var state = 0

    private var cpuMap: HashMap<String, CPUData> = HashMap()
    private var gpuMap: HashMap<String, GPUData> = HashMap()

    private lateinit var recycler: RecyclerView

    var stringToMaps = mapOf<String, HashMap<String, Hardware>>()

    private val queryListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextChange(newText: String?): Boolean {
            if(newText == null || current == "" || stringToMaps[current] == null) {
                return false
            }
            recycler.adapter = DataAdapter(searchForSubstring(ArrayList(stringToMaps[current]!!.values as ArrayList<Hardware>), newText))
            return false
        }

        override fun onQueryTextSubmit(newText: String?): Boolean {
            if(newText == null || current == "" || stringToMaps[current] == null) {
                return false
            }
            recycler.adapter = DataAdapter(searchForSubstring(ArrayList(stringToMaps[current]!!.values as ArrayList<Hardware>), newText))
            return false
        }
    }

    private fun prepareMaps(){
        val parser = Gson()
        val assetManager = ctx.assets

        var input = assetManager.open("CPU_DATA.json")
        val cpuDataType = object:TypeToken<HashMap<String, CPUData>>(){}.type
        cpuMap = parser.fromJson(InputStreamReader(input), cpuDataType)

        input = assetManager.open("GPU_MAP.json")
        val gpuDataType = object:TypeToken<HashMap<String, GPUData>>(){}.type
        gpuMap = parser.fromJson(InputStreamReader(input), gpuDataType)

        stringToMaps = mapOf(
                "cpu" to cpuMap as HashMap<String, Hardware>,
                "gpu" to gpuMap as HashMap<String, Hardware>
        )
    }

    private fun reverse(l: ArrayList<Hardware>): ArrayList<Hardware> {
        val out = deepCopy(l)
        Collections.reverse(out)
        return out
    }

    private fun deepCopy(list: ArrayList<Hardware>): ArrayList<Hardware> {
        val ret = ArrayList(list)
        list.forEach { ret.add(it) }
        return ret
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        prepareMaps()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar).apply { title = "" }
        setSupportActionBar(toolbar)

        val drawer = findViewById<DrawerLayout>(R.id.drawer)
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close).apply { syncState() }
        drawer.addDrawerListener(toggle)

        val navigationView = findViewById<NavigationView>(R.id.navView)
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.setCheckedItem(R.id.home)

        recycler = recyclerView.apply { layoutManager = LinearLayoutManager(applicationContext, LinearLayout.VERTICAL, false) }

        findViewById<SearchView>(R.id.searchView).apply { setOnQueryTextListener(queryListener) }
        setListener()
    }

    private fun setListener() {
        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(applicationContext, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val text = view.hardware.text.toString()
                val splitText = text.split(delimiters = " ", limit = 2)

                startActivity(intentFor<ProductActivity>("data" to stringToMaps[current]!![splitText[1]]))
            }
        }))
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

    override fun onCreateOptionsMenu(menu: Menu) = true

    override fun onOptionsItemSelected(item: MenuItem) = true

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when(id) {
            R.id.home -> {
                recyclerView.adapter = null
                current = ""
                toolbar.title = ""
            }
            R.id.cpu -> {
                makeHardwareUI(ArrayList<Hardware>(ArrayList<Hardware>(cpuMap.values).sorted()))
                current = "cpu"
                toolbar.title = "CPU"
            }
            R.id.gpu -> {
                makeHardwareUI(ArrayList<Hardware>(ArrayList<Hardware>(gpuMap.values).sorted()))
                current = "gpu"
                toolbar.title = "GPU"
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
                val mapToList = ArrayList<Hardware>(currMap.values)
                makeHardwareUI(ArrayList<Hardware>(mapToList.sorted()))
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
                val mapToList = ArrayList<Hardware>(currMap.values)
                makeHardwareUI(ArrayList<Hardware>(mapToList.sorted().reversed()))
                toast("List sorted from highest to lowest rank")
            }
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun makeHardwareUI(list: ArrayList<Hardware>) {
        searchView.apply {
            setQuery("", false)
            isIconified = true
        }

        doAsync {
            val filtered = filterDuplicateURLS(list)
            uiThread {
               recyclerView.adapter = DataAdapter(filtered)
            }
        }
    }
}