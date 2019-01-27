package com.binarymonks.jj.core.physics

import com.badlogic.gdx.physics.box2d.Joint
import com.badlogic.gdx.physics.box2d.JointDef
import com.badlogic.gdx.physics.box2d.World
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.api.PhysicsAPI
import com.binarymonks.jj.core.async.Bond
import com.binarymonks.jj.core.async.OneTimeTask
import com.binarymonks.jj.core.pools.Poolable
import com.binarymonks.jj.core.pools.new
import com.binarymonks.jj.core.pools.recycle

open class PhysicsWorld(
        val b2dworld: World = World(JJ.B.config.b2d.gravity, true)
) : PhysicsAPI {


    var velocityIterations = 20
    var positionIterations = 20
    var stepping = false
    var stepReleased = false
    override var collisionGroups = CollisionGroups()
    override var materials = Materials()
    var isUpdating = false
        internal set

    init {
        b2dworld.setContactListener(JJContactListener())
    }

    fun update() {
        isUpdating = true
        val frameDelta = JJ.clock.deltaFloat
        if (frameDelta > 0) {
            b2dworld.step(frameDelta, velocityIterations, positionIterations)
        }
        isUpdating = false
    }

    /**
     * Lets you create joints during the physics step
     */
    fun createJoint(jointDef: JointDef): Bond<Joint> {
        @Suppress("UNCHECKED_CAST")
        val bond = new(Bond::class) as Bond<Joint>
        val delayedJoint = new(DelayedCreateJoint::class).set(jointDef, bond)
        if (isUpdating) {
            JJ.tasks.addPostPhysicsTask(delayedJoint)
        } else {
            JJ.tasks.addPrePhysicsTask(delayedJoint)
        }
        return bond
    }

    /**
     * Lets you destroy a joint during the physics step
     */
    fun destroyJoint(joint: Joint) {
        if (isUpdating) {
            JJ.tasks.addPostPhysicsTask(new(DelayedDestroyJoint::class).set(joint))
        } else {
            JJ.B.physicsWorld.b2dworld.destroyJoint(joint)
        }
    }

}

internal class DelayedCreateJoint : OneTimeTask(), Poolable {

    var jointDef: JointDef? = null
    var bond: Bond<Joint>? = null

    fun set(jointDef: JointDef, bond: Bond<Joint>): DelayedCreateJoint {
        this.jointDef = jointDef
        this.bond = bond
        return this
    }

    override fun doOnce() {
        val joint = JJ.B.physicsWorld.b2dworld.createJoint(jointDef)
        bond!!.succeed(joint)
        recycle(this)
    }

    override fun reset() {
        jointDef = null
        bond = null
    }

}

internal class DelayedDestroyJoint : OneTimeTask(), Poolable {

    var joint: Joint? = null

    fun set(joint: Joint): DelayedDestroyJoint {
        this.joint = joint
        return this
    }

    override fun doOnce() {
        JJ.B.physicsWorld.b2dworld.destroyJoint(joint)
        recycle(this)
    }

    override fun reset() {
        joint = null
    }

}
