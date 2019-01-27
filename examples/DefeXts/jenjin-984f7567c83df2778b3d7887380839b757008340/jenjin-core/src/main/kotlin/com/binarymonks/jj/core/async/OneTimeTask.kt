package com.binarymonks.jj.core.async

abstract class OneTimeTask : Task {

    override fun getReady() {
    }

    override fun doWork() {

    }

    override fun tearDown() {
        doOnce()
    }

    abstract fun doOnce()

    override fun isDone(): Boolean{
        return true
    }
}
