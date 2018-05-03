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
import arrow.typeclasses.binding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.parts_list_row.view.*
import kotlinx.coroutines.experimental.*
import xyz.noahsc.userbenchmark.data.*
import org.jetbrains.anko.*
import xyz.noahsc.userbenchmark.R
import xyz.noahsc.userbenchmark.R.color.cardview_light_background
import xyz.noahsc.userbenchmark.R.id.*
import xyz.noahsc.userbenchmark.R.layout.activity_main
import xyz.noahsc.userbenchmark.R.string.*
import xyz.noahsc.userbenchmark.listener.ClickListener
import xyz.noahsc.userbenchmark.listener.RecyclerItemClickListener
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val queryListener = object : SearchView.OnQueryTextListener {

        override fun onQueryTextChange(newText: String?): Boolean {
            val hardwareMap = getHardwareMap(getStateString())

            if (newText == null || hardwareMap == None) {
                return false
            }

            val searched = searchForSubstring(ArrayList(hardwareMap.orNull()!!.values), newText).sorted()
            when (getSorting()) {
                Sorting.ASCENDING -> recyclerView.adapter = DataAdapter(ArrayList(searched))
                Sorting.DESCENDING -> recyclerView.adapter = DataAdapter(ArrayList(searched.reversed()))
            }
            return false
        }

        override fun onQueryTextSubmit(newText: String?): Boolean {
            val hardwareMap = getHardwareMap(getStateString())

            if (newText == null || hardwareMap == None) {
                return false
            }

            val searched = searchForSubstring(ArrayList(hardwareMap.orNull()!!.values), newText).sorted()
            when (getSorting()) {
                Sorting.ASCENDING -> recyclerView.adapter = DataAdapter(ArrayList(searched))
                Sorting.DESCENDING -> recyclerView.adapter = DataAdapter(ArrayList(searched.reversed()))
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
                        cv.setCardBackgroundColor(ContextCompat.getColor(cv.context, R.color.selected))
                        recyclerView.adapter.notifyItemChanged(position)
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
                return getHardwareMap(getStateString()).orNull()!![splitText[1]]
            }
        }))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /*
            1 = from details
            2 = from comparison
         */
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
            drawer.isDrawerOpen(GravityCompat.START) -> drawer.closeDrawer(GravityCompat.START)
            !drawer.isDrawerOpen(GravityCompat.START) -> drawer.openDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer.closeDrawer(GravityCompat.START)

        //TODO clean up this godawful mess

        launch(CommonPool) {
            when (item.itemId) {
                home -> {
                    recyclerView.adapter = null
                    updateState("")
                    ComparisonData.setCompareFirst(null)
                }
                cpu -> {
                    updateState("CPU")
                    makeHardwareUI(ArrayList(getHardwareMap(getStateString()).orNull()!!.values.sorted()))
                    ComparisonData.setCompareFirst(null)
                }
                gpu -> {
                    updateState("GPU")
                    makeHardwareUI(ArrayList(getHardwareMap(getStateString()).orNull()!!.values.sorted()))
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
                        makeHardwareUI(ArrayList(map.values.sorted().reversed()))
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
                        makeHardwareUI(ArrayList(map.values.sorted()))
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

        toolbar.title = state
        setState(state)
    }

    private fun makeHardwareUI(list: ArrayList<Hardware>) {
        runOnUiThread {
            searchView.apply {
                setQuery("", false)
                isIconified = true
            }

            recyclerView.adapter = DataAdapter(list)
        }
    }
}