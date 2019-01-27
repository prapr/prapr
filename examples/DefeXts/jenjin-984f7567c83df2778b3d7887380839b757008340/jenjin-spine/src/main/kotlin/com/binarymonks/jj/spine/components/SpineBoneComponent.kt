package com.binarymonks.jj.spine.components

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.Array
import com.binarymonks.jj.core.components.Component
import com.binarymonks.jj.core.pools.new
import com.binarymonks.jj.core.pools.recycle
import com.binarymonks.jj.core.scenes.ScenePath
import com.binarymonks.jj.core.spine.RagDollBone
import com.binarymonks.jj.spine.render.SpineRenderNode
import com.binarymonks.jj.core.scenes.Scene
import com.esotericsoftware.spine.Bone

class SpineBoneComponent : Component() {

    internal var spineParent: SpineComponent? = null
    internal var bone: RagDollBone? = null
        set(value) {
            value!!.spinePart = this
            field = value
        }
    internal var ragDoll = false
    var bonePath: Array<String> = Array()

    internal fun setSpineComponent(spineParentScene: Scene) {
        spineParent = spineParentScene.getComponent(SpineComponent::class).first()
        val spineRender: SpineRenderNode = spineParentScene.renderRoot.getNode(SPINE_RENDER_NAME) as SpineRenderNode
        val boneNode = findMyBone(spineRender.skeleton.rootBone, 0) as RagDollBone
        bone = boneNode
        spineParent!!.addBone(bone!!.data.name, this)
    }

    fun spineComponent(): SpineComponent {
        return checkNotNull(spineParent)
    }

    private fun findMyBone(boneNode: Bone?, offset: Int): Bone {
        if (boneNode == null) {
            throw Exception("Could not find bone")
        }
        if (offset + 1 == bonePath.size) {
            return boneNode
        }
        boneNode.children.forEach {
            if (it.data.name == bonePath[offset + 1]) {
                return findMyBone(it, offset + 1)
            }
        }
        throw Exception("Could not find bone ${bonePath[offset]}")
    }

    private fun getRootNode(): Scene {
        val scenePath: ScenePath = new(ScenePath::class)
        for (i in 0..bonePath.size - 1) {
            scenePath.up()
        }
        val root = me().getNode(scenePath)
        recycle(scenePath)
        return root
    }

    fun applyToBones(sceneOperation: (Scene) -> Unit) {
        spineParent!!.applyToBones(sceneOperation)
    }

    override fun update() {
        updatePosition()
    }

    internal fun updatePosition() {
        if (!ragDoll && bone != null) {
            val x = bone!!.worldX
            val y = bone!!.worldY
            val rotation = bone!!.worldRotationX
            me().physicsRoot.b2DBody.setTransform(x, y, rotation * MathUtils.degRad)
        }
    }

    fun triggerRagDoll(gravity: Float = 1f) {
        if (bone == null) println("No bone $bonePath")
        if (!ragDoll && bone != null) {
            ragDoll = true
            bone!!.triggerRagDoll()
            scene!!.physicsRoot.b2DBody.type = BodyDef.BodyType.DynamicBody
            scene!!.physicsRoot.b2DBody.gravityScale = gravity
            bone!!.children.forEach {
                val name = it.data.name
                val boneComponent = me().getChild(name)!!.getComponent(SpineBoneComponent::class).first()
                boneComponent!!.triggerRagDoll(gravity)
            }
        }
    }

    fun reverseRagDoll() {
        if (ragDoll) {
            ragDoll = false
            bone!!.reverseRagDoll()
            me().physicsRoot.b2DBody.type = BodyDef.BodyType.StaticBody
            me().physicsRoot.b2DBody.gravityScale = 0f
            bone!!.children.forEach {
                val name = it.data.name
                val boneComponent = me().getChild(name)!!.getComponent(SpineBoneComponent::class).first()
                boneComponent!!.reverseRagDoll()
            }
        }
    }


}
