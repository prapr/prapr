package com.binarymonks.jj.core.physics

import com.badlogic.gdx.physics.box2d.Filter
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.JJ


class PhysicsNode(
        var name: String?,
        var fixture: Fixture,
        var physicsRoot: PhysicsRoot,
        var properties: ObjectMap<String, Any>,
        var material: String?) {

    val collisionResolver: CollisionResolver = CollisionResolver()
    private var stashedCategory: Short = 0
    private var stashedMask: Short = 0
    private var stashed = false

    init {
        collisionResolver.parent = physicsRoot.collisionResolver
        physicsRoot.addNode(this)
    }

    fun hasProperty(propertyKey: String): Boolean {
        if (properties.containsKey(propertyKey)) return true
        return physicsRoot.hasProperty(propertyKey)
    }

    fun getProperty(propertyKey: String): Any? {
        if (properties.containsKey(propertyKey)) return properties[propertyKey]
        return physicsRoot.getProperty(propertyKey)
    }

    fun stashFilter(newCat: Short, newMask: Short) {
        if (!stashed) {
            stashedCategory = fixture.filterData.categoryBits
            stashedMask = fixture.filterData.maskBits
            val filter = Filter()
            filter.maskBits = newMask
            filter.categoryBits = newCat
            fixture.filterData = filter
            stashed = true
        }
    }

    fun stashFilter(newGroupName: String) {
        val collisionData = JJ.B.physicsWorld.collisionGroups.getCollisionData(newGroupName)
        stashFilter(collisionData.category, collisionData.mask)
    }

    fun unStashFilter() {
        if (stashed) {
            val filter = Filter()
            filter.maskBits = stashedMask
            filter.categoryBits = stashedCategory
            fixture.filterData = filter
            stashed = false
        }
    }
}