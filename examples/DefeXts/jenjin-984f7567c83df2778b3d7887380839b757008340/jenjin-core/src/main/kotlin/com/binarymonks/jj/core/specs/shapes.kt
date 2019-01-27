package com.binarymonks.jj.core.specs

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array

/**
 * Some shapes that correlate to B2D shapes.
 */

interface ShapeSpec

class Rectangle constructor(
        var width: Float = 1f,
        var height: Float = 1f
) : ShapeSpec

class Circle constructor(
        var radius: Float = 1f
) : ShapeSpec

class Polygon constructor(
        var vertices: Array<Vector2> = Array()
) : ShapeSpec {
    constructor(vararg vertices: Vector2) : this() {
        vertices.forEach { this.vertices.add(it) }
    }
}

class Chain constructor(
        var vertices: Array<Vector2> = Array()
) : ShapeSpec {
    constructor(vararg vertices: Vector2) : this() {
        vertices.forEach { this.vertices.add(it) }
    }
}


