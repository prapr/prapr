package com.binarymonks.jj.core.async

abstract class PerpetualTask : Task {

    override fun tearDown() {}

    override fun isDone(): Boolean {
        return false
    }

}
