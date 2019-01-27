package com.binarymonks.jj.spine.collisions

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.async.OneTimeTask
import com.binarymonks.jj.core.physics.CollisionHandler
import com.binarymonks.jj.core.pools.Poolable
import com.binarymonks.jj.core.pools.new
import com.binarymonks.jj.core.pools.recycle
import com.binarymonks.jj.spine.components.SpineBoneComponent
import com.binarymonks.jj.core.scenes.Scene

class TriggerRagDollCollision : CollisionHandler() {

    var all = true
    var gravity = 1f

    override fun collision(me: Scene, myFixture: Fixture, other: Scene, otherFixture: Fixture, contact: Contact): Boolean {
        val boneComponent: SpineBoneComponent = checkNotNull(me.getComponent(SpineBoneComponent::class).first())
        JJ.tasks.addPostPhysicsTask(new(DelayedTriggerRagDoll::class).set(all, gravity, boneComponent, other))
        return false
    }

    override fun clone(): CollisionHandler {
        return TriggerRagDollCollision()
    }

    class DelayedTriggerRagDoll : OneTimeTask(), Poolable {
        internal var spineBone: SpineBoneComponent? = null
        internal var other: Scene? = null
        internal var all = false
        internal var gravity = 1f

        operator fun set(all: Boolean, gravity: Float, spineBoneComponent: SpineBoneComponent, other: Scene): DelayedTriggerRagDoll {
            this.spineBone = spineBoneComponent
            this.other = other
            this.all = all
            this.gravity = gravity
            return this
        }

        override fun doOnce() {
            if (all)
                spineBone!!.spineParent!!.triggerRagDoll(gravity)
            else
                spineBone!!.triggerRagDoll(gravity)
            recycle(this)
        }

        override fun reset() {
            spineBone = null
            other = null
            all = false
            gravity = 1f
        }
    }
}
