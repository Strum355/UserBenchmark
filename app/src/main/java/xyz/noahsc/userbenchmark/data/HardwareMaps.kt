package xyz.noahsc.userbenchmark.data

import android.content.res.AssetManager
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import kotlin.reflect.KClass

private lateinit var stringToMaps: HashMap<String, HashMap<String, Hardware>>

private var state = ""

fun <T: Hardware> prepareMap(assetManager: AssetManager, hardwareClasses: Array<Pair<String, KClass<out T>>>) {
    val parser = Gson()

    hardwareClasses.forEach {
        val input = assetManager.open(it.first)
        val type = object : TypeToken<HashMap<String, T>>() {}.type
        stringToMaps[it.second.simpleName!!] = parser.fromJson(InputStreamReader(input), type) as java.util.HashMap<String, Hardware>
    }
}

fun getHardwareMap(type: String) = if (stringToMaps.containsKey(type)) Some(stringToMaps[type]!!) else None

fun getState() = if (state == "") None else Some(state)

fun setState(s: String) {
    state = s
}