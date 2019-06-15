package com.binarymonks.jj.core.physics

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.binarymonks.jj.core.copy
import com.binarymonks.jj.core.scenes.Scene

abstract class CollisionHandler {

    protected var ignoreProperties: Array<String> = Array()
    protected var matchProperties: Array<String> = Array()
    private var enabled = true


    fun performCollision(me: Scene, myFixture: Fixture, other: Scene, otherFixture: Fixture, contact: Contact): Boolean {
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
                        return collision(me, myFixture, other, otherFixture, contact)
                    }
                }
            } else {
                return collision(me, myFixture, other, otherFixture, contact)
            }
        }
        return false
    }

    /**
     * Handle the collision, return True if propagation of the collision should stop. [CollisionHandler]s after this
     * will not be called if True is returned, including physics root [CollisionHandler]s
     */
    abstract fun collision(me: Scene, myFixture: Fixture, other: Scene, otherFixture: Fixture, contact: Contact): Boolean


    open fun clone(): CollisionHandler {
        return copy(this)
    }

    fun disable() {
        enabled = false
    }

    fun enable() {
        enabled = true
    }

    open fun onAddToWorld() {

    }

    open fun onRemoveFromWorld() {


    }
}
