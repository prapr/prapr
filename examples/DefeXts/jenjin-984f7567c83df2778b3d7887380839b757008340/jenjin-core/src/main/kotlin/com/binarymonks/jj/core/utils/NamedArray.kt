package com.binarymonks.jj.core.utils

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap

class NamedArray<T> : Array<T>() {
    private val nameMap: ObjectMap<String, T> = ObjectMap()

    fun add(name: String, item: T) {
        add(item)
        nameMap.put(name, item)
    }

    fun get(name: String): T? {
        return nameMap[name]
    }

    fun removeName(name: String): Boolean {
        val removed = nameMap.remove(name)
        return removeValue(removed, true)
    }

    override fun removeAll(array: Array<out T>?, identity: Boolean): Boolean {
        return super.removeAll(array, identity)
    }

    override fun removeValue(value: T, identity: Boolean): Boolean {
        removeFromMap(value)
        return super.removeValue(value, identity)
    }

    override fun clear() {
        super.clear()
    }

    override fun pop(): T {
        val popped = super.pop()
        removeFromMap(popped)
        return popped
    }

    override fun removeIndex(index: Int) : T{
        val removed = super.removeIndex(index)
        removeFromMap(removed)
        return removed
    }

    private fun removeFromMap(value: T) {
        for (entry in nameMap) {
            if (entry.value == value) {
                nameMap.remove(entry.key)
            }
        }
    }


}