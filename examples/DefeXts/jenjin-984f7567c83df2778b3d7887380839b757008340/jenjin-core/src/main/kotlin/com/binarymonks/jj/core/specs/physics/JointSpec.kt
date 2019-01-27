package com.binarymonks.jj.core.specs.physics

import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Joint
import com.badlogic.gdx.physics.box2d.JointDef
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef
import com.binarymonks.jj.core.extensions.copy
import com.binarymonks.jj.core.pools.vec2

/**
 * Like a [com.badlogic.gdx.physics.box2d.JointDef] but with names of Scenes in a scene rather than bodies.
 *
 * @param nameA instance name of Body A. Leave null to use scene Body.
 * @param nameB instance name of Body B.
 */
abstract class JointSpec(
        val nameA: String?,
        val nameB: String
) {
    var collideConnected: Boolean = false

    abstract fun toJointDef(bodyA: Body, bodyB: Body, transform: Matrix3): JointDef
}

class RevoluteJointSpec(
        nameA: String?,
        nameB: String
) : JointSpec(nameA, nameB) {

    constructor(nameA: String?,
                nameB: String,
                anchor: Vector2) : this(nameA, nameB) {
        this.anchor = anchor
    }

    constructor(nameA: String?,
                nameB: String,
                localAnchorA: Vector2,
                localAnchorB: Vector2
    ) : this(nameA, nameB) {
        this.localAnchorB = localAnchorB
        this.localAnchorA = localAnchorA
    }

    var anchor: Vector2? = null
        private set
    var localAnchorA: Vector2? = null
        private set
    var localAnchorB: Vector2? = null
        private set
    var enableLimit = false
    var lowerAngle = 0f
    var upperAngle = 0f
    var enableMotor = false
    var motorSpeed = 0f
    var maxMotorTorque = 0f

    override fun toJointDef(bodyA: Body, bodyB: Body, transform: Matrix3): JointDef {
        val revJoint = RevoluteJointDef()
        if (anchor != null) {
            revJoint.initialize(bodyA, bodyB, anchor!!.copy().mul(transform))
        } else {
            revJoint.bodyA = bodyA
            revJoint.bodyB = bodyB
            revJoint.localAnchorA.set(localAnchorA!!.copy())
            revJoint.localAnchorB.set(localAnchorB!!.copy())
        }
        revJoint.enableLimit = enableLimit
        revJoint.lowerAngle = lowerAngle
        revJoint.upperAngle = upperAngle
        revJoint.enableMotor = enableMotor
        revJoint.motorSpeed = motorSpeed
        revJoint.maxMotorTorque = maxMotorTorque
        return revJoint
    }

}

class WeldJointSpec(
        nameA: String?,
        nameB: String,
        val anchor: Vector2
) : JointSpec(nameA, nameB) {

    var frequencyHz = 0f
    var dampingRatio = 0f

    override fun toJointDef(bodyA: Body, bodyB: Body, transform: Matrix3): JointDef {
        val weldJoint = WeldJointDef()
        weldJoint.initialize(bodyA, bodyB, anchor.copy().mul(transform))
        weldJoint.frequencyHz = frequencyHz
        weldJoint.dampingRatio = dampingRatio
        return weldJoint
    }
}

class PrismaticJointSpec(
        nameA: String?,
        nameB: String
) : JointSpec(nameA, nameB) {

    constructor(
            nameA: String?,
            nameB: String,
            localAnchorA: Vector2,
            localAnchorB: Vector2,
            localAxisA: Vector2
    ) : this(nameA, nameB) {
        this.localAnchorA = localAnchorA
        this.localAnchorB = localAnchorB
        this.localAxisA = localAxisA
    }

    /** The local anchor point relative to body1's origin.  */
    var localAnchorA: Vector2? = vec2()

    /** The local anchor point relative to body2's origin.  */
    var localAnchorB: Vector2? = vec2()

    /** The local translation axis in body1.  */
    var localAxisA: Vector2? = vec2(1f, 0f)

    /** The constrained angle between the bodies: body2_angle - body1_angle.  */
    var referenceAngle = 0f

    /** Enable/disable the joint limit.  */
    var enableLimit = false

    /** The lower translation limit, usually in meters.  */
    var lowerTranslation = 0f

    /** The upper translation limit, usually in meters.  */
    var upperTranslation = 0f

    /** Enable/disable the joint motor.  */
    var enableMotor = false

    /** The maximum motor torque, usually in N-m.  */
    var maxMotorForce = 0f

    /** The desired motor speed in radians per second.  */
    var motorSpeed = 0f

    override fun toJointDef(bodyA: Body, bodyB: Body, transform: Matrix3): JointDef {
        val prisJoint = PrismaticJointDef()
        prisJoint.bodyA = bodyA
        prisJoint.bodyB = bodyB
        prisJoint.localAnchorA.set(localAnchorA)
        prisJoint.localAnchorB.set(localAnchorB)
        prisJoint.localAxisA.set(localAxisA)
        prisJoint.referenceAngle = referenceAngle
        prisJoint.enableLimit = enableLimit
        prisJoint.lowerTranslation = lowerTranslation
        prisJoint.upperTranslation = upperTranslation
        prisJoint.enableMotor = enableMotor
        prisJoint.maxMotorForce = maxMotorForce
        prisJoint.motorSpeed = motorSpeed
        return prisJoint
    }
}
