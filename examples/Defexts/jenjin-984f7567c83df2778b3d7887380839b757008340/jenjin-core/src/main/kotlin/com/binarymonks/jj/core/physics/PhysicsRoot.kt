package com.binarymonks.jj.core.physics

import box2dLight.Light
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Joint
import com.badlogic.gdx.physics.box2d.Transform
import com.badlogic.gdx.utils.Array
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.async.OneTimeTask
import com.binarymonks.jj.core.pools.Poolable
import com.binarymonks.jj.core.pools.new
import com.binarymonks.jj.core.scenes.Scene
import com.binarymonks.jj.core.utils.NamedArray

private val COLLISION_MASK_CACHE = "collision_mask_cache"
private val COLLISION_CAT_CACHE = "collision_cat_cache"
private val jointsToDestroy = Array<Joint>()
private val physicsPoolLocation = Vector2(10000f, 10000f)
private val NONE: Short = 0x0000

open class PhysicsRoot(val b2DBody: Body) {
    var parent: Scene? = null
        set(value) {
            field = value
            b2DBody.userData = value
            collisionResolver.me = value
            nodes.forEach { it.collisionResolver.me = value }
        }
    var nodes: NamedArray<PhysicsNode> = NamedArray()
    var collisionResolver: CollisionResolver = CollisionResolver()
    var lights: NamedArray<Light> = NamedArray()

    fun position(): Vector2 {
        return b2DBody.position
    }

    fun rotationR(): Float {
        return b2DBody.angle
    }

    fun setPosition(x: Float, y: Float) {
        b2DBody.setTransform(x, y, b2DBody.angle)
    }

    fun setPosition(position: Vector2) {
        b2DBody.setTransform(position.x, position.y, b2DBody.angle)
    }

    fun setRotationR(rotation: Float) {
        val position = b2DBody.position
        b2DBody.setTransform(position.x, position.y, rotation)
    }

    val transform: Transform
        get() = b2DBody.transform

    fun hasProperty(propertyKey: String): Boolean {
        if (parent != null) return parent!!.hasProp(propertyKey)
        return false
    }

    fun getProperty(propertyKey: String): Any? {
        if (parent != null) return parent!!.getProp(propertyKey)
        return null
    }

    internal fun destroy(pooled: Boolean) {
        collisionResolver.collisions.onRemoveFromWorld()
        if (!pooled) {
            JJ.B.physicsWorld.b2dworld.destroyBody(b2DBody)
        } else {
            neutralise()
        }
    }

    internal fun neutralise() {
        if (!JJ.B.physicsWorld.isUpdating) {
            for (node in nodes) {
                node.unStashFilter()
            }
            for (fixture in b2DBody.fixtureList) {
                val sceneNode = fixture.userData as PhysicsNode
                sceneNode.properties.put(COLLISION_CAT_CACHE, fixture.filterData.categoryBits)
                sceneNode.properties.put(COLLISION_MASK_CACHE, fixture.filterData.maskBits)
                val filterData = fixture.filterData
                filterData.categoryBits = NONE
                filterData.maskBits = NONE
                fixture.filterData = filterData
            }
            jointsToDestroy.clear()
            for (joint in b2DBody.jointList) {
                jointsToDestroy.add(joint.joint)
            }
            for (joint in jointsToDestroy) {
                JJ.B.physicsWorld.b2dworld.destroyJoint(joint)
            }
            jointsToDestroy.clear()
            b2DBody.setTransform(physicsPoolLocation, 0f)
            b2DBody.setLinearVelocity(Vector2.Zero)
            b2DBody.setAngularVelocity(0f)
            b2DBody.setActive(false)
            b2DBody.setAwake(false)

            for (light in lights) {
                light.isActive = false
            }
        } else {
            JJ.tasks.addPostPhysicsTask(
                    new(NeutralisePhysics::class)
                            .setRoot(this)
            )
        }
    }

    fun reset(x: Float, y: Float, rotationD: Float) {
        for (fixture in b2DBody.fixtureList) {
            val fixtureGameData = fixture.userData as PhysicsNode
            val filterData = fixture.filterData
            filterData.categoryBits = fixtureGameData.properties.get(COLLISION_CAT_CACHE) as Short
            filterData.maskBits = fixtureGameData.properties.get(COLLISION_MASK_CACHE) as Short
            fixture.filterData = filterData
        }
        b2DBody.setTransform(x, y, rotationD * MathUtils.degreesToRadians)
        b2DBody.isActive = true
        b2DBody.isAwake = true
        for (light in lights) {
            light.isActive = true
        }
    }

    fun addNode(physicsNode: PhysicsNode) {
        nodes.add(physicsNode)
    }

    fun getNode(name:String): PhysicsNode? {
        return nodes.get(name)
    }

    fun onAddToWorld() {
        collisionResolver.collisions.onAddToWorld()
    }

}

class NeutralisePhysics : OneTimeTask(), Poolable {

    internal var physicsRoot: PhysicsRoot? = null

    fun setRoot(physicsRoot: PhysicsRoot): NeutralisePhysics {
        this.physicsRoot = physicsRoot
        return this
    }


    override fun reset() {
        physicsRoot = null
    }

    override fun doOnce() {
        checkNotNull(physicsRoot).neutralise()
    }
}

