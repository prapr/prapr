package com.binarymonks.jj.core.pools

import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

/**
 * Some convenience constructors for widely used pooled objects.
 */

fun vec2(x: Float = 0f, y: Float = 0f): Vector2 {
    return new(Vector2::class).set(x, y)
}

fun vec3(x: Float = 0f, y: Float = 0f, z: Float = 0f): Vector3 {
    return new(Vector3::class).set(x, y, z)
}

fun mat3(): Matrix3 {
    return new(Matrix3::class)
}


