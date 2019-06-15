package com.binarymonks.jj.spine.components

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.components.Component
import com.binarymonks.jj.core.scenes.Scene
import com.binarymonks.jj.core.scenes.ScenePath
import com.binarymonks.jj.spine.render.SpineRenderNode
import com.binarymonks.jj.spine.specs.SpineAnimations
import com.esotericsoftware.spine.AnimationState
import com.esotericsoftware.spine.AnimationStateData
import com.esotericsoftware.spine.Event
import com.esotericsoftware.spine.attachments.Attachment


val SPINE_RENDER_NAME = "SPINE_RENDER_NODE"

class SpineComponent(
        var animations: SpineAnimations
) : Component() {

    constructor() : this(SpineAnimations())

    internal var spineBoneComponents: ObjectMap<String, SpineBoneComponent> = ObjectMap()
    internal var rootBone: SpineBoneComponent? = null
    internal var animationListener = JJSpineAnimationStateListener(this)
    lateinit internal var spineRenderNode: SpineRenderNode
    lateinit internal var animationState: AnimationState
    internal var ragDoll = false
    var bonePaths: ObjectMap<String, ScenePath> = ObjectMap()
    private var hiddenSlotAttachments: ObjectMap<String, Attachment> = ObjectMap()


    override fun onAddToWorld() {
        spineRenderNode = me().renderRoot.getNode(SPINE_RENDER_NAME) as SpineRenderNode
        initialiseAnimations()
        bonePaths.forEach {
            it.value.from(me()).getComponent(SpineBoneComponent::class).first().setSpineComponent(me())
            if (it.value.path.size == 1) {
                rootBone = it.value.from(me()).getComponent(SpineBoneComponent::class).first()
            }
        }
        val shows = Array<String>()
        hiddenSlotAttachments.forEach {
            shows.add(it.key)
        }
        shows.forEach { show(it) }
        if (ragDoll) {
            reverseRagDoll()
        }
    }

    override fun update() {
        animationState.update(JJ.clock.deltaFloat)
        animationState.apply(spineRenderNode.skeleton)
    }


    private fun initialiseAnimations() {
        //FIXME: There must be a problem with pooling and doing this!
        val stateData = AnimationStateData(spineRenderNode.skeletonData)
        animations.crossFades.forEach { stateData.setMix(it.fromName, it.toName, it.duration) }
        animationState = AnimationState(stateData)
        if (animations.startingAnimation != null) {
            animationState.setAnimation(0, animations.startingAnimation, true)
        }
        animationListener.spineAnimations = animations
        animationState.addListener(animationListener)
    }

    fun animationState(): AnimationState {
        return animationState
    }

    fun applyToBones(sceneOperation: (Scene) -> Unit) {
        bonePaths.forEach {
            sceneOperation(it.value.from(me()))
        }
    }

    fun hide(slotName: String) {
        if (!hiddenSlotAttachments.containsKey(slotName)) {
            spineRenderNode.skeleton.slots.forEach {
                if (it.data.name == slotName) {
                    hiddenSlotAttachments.put(slotName, it.attachment)
                    it.attachment = null
                }
            }
        }
    }

    fun show(slotName: String) {
        if (hiddenSlotAttachments.containsKey(slotName)) {
            spineRenderNode.skeleton.slots.forEach {
                if (it.data.name == slotName) {
                    it.attachment = hiddenSlotAttachments.get(slotName)
                    hiddenSlotAttachments.remove(slotName)
                }
            }
        }
    }

    fun triggerRagDollBelow(rootBoneName: String, gravity: Float = 1f) {
        val component = spineBoneComponents.get(rootBoneName)
        component.triggerRagDoll(gravity)
    }

    internal fun addBone(name: String, spineBoneComponent: SpineBoneComponent) {
        spineBoneComponents.put(name, spineBoneComponent)
        spineBoneComponent.spineParent = this
    }

    fun getBone(name: String): SpineBoneComponent {
        return spineBoneComponents.get(name)
    }


    override fun onRemoveFromWorld() {
        spineBoneComponents.clear()
        animationState.removeListener(animationListener)
        animationListener.spineAnimations = null
    }

    fun myRender(): SpineRenderNode {
        return checkNotNull(spineRenderNode)
    }

    fun reverseRagDoll() {
        if (ragDoll) {
            ragDoll = false
            rootBone!!.reverseRagDoll()
        }
    }

    fun triggerRagDoll(gravity: Float = 1f) {
        if (!ragDoll) {
            ragDoll = true
            rootBone!!.triggerRagDoll(gravity)
        }
    }


}

class JJSpineAnimationStateListener(
        val spineComponent: SpineComponent,
        var spineAnimations: SpineAnimations? = null
) : AnimationState.AnimationStateListener {


    override fun start(entry: AnimationState.TrackEntry) {
    }

    override fun interrupt(entry: AnimationState.TrackEntry) {
    }

    override fun end(entry: AnimationState.TrackEntry) {
    }

    override fun dispose(entry: AnimationState.TrackEntry) {
    }

    override fun complete(entry: AnimationState.TrackEntry) {
    }

    override fun event(entry: AnimationState.TrackEntry, event: Event) {
        val name = event.data.name
        if (spineAnimations!!.handlers.containsKey(name)) {
            spineAnimations!!.handlers.get(name)(spineComponent, event)
        }
        if (spineAnimations!!.functions.containsKey(name)) {
            spineAnimations!!.functions.get(name)()
        }
        if (spineAnimations!!.componentHandlers.containsKey(name)) {
            val mapping = spineAnimations!!.componentHandlers.get(name)
            spineComponent.me().getComponent(mapping.componentType).forEach {
                mapping.componentFunction.invoke(it, event)
            }
        }
        if (spineAnimations!!.componentFunctions.containsKey(name)) {
            val mapping = spineAnimations!!.componentFunctions.get(name)
            spineComponent.me().getComponent(mapping.componentType).forEach {
                mapping.componentFunction.invoke(it)
            }
        }
    }

}