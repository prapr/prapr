package com.binarymonks.jj.spine.specs

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.components.Component
import com.binarymonks.jj.core.physics.CollisionHandlers
import com.binarymonks.jj.core.specs.ShapeSpec
import com.binarymonks.jj.core.specs.physics.CollisionGroupSpec
import com.binarymonks.jj.core.specs.physics.CollisionGroupSpecExplicit


class SpineSkeletonSpec() {
    var boneWidth: Float = 0.1f
    var coreMass: Float = 0.5f
    var massFalloff = 0.5f
    var coreMotorTorque = 0.5f
    var coreMotorTorqueFalloff = 0.5f
    var all = All()
    var boundingBoxes = false
    internal val customs: ObjectMap<String, CustomBone> = ObjectMap()

    constructor(build: SpineSkeletonSpec.() -> Unit) : this() {
        this.build()
    }

    fun customize(boneName: String, build: CustomBone.() -> Unit) {
        val custom = CustomBone()
        custom.build()
        customs.put(boneName, custom)
    }
}

class All {
    var collisionGroup: CollisionGroupSpec = CollisionGroupSpecExplicit()
    var restitution = 0.2f
    var friction = 0.2f
    var material: String? = null

    var enableLimit: Boolean = false
    var lowerAngle: Float = 0f
    var upperAngle: Float = 30f
    var enableMotor: Boolean = true
    internal val components: Array<Component> = Array()
    val collisions = CollisionHandlers()
    internal val properties: ObjectMap<String, Any> = ObjectMap()
    fun <T : Component> component(component: T, init: T.() -> Unit) {
        component.init()
        components.add(component)
    }

    fun <T : Component> component(component: T) {
        components.add(component)
    }

    fun prop(name: String, value: Any? = null) {
        properties.put(name, value)
    }
}

class CustomBone {
    internal val boneOverride = BoneOverride()
    internal val components: Array<Component> = Array()
    internal val properties: ObjectMap<String, Any> = ObjectMap()
    val collisions = CollisionHandlers()

    fun override(build: BoneOverride.() -> Unit) {
        boneOverride.build()
    }

    fun <T : Component> component(component: T, init: T.() -> Unit) {
        component.init()
        components.add(component)
    }

    fun <T : Component> component(component: T) {
        components.add(component)
    }

    fun prop(name: String, value: Any? = null) {
        properties.put(name, value)
    }
}

class BoneOverride {
    var collisionGroup: CollisionGroupSpec? = null
    var enableLimit: Boolean? = null
    var lowerAngle: Float? = null
    var upperAngle: Float? = null
    var enableMotor: Boolean? = null
    var motorSpeed: Float? = null
    var maxMotorTorque: Float? = null

    var shape: ShapeSpec? = null
    var offsetX: Float? = null
    var offsetY: Float? = null
    var friction: Float? = null
    var restitution: Float? = null
    var mass: Float? = null
    var material: String? = null

}