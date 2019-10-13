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
import arrow.core.*
import arrow.instances.option.monad.monad
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.parts_list_row.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import xyz.noahsc.userbenchmark.data.*
import org.jetbrains.anko.*
import xyz.noahsc.userbenchmark.R.color.*
import xyz.noahsc.userbenchmark.R.id.*
import xyz.noahsc.userbenchmark.R.layout.activity_main
import xyz.noahsc.userbenchmark.R.string.*
import xyz.noahsc.userbenchmark.listener.ClickListener
import xyz.noahsc.userbenchmark.listener.RecyclerItemClickListener

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val adapter = DataAdapter(emptyList())

    private val queryListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextChange(newText: String?): Boolean {
            // Currently dont need different behaviour
            return onQueryTextSubmit(newText)
        }

        override fun onQueryTextSubmit(newText: String?): Boolean {
            newText?.run {
                val hardwareList = getHardwareMap(getStateString())

                if (hardwareList == None) {
                    return false
                }

                val searched = searchForSubstring(hardwareList.orNull()!!, newText)
                when (getSorting()) {
                    Sorting.ASCENDING -> {
                        adapter.partsList = searched
                        recyclerView.adapter!!.notifyDataSetChanged()
                    }
                    Sorting.DESCENDING -> {
                        adapter.partsList = searched
                        recyclerView.adapter!!.notifyDataSetChanged()
                    }
                }
            }
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
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

        recyclerView.layoutManager = LinearLayoutManager(applicationContext, LinearLayout.VERTICAL, false)
        recyclerView.adapter = adapter

        searchView.setOnQueryTextListener(queryListener)
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
                        cv.setCardBackgroundColor(ContextCompat.getColor(cv.context, xyz.noahsc.userbenchmark.R.color.selected))
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
                val extractNum = Regex("([0-9])")
                val rank = Integer.parseInt(extractNum.find(view.rank.text.toString())!!.value)
                return getHardwareMap(getStateString()).orNull()!![rank-1]
            }
        }))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode != Activity.RESULT_OK) {
            return
        }

        val applyColor: (Int) -> Unit = { col ->
            ComparisonData.getCompareFirst()?.let { c ->
                recyclerView.forEachChild {
                    if (it.hardware.text.contains(c.model)) {
                        it.cv.apply {
                            setBackgroundColor(ContextCompat.getColor(cv.context, col))
                            invalidate()
                        }
                    }
                }
            }
        }

        /*
            1 = from details
            2 = from comparison
        */
        when(requestCode) {
            1 -> {
                data?.let {
                    if (ComparisonData.getCompareFirst() != null) {
                        if (ComparisonData.getCompareSecond() != null) {
                            applyColor(cardview_light_background)
                            ComparisonData.reset()
                            return
                        }
                        applyColor(xyz.noahsc.userbenchmark.R.color.selected)
                    }
                }
            }
            2 -> {
                applyColor(cardview_light_background)
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
            drawer.isDrawerOpen(GravityCompat.START) -> drawer.closeDrawer(GravityCompat.START)
            !drawer.isDrawerOpen(GravityCompat.START) -> drawer.openDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer.closeDrawer(GravityCompat.START)

        //TODO clean up this godawful mess
        GlobalScope.launch(Dispatchers.Default) {
            when (item.itemId) {
                home -> {
                    updateState("Home")
                    makeHardwareUI(emptyList())
                    ComparisonData.setCompareFirst(null)
                }
                cpu -> {
                    updateState("CPU")
                    makeHardwareUI(getHardwareMap(getStateString()).orNull()!!)
                    ComparisonData.setCompareFirst(null)
                }
                gpu -> {
                    updateState("GPU")
                    makeHardwareUI(getHardwareMap(getStateString()).orNull()!!)
                    ComparisonData.setCompareFirst(null)
                }
                share -> {
                    runOnUiThread {
                        share("test")
                    }
                }

                //TODO DRY code pls
                rank_desc -> {
                    getSorting().descending()
                    val text =  Option.monad().binding {
                        val state = getState().bind()
                        val map = getHardwareMap(state).bind()
                        makeHardwareUI(map.reversed())
                        getState().bind()
                    }.fix().fold(
                        {"Must be in a hardware group!"},
                        {"List sorted from lowest to highest rank"}
                    )

                    runOnUiThread {
                        toast(text)
                    }
                }
                rank_asc -> {
                    getSorting().ascending()

                    val text = Option.monad().binding {
                        val state = getState().bind()
                        val map = getHardwareMap(state).bind()
                        makeHardwareUI(map)
                        getState().bind()
                    }.fix().fold(
                        {"Must be in a hardware group!"},
                        {"List sorted from highest to lowest rank"}
                    )

                    runOnUiThread {
                        toast(text)
                    }
                }
            }
        }

        return true
    }

    private fun updateState(state: String) {
        if(state == "") {
            return
        }

        runOnUiThread {
            toolbar.title = state
        }
        setState(state)
    }

    private fun makeHardwareUI(list: List<Hardware>) {
        runOnUiThread {
            searchView.apply {
                setQuery("", false)
                isIconified = true
            }

            adapter.partsList = list
            recyclerView.adapter!!.notifyDataSetChanged()
        }
    }
}