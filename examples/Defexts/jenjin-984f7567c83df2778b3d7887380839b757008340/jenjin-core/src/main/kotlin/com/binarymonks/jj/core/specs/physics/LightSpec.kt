package com.binarymonks.jj.core.specs.physics

import com.badlogic.gdx.graphics.Color
import com.binarymonks.jj.core.properties.PropOverride


abstract class LightSpec {
    var name: String? = null
    var rays: Int = 100
    var color: PropOverride<Color> = PropOverride(Color(0.3f, 0.3f, 0.3f, 1f))
    var reach: Float = 2f
    var collisionGroup :CollisionGroupSpec = CollisionGroupSpecExplicit()
}

class PointLightSpec:LightSpec() {
    var offsetX: Float = 0f
    var offsetY: Float = 0f
}