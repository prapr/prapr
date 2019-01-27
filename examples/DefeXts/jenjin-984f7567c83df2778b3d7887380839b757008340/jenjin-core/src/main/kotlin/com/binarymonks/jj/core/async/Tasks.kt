package com.binarymonks.jj.core.async

import com.binarymonks.jj.core.api.TasksAPI
import com.binarymonks.jj.core.pools.Poolable
import com.binarymonks.jj.core.pools.new
import com.binarymonks.jj.core.pools.recycle
import kotlin.reflect.KFunction

class Tasks : TasksAPI {

    internal var preloopTasks = TaskMaster()
    internal var postPhysicsTasks = TaskMaster()
    internal var prePhysicsTasks = TaskMaster()

    override fun addPreLoopTask(task: Task) {
        preloopTasks.addTask(task)
    }

    override fun addPostPhysicsTask(task: Task) {
        postPhysicsTasks.addTask(task)
    }

    override fun doOnceAfterPhysics(fn: () -> Unit) {
        postPhysicsTasks.addTask(new(OneTimeFunctionWrapper::class).set(fn))
    }

    override fun doOnceAfterPhysicsCapture(function: KFunction<*>, build: (FunctionClosureBuilder.() -> Unit)?) {
        val functionCapture = capture(function, build)
        postPhysicsTasks.addTask(new(OneTimeFunctionWrapper::class).set(functionCapture::call))
    }

    override fun addPrePhysicsTask(task: Task) {
        prePhysicsTasks.addTask(task)
    }
}

internal class OneTimeFunctionWrapper : OneTimeTask(), Poolable {

    var function: (() -> Unit)? = null

    fun set(fn: () -> Unit): OneTimeFunctionWrapper {
        function = fn
        return this
    }

    override fun doOnce() {
        function!!()
    }

    override fun reset() {
        function = null
    }

    override fun tearDown() {
        super.tearDown()
        recycle(this)
    }
}
