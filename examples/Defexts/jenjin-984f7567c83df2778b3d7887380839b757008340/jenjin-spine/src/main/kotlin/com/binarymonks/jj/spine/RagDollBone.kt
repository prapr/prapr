package com.binarymonks.jj.core.spine

import com.badlogic.gdx.math.MathUtils
import com.binarymonks.jj.spine.components.SpineBoneComponent
import com.esotericsoftware.spine.Bone
import com.esotericsoftware.spine.BoneData
import com.esotericsoftware.spine.Skeleton

import com.badlogic.gdx.math.MathUtils.cosDeg
import com.badlogic.gdx.math.MathUtils.sinDeg

class RagDollBone : Bone {

    internal var ragDoll = false
    internal var spinePart: SpineBoneComponent? = null

    constructor(data: BoneData, skeleton: Skeleton, parent: Bone?) : super(data, skeleton, parent)

    constructor(bone: Bone, skeleton: Skeleton, parent: Bone?) : super(bone, skeleton, parent)

    override fun getWorldX(): Float {
        if (ragDoll) {
            return spinePart!!.me().physicsRoot.position().x

        }
        return super.getWorldX()
    }

    override fun getWorldY(): Float {
        if (ragDoll) {
            return spinePart!!.me().physicsRoot.position().y
        }
        return super.getWorldY()
    }

    override fun getA(): Float {
        if (ragDoll) {
            return cosDeg(spinePart!!.me().physicsRoot.rotationR() * MathUtils.radDeg)
        }
        return super.getA()
    }

    override fun getB(): Float {
        if (ragDoll) {
            return cosDeg(spinePart!!.me().physicsRoot.rotationR() * MathUtils.radDeg + 90)
        }
        return super.getB()
    }

    override fun getC(): Float {
        if (ragDoll) {
            return sinDeg(spinePart!!.me().physicsRoot.rotationR() * MathUtils.radDeg)
        }
        return super.getC()
    }

    override fun getD(): Float {
        if (ragDoll) {
            return sinDeg(spinePart!!.me().physicsRoot.rotationR() * MathUtils.radDeg + 90)
        }
        return super.getD()
    }

    fun triggerRagDoll() {
        ragDoll = true
    }

    fun reverseRagDoll() {
        ragDoll = false
    }
}
