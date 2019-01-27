package com.binarymonks.jj.core.api

import com.binarymonks.jj.core.async.FunctionClosureBuilder
import com.binarymonks.jj.core.async.Task
import kotlin.reflect.KFunction

/**
 * Lets you add [com.binarymonks.jj.core.async.Task]s to parts of the game loop.
 */
interface TasksAPI {


    fun addPreLoopTask(task: Task)

    fun addPostPhysicsTask(task: Task)

    fun doOnceAfterPhysics(fn: () -> Unit)

    /**
     * Often you need to do something that involves modifying something in the physics world during the
     * physics step. With box2d this is not allowed, so you need to schedule something to happen after the
     * step. But, what if you need to have access to the context that you have now to pass into the function?
     *
     * This lets you capture a closure around the function that you want to call, but the closure will automatically
     * be recycled into a pool. No object creation to make GC sad.
     *
     * The function will then be called with the arguments in the closure.
     */
    fun doOnceAfterPhysicsCapture(function: KFunction<*>, build: (FunctionClosureBuilder.() -> Unit)? = null)

    fun addPrePhysicsTask(task: Task)

}
