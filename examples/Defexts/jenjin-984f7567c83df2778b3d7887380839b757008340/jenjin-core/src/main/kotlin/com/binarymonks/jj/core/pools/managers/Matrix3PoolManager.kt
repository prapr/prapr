package com.binarymonks.jj.core.pools.managers

import com.badlogic.gdx.math.Matrix3
import com.binarymonks.jj.core.pools.PoolManager

class Matrix3PoolManager : PoolManager<Matrix3> {

    override fun reset(pooled: Matrix3) {
        pooled.idt()
    }

    override fun create_new(): Matrix3 {
        return Matrix3()
    }

    override fun dispose(pooled: Matrix3) {

    }
}
