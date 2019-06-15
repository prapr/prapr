package com.binarymonks.jj.core.extensions

import com.badlogic.gdx.math.Matrix3
import com.binarymonks.jj.core.pools.mat3
import com.binarymonks.jj.core.pools.new

private var clear3 = floatArrayOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)

fun Matrix3.copy(): Matrix3 {
    return mat3().set(this)
}