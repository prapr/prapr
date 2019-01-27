package com.binarymonks.jj.core.extensions

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectSet
import com.binarymonks.jj.core.Copyable
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.pools.newObjectMap

/**
 * Copies the map with [Copyable] awareness for values. Uses pooled Object maps via [newObjectMap]
 */
fun <K, V> ObjectMap<K, V>.copy(): ObjectMap<K, V> {
    val clone: ObjectMap<K, V> = newObjectMap()
    for (entry in this) {
        val value = entry.value
        if (value is Copyable<*>) {
            @Suppress("UNCHECKED_CAST")
            clone.put(entry.key, value.clone() as V)
        } else {
            clone.put(entry.key, value)
        }
    }
    return clone
}

fun <K,V> ObjectMap<K,V>.recycle(){
    JJ.B.pools.recycle(this)
}

/**
 * Merges a map into this map. Keys will be overriden by the mergeFrom map.
 *
 * Returns the original
 */
fun <K, V> ObjectMap<K, V>.merge(mergeFrom: ObjectMap<K, V>): ObjectMap<K, V> {
    for (entry in mergeFrom) {
        this.put(entry.key, entry.value)
    }
    return this
}

/**
 * Lets you build your maps entries
 */
fun <K, V> ObjectMap<K, V>.build(builder: ObjectMap<K, V>.() -> Unit): ObjectMap<K, V> {
    this.builder()
    return this
}


/**
 * Copies the array with [Copyable] awareness for values
 */
fun <T> Array<T>.copy(): Array<T> {
    val clone: Array<T> = Array()
    this.forEach {
        if (it is Copyable<*>) {
            @Suppress("UNCHECKED_CAST")
            clone.add(it.clone() as T)
        } else {
            clone.add(it)
        }
    }
    return clone
}

/**
 * Lets you build your array
 */
fun <T> Array<T>.build(builder: Array<T>.() -> Unit): Array<T> {
    this.builder()
    return this
}

fun <T> Array<T>.addVar(vararg add: T): Array<T> {
    add.forEach { this.add(it) }
    return this
}


/**
 * Copies the set with [Copyable] awareness for values
 */
fun <T> ObjectSet<T>.copy(): ObjectSet<T> {
    val clone: ObjectSet<T> = ObjectSet()
    this.forEach {
        if (it is Copyable<*>) {
            @Suppress("UNCHECKED_CAST")
            clone.add(it.clone() as T)
        } else {
            clone.add(it)
        }
    }
    return clone
}

fun <T> ObjectSet<T>.addVar(vararg add: T): ObjectSet<T> {
    add.forEach { this.add(it) }
    return this
}



