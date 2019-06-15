package com.binarymonks.jj.core.async

import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.pools.Poolable
import com.binarymonks.jj.core.pools.new
import com.binarymonks.jj.core.pools.recycle
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

/**
 * Provides a closure of a function with its arguments. Can only be called once, because it recycles itself.
 * This involves no object creation.
 */
fun capture(function: KFunction<*>, build: (FunctionClosureBuilder.() -> Unit)? = null): FunctionClosure {
    val functionCapture = new(FunctionClosure::class).capture(function, build)
    return functionCapture
}

/**
 * Provides a closure of a function with its arguments. Can only be called once, because it recycles itself.
 * This involves no object creation.
 */
class FunctionClosure : Poolable {

    internal val args: ObjectMap<String, Any?> = ObjectMap()
    internal val kparams: MutableMap<KParameter, Any?> = HashMap()
    internal var function: KFunction<*>? = null
    internal val argBuilder = FunctionClosureBuilder(this)

    fun capture(function: KFunction<*>, build: (FunctionClosureBuilder.() -> Unit)? = null): FunctionClosure {
        this.function = function
        if (build != null) {
            argBuilder.build()
        }
        return this
    }

    fun call() {
        function!!.parameters.forEach {
            if (args.containsKey(it.name)) {
                kparams.put(it, args[it.name])
            }
        }
        function!!.callBy(kparams)
        recycle(this)
    }

    override fun reset() {
        args.clear()
        kparams.clear()
        function = null
    }
}

class FunctionClosureBuilder(
        val functionClosure: FunctionClosure
) {
    fun withArg(name: String, value: Any?) {
        functionClosure.args.put(name, value)
    }
}