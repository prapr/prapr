package com.binarymonks.jj.core.physics

import com.badlogic.gdx.utils.ObjectMap

/**
 * Lets you build a material system.
 *
 * So you can register materials with the physical properties you desire, and then use these
 * named materials for your [com.binarymonks.jj.core.specs.physics.FixtureSpec] by setting the material name.
 */
class Materials {

    private val mats: ObjectMap<String, Material> = ObjectMap()

    fun registerMaterial(name: String, restitution: Float = 0f, friction: Float = 0.1f, density: Float = 0.5f) {
        mats.put(name, Material(name, restitution, friction, density))
    }

    internal fun getMaterial(name: String): Material? {
        return mats[name]
    }

}

class Material(
        var name: String,
        var restitution: Float,
        var friction: Float,
        var density: Float
)