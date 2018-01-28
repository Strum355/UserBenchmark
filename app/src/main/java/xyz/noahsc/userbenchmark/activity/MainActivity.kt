package xyz.noahsc.userbenchmark.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.*
import android.widget.SearchView
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
import xyz.noahsc.userbenchmark.data.*
import org.jetbrains.anko.*
import xyz.noahsc.userbenchmark.R
import xyz.noahsc.userbenchmark.R.color.cardview_light_background
import xyz.noahsc.userbenchmark.R.id.*
import xyz.noahsc.userbenchmark.R.layout.activity_main
import xyz.noahsc.userbenchmark.R.string.*
import xyz.noahsc.userbenchmark.listener.ClickListener
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

    private var cpuMap: HashMap<String, CPUData> = HashMap()
    private var gpuMap: HashMap<String, GPUData> = HashMap()

    var stringToMaps = mapOf<String, HashMap<String, Hardware>>()

    private val queryListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextChange(newText: String?): Boolean {
            if(newText == null || current == "" || stringToMaps[current] == null) {
                return false
            }
            val searched = searchForSubstring(ArrayList(cpuMap.values), newText).sorted()
            when(state) {
                0 -> recyclerView.adapter = DataAdapter(ArrayList(searched))
                1 -> recyclerView.adapter = DataAdapter(ArrayList(searched.reversed()))
            }
            return false
        }

        override fun onQueryTextSubmit(newText: String?): Boolean {
            if(newText == null || current == "" || stringToMaps[current] == null) {
                return false
            }
            val searched = searchForSubstring(ArrayList(cpuMap.values), newText).sorted()
            when(state) {
                0 -> recyclerView.adapter = DataAdapter(ArrayList(searched))
                1 -> recyclerView.adapter = DataAdapter(ArrayList(searched.reversed()))
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
        setContentView(activity_main)

        toolbar.apply {
            title = ""
            setSupportActionBar(this)
        }

        ActionBarDrawerToggle(this, drawer, toolbar, navigation_drawer_open, navigation_drawer_close).apply {
            syncState()
            drawer.addDrawerListener(this)
        }

        navView.apply {
            setNavigationItemSelectedListener(this@MainActivity)
            setCheckedItem(home)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext, LinearLayout.VERTICAL, false)
        }

        searchView.apply { setOnQueryTextListener(queryListener) }
        setListener()
    }

    private fun setListener() {
        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(applicationContext, recyclerView, object : ClickListener {
            override fun onClick(view: View, position: Int) {
                getHardware(view)?.let {
                    startActivityForResult(intentFor<ProductActivity>("data" to it), 1)
                }
            }

            override fun onLongClick(view: View, position: Int) {
                if (ComparisonData.getCompareFirst() == null) {
                    view.apply {
                        cv.setCardBackgroundColor(ContextCompat.getColor(cv.context, R.color.selected))
                        invalidate()
                    }
                    ComparisonData.setCompareFirst(getHardware(view))
                } else {
                    val compare = getHardware(view)
                    if (compare != ComparisonData.getCompareFirst()) {
                        ComparisonData.setCompareSecond(compare)
                        startActivityForResult(intentFor<CompareActivity>(), 2)
                    }
                }
            }

            fun getHardware(view: View): Hardware? {
                //TODO revise, this cant be right :thinking:
                val splitText = view.hardware.text.toString().split(" ", ignoreCase = true, limit = 2)
                return stringToMaps[current]!![splitText[1]]
            }
        }))

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if ((requestCode == 2 || requestCode == 1) && resultCode == Activity.RESULT_OK) {
            ComparisonData.getCompareFirst()?.let { c ->
                recyclerView.forEachChild {
                    if (it.hardware.text.contains(c.model)) {
                        it.cv.apply {
                            setBackgroundColor(ContextCompat.getColor(applicationContext, cardview_light_background))
                            invalidate()
                        }
                    }
                }
            }
            if (ComparisonData.getCompareSecond() != null) {
                ComparisonData.reset()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        when {
            searchView.isInEditMode -> {
                searchView.apply {
                    clearFocus()
                    isIconified = true
                }
            }
            drawer.isDrawerOpen(GravityCompat.START)  -> drawer.closeDrawer(GravityCompat.START)
            !drawer.isDrawerOpen(GravityCompat.START) -> drawer.openDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            home -> {
                recyclerView.adapter = null
                current = ""
                toolbar.title = ""
                ComparisonData.setCompareFirst(null)
            }
            cpu -> {
                makeHardwareUI(ArrayList(cpuMap.values.sorted()))
                current = "cpu"
                toolbar.title = "CPU"
                ComparisonData.setCompareFirst(null)
            }
            gpu -> {
                makeHardwareUI(ArrayList(gpuMap.values.sorted()))
                current = "gpu"
                toolbar.title = "GPU"
                ComparisonData.setCompareFirst(null)
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
            share -> {
                share("test")
            }
            rank_desc -> {
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
            rank_asc -> {
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
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun makeHardwareUI(list: ArrayList<Hardware>) {
        searchView.apply {
            setQuery("", false)
            isIconified = true
        }

        recyclerView.adapter = DataAdapter(list)
    }
}