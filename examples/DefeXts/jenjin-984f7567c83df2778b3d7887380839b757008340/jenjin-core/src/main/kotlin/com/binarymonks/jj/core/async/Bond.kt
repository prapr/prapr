package com.binarymonks.jj.core.async

import com.binarymonks.jj.core.pools.Poolable


class UnitBond : Poolable {
    override fun reset() {
        callback = noCallback
    }

    private val noCallback: () -> Unit = { Unit }

    private var callback: () -> Unit = noCallback

    fun then(callback: () -> Unit) {
        this.callback = callback
    }

    fun succeed() {
        callback.invoke()
    }
}


class Bond<RESULT> : Poolable {

    private val noCallback: (RESULT) -> Unit = { _ : RESULT -> Unit }

    override fun reset() {
        callback = noCallback
    }

    private var callback: (RESULT) -> Unit = noCallback

    fun then(callback: (RESULT) -> Unit) {
        this.callback = callback
    }

    fun succeed(result: RESULT) {
        callback.invoke(result)
    }

}