package me.theseems.tomshelby.mentionpack.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Reader
import java.io.Writer

inline fun <reified K, reified V> Gson.readMap(reader: Reader): MutableMap<K, V> =
    fromJson(reader, object : TypeToken<MutableMap<K, V>>() {}.type)

inline fun <reified K, reified V> Gson.writeMapAndFlush(map: Map<K, V>, writer: Writer) {
    toJson(map, writer)
    writer.flush()
}
