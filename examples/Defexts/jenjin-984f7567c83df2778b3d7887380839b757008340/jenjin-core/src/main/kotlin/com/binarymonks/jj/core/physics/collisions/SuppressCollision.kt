package com.binarymonks.jj.core.physics.collisions

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.binarymonks.jj.core.physics.CollisionHandler
import com.binarymonks.jj.core.scenes.Scene

/**
 * Does nothing and returns true. This will suppress collision handlers that follow this collision handler
 */
class SuppressCollision: CollisionHandler() {
    override fun collision(me: Scene, myFixture: Fixture, other: Scene, otherFixture: Fixture, contact: Contact): Boolean {
        return true
    }
}