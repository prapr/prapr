package com.binarymonks.jj.core.api

import com.binarymonks.jj.core.physics.CollisionGroups
import com.binarymonks.jj.core.physics.Materials


interface PhysicsAPI {
    /**
     * Lets you do physicsy stuff.
     */

    /**
     * This lets you configure collision groups for your game. What can collide with what.
     */
    var collisionGroups: CollisionGroups

    /**
     * This lets you define materials that can be used to quickly provide the properties of your physics Fixtures.
     */
    var materials: Materials
}