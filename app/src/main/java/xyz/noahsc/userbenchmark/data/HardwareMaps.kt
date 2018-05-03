package xyz.noahsc.userbenchmark.data

import android.content.res.AssetManager
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import com.google.gson.Gson
import java.io.InputStreamReader
import java.lang.reflect.Type

private val stringToMaps = HashMap<String, HashMap<String, Hardware>>()

private var state = ""
private var sorting = Sorting.ASCENDING

fun prepareMap(assetManager: AssetManager, hardwareClasses: Array<Pair<String, Type>>) {
    val parser = Gson()

    hardwareClasses.forEach {
        val input = assetManager.open(it.first)
        stringToMaps[it.first.substring(0..2)] = parser.fromJson(InputStreamReader(input), it.second) as HashMap<String, Hardware>
    }
}

fun getHardwareMap(type: String) = Option.fromNullable(stringToMaps[type])

fun getState() = if (state == "") None else Some(state)

fun getStateString() = getState().getOrElse { "" }

fun setState(s: String) {
    state = s
}

fun getSorting() = sorting

enum class Sorting {
    ASCENDING, DESCENDING;

    fun ascending() {
        sorting = ASCENDING
    }

    fun descending() {
        sorting = DESCENDING
    }
}