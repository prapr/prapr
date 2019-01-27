package com.binarymonks.jj.core.async

/**
 * A Task lets you define some work that you want to do over a game loop, or several game loops.
 *
 * Hint: If you are creating a lot of the same shortlived task,
 * consider making it poolable, and recycling itself in teardown.
 */
interface Task {

    fun getReady()

    fun doWork()

    fun tearDown()

    fun isDone(): Boolean

}
