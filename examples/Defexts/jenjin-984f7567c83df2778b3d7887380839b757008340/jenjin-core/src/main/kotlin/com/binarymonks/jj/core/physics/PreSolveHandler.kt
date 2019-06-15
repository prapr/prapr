package com.binarymonks.jj.core.physics

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.Manifold
import com.binarymonks.jj.core.copy
import com.binarymonks.jj.core.scenes.Scene

abstract class PreSolveHandler {

    protected var ignoreProperties: Array<String> = Array()
    protected var matchProperties: Array<String> = Array()
    private var enabled = true

    init {
        init()
    }

    internal fun preSolveCollision(me: Scene, myFixture: Fixture,
                                   other: Scene, otherFixture: Fixture, contact: Contact, oldManifold: Manifold): Boolean {
        if (enabled) {
            val gameData = otherFixture.userData as PhysicsNode
            for (ignore in ignoreProperties) {
                if (gameData.hasProperty(ignore)) {
                    return false
                }
            }
            if (matchProperties.size > 0) {
                for (matchProp in matchProperties) {
                    if (gameData.hasProperty(matchProp)) {
                        return preSolve(me, myFixture, other, otherFixture, contact, oldManifold)
                    }
                }
            } else {
                return preSolve(me, myFixture, other, otherFixture, contact, oldManifold)
            }
        }
        return false
    }

    /**
     * Handle the collision, return True if propagation of the collision should stop. [CollisionHandler]s after this
     * will not be called if True is returned, including physics root [CollisionHandler]s
     */
    abstract fun preSolve(me: Scene, myFixture: Fixture, other: Scene, otherFixture: Fixture, contact: Contact, oldManifold: Manifold): Boolean

    open fun init() {

    }

    open fun clone(): PreSolveHandler {
        return copy(this)
    }

    fun disable() {
        enabled = false
    }

    fun enable() {
        init()
        enabled = true
    }

    open fun onAddToWorld() {

    }

    open fun onRemoveFromWorld() {

    }
}
