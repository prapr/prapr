package com.binarymonks.jj.core.physics

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.utils.Array
import com.binarymonks.jj.core.scenes.Scene

class CollisionResolver {

    var me: Scene? = null
    var parent: CollisionResolver? = null
    val collisions = CollisionHandlers()
    var collisionCount = 0
        protected set

    fun preSolveContact(otherScene: Scene, otherFixture: Fixture, contact: Contact, myFixture: Fixture, oldManifold: Manifold) {
        var propogate = true
        for (function in collisions.preSolves) {
            if (function.preSolveCollision(checkNotNull(me), myFixture, otherScene, otherFixture, contact, oldManifold)) {
                propogate = false
                break
            }
        }
        if (propogate && parent != null) {
            parent!!.preSolveContact(otherScene, otherFixture, contact, myFixture, oldManifold)
        }
    }

    fun beginContact(otherObject: Scene, otherFixture: Fixture, contact: Contact, myFixture: Fixture) {
        collisionCount++
        var propogate = true
        for (function in collisions.begins) {
            if (function.performCollision(checkNotNull(me), myFixture, otherObject, otherFixture, contact)) {
                propogate = false
                break
            }
        }
        if (propogate && parent != null) {
            parent!!.beginContact(otherObject, otherFixture, contact, myFixture)
        }
    }

    fun finalBeginContact(otherObject: Scene, otherFixture: Fixture, contact: Contact, myFixture: Fixture) {
        var propogate = true
        for (function in collisions.finalBegins) {
            if (function.performCollision(checkNotNull(me), myFixture, otherObject, otherFixture, contact)) {
                propogate = false
                break
            }
        }
        if (propogate && parent != null) {
            parent!!.finalBeginContact(otherObject, otherFixture, contact, myFixture)
        }
    }

    fun endContact(otherObject: Scene, otherFixture: Fixture, contact: Contact, myFixture: Fixture) {
        var propogate = true
        for (function in collisions.ends) {
            if (function.performCollision(checkNotNull(me), myFixture, otherObject, otherFixture, contact)) {
                propogate = false
                break
            }
        }
        if (propogate && parent != null) {
            parent!!.endContact(otherObject, otherFixture, contact, myFixture)
        }
    }

    fun postSolveContact(otherScene: Scene, otherFixture: Fixture, contact: Contact, myFixture: Fixture, impulse: ContactImpulse) {
        var propogate = true
        for (function in collisions.postSolves) {
            if (function.postSolveCollision(checkNotNull(me), myFixture, otherScene, otherFixture, contact, impulse)) {
                propogate = false
                break
            }
        }
        if (propogate && parent != null) {
            parent!!.postSolveContact(otherScene, otherFixture, contact, myFixture, impulse)
        }
    }

    fun addInitialBegin(collision: CollisionHandler) {
        collisions.begins.add(collision)
    }

    fun addFinalBegin(collision: CollisionHandler) {
        collisions.finalBegins.add(collision)
    }

    fun addEnd(collision: CollisionHandler) {
        collisions.ends.add(collision)
    }


    /**
     * Will disable every [CollisionHandler].
     */
    fun disableCurrentCollisions() {
        collisions.preSolves.forEach { it.disable() }
        collisions.begins.forEach { it.disable() }
        collisions.finalBegins.forEach { it.disable() }
        collisions.ends.forEach { it.disable() }
        collisions.postSolves.forEach { it.disable() }
    }


    /**
     * Will enable every [CollisionHandler].
     */
    fun enableCurrentCollisions() {
        collisions.preSolves.forEach { it.enable() }
        collisions.begins.forEach { it.enable() }
        collisions.finalBegins.forEach { it.enable() }
        collisions.ends.forEach { it.enable() }
        collisions.postSolves.forEach { it.enable() }
    }


}
