package com.binarymonks.jj.core.time

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.pools.Poolable
import com.binarymonks.jj.core.pools.new
import com.binarymonks.jj.core.pools.recycle


class Scheduler(val timeFunction: () -> Float) {
    var idCounter = 0
    val scheduledFunctions: ObjectMap<Int, FunctionTracker> = ObjectMap()
    val adds: ObjectMap<Int, FunctionTracker> = ObjectMap()
    private val removals: Array<Int> = Array()

    fun update() {
        val time = timeFunction.invoke()
        clean()
        scheduledFunctions.forEach {
            if (it.value.call(time)) {
                removals.add(it.key)
            }
        }
    }

    fun clean() {
        removals.forEach {
            if (scheduledFunctions.containsKey(it)) {
                recycle(scheduledFunctions.remove(it))
            }
        }
        removals.clear()
        adds.forEach {
            scheduledFunctions.put(it.key, it.value)
        }
        adds.clear()
    }

    fun schedule(
            function: () -> Unit,
            delaySeconds: Float,
            repeat: Int = 1,
            name: String? = null
    ): Int {
        return schedule(function, delaySeconds, delaySeconds, repeat, name)
    }

    fun schedule(
            function: () -> Unit,
            delayMinSeconds: Float,
            delayMaxSeconds: Float,
            repeat: Int = 1,
            name: String? = null
    ): Int {
        idCounter++

        adds.put(
                idCounter,
                new(FunctionTracker::class).set(function, delayMinSeconds, delayMaxSeconds, timeFunction.invoke(), repeat, idCounter, name)
        )
        return idCounter
    }

    fun cancel(id: Int) {
        if (scheduledFunctions.containsKey(id)) {
            removals.add(id)
        }
    }
}

val doNothing: () -> Unit = {}

class FunctionTracker : Poolable {

    var function: () -> Unit = doNothing
    var lastCall = 0f
    var delayMin = 0f
    var delayMax = 0f
    var actualDelay = 0f
    var repeat = 1
    var callCount = 0
    var scheduleID = -1
    var finished = false
    var name: String? = null


    fun call(time: Float): Boolean {
        val elapsed = time - lastCall
        if (elapsed >= actualDelay) {
            updateDelay()
            function.invoke()
            callCount++
            lastCall = time
        }
        if (repeat == 0) {
            return false
        }
        when (callCount) {
            repeat -> {
                finished = true
                return true
            }
            else -> return false
        }
    }


    override fun reset() {
        function = doNothing
        delayMin = 0f
        delayMax = 0f
        actualDelay = 0f
        lastCall = 0f
        repeat = 1
        callCount = 0
        finished = false
        name = null
    }

    fun set(function: () -> Unit, delayMin: Float, delayMax: Float, time: Float, repeat: Int, scheduleID: Int, name: String?): FunctionTracker {
        this.function = function
        this.delayMin = delayMin
        this.delayMax = delayMax
        this.lastCall = time
        this.repeat = repeat
        this.scheduleID = scheduleID
        this.name = name
        updateDelay()
        return this
    }

    private fun updateDelay() {
        if (delayMin == delayMax)
            actualDelay = delayMin
        else
            actualDelay = MathUtils.random(delayMin, delayMax)
    }
}
