package com.binarymonks.jj.core.specs

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.assets.AssetReference
import com.binarymonks.jj.core.components.Component
import com.binarymonks.jj.core.specs.physics.*
import com.binarymonks.jj.core.specs.render.RenderSpec

internal var sceneIDCounter = 0

open class SceneSpec() : SceneSpecRef {

    var id = sceneIDCounter++
    var name: String? = null
    internal var nodeCounter = 0
    var physics: PhysicsSpec = PhysicsSpec()
    var render: RenderSpec = RenderSpec()
    var sounds = SoundSpec()
    var components: Array<Component> = Array()
    var properties: ObjectMap<String, Any> = ObjectMap()
    var pooled: Boolean = true
    var nodes: ObjectMap<String, SceneNode> = ObjectMap()
    var joints: Array<JointSpec> = Array()
    var layer = 0
    var container = false

    constructor(init: SceneSpec.() -> Unit) : this() {
        this.init()
    }


    /**
     * Add a addNode [SceneSpecRef] instance
     *
     * @param scene The scene to instantiate
     * @param instanceParams The instance specific parameters
     */
    fun addNode(scene: SceneSpecRef, instanceParams: InstanceParams = InstanceParams.new()) {
        nodes.put(getName(instanceParams), SceneNode(scene, instanceParams))
    }

    /**
     * Add a addNode [SceneSpec] instance
     *
     * @param scenePath The path to the addNode[SceneSpec].
     * @param instanceParams The instance specific parameters
     */
    fun addNode(scenePath: String, instanceParams: InstanceParams = InstanceParams.new()) {
        nodes.put(getName(instanceParams), SceneNode(SceneSpecRefPath(scenePath), instanceParams))
    }

    private fun getName(instanceParams: InstanceParams): String? {
        return if (instanceParams.name == null) {
            "ANON${nodeCounter++}"
        } else {
            instanceParams.name
        }
    }

    fun prop(key: String, value: Any? = null) {
        properties.put(key, value)
    }

    fun node(instanceParams: InstanceParams = InstanceParams.new(), init: SceneSpec.() -> Unit): SceneSpec {
        val sceneSpec = SceneSpec()
        sceneSpec.init()
        this.addNode(sceneSpec, instanceParams)
        return sceneSpec
    }

    fun nodeRef(path: String) {
        this.addNode(path, InstanceParams.new())
    }

    fun nodeRef(instanceParams: InstanceParams, path: String) {
        this.addNode(path, instanceParams)
    }

    fun physics(init: PhysicsSpec.() -> Unit): PhysicsSpec {
        this.physics.init()
        return this.physics
    }

    fun render(init: RenderSpec.() -> Unit) {
        this.render.init()
    }


    fun <T : Component> component(component: T, init: T.() -> Unit) {
        component.init()
        this.components.add(component)
    }

    fun <T : Component> component(component: T) {
        this.components.add(component)
    }

    fun revJoint(nameA: String?, nameB: String, anchor: Vector2 = Vector2(), init: RevoluteJointSpec.() -> Unit) {
        val revJoint = RevoluteJointSpec(nameA, nameB, anchor)
        revJoint.init()
        this.joints.add(revJoint)
    }

    fun revJoint(nameA: String?, nameB: String, anchor: Vector2 = Vector2()) {
        val revJoint = RevoluteJointSpec(nameA, nameB, anchor)
        this.joints.add(revJoint)
    }

    fun revJoint(nameA: String?, nameB: String, localAnchorA: Vector2, localAnchorB: Vector2, init: RevoluteJointSpec.() -> Unit) {
        val revJoint = RevoluteJointSpec(nameA, nameB, localAnchorA, localAnchorB)
        revJoint.init()
        this.joints.add(revJoint)
    }

    fun prismaticJoint(nameA: String?, nameB: String, init: PrismaticJointSpec.() -> Unit) {
        val prisJoint = PrismaticJointSpec(nameA, nameB)
        prisJoint.init()
        this.joints.add(prisJoint)
    }

    fun weldJoint(nameA: String, nameB: String, anchor: Vector2, init: WeldJointSpec.() -> Unit) {
        val revJoint = WeldJointSpec(nameA, nameB, anchor)
        revJoint.init()
        this.joints.add(revJoint)
    }

    override fun resolve(): SceneSpec {
        return this
    }

    override fun getAssets(): Array<AssetReference> {
        val assets: Array<AssetReference> = Array()
        for (node in render.renderNodes) {
            assets.addAll(node.getAssets())
        }
        sounds.params.forEach {
            it.soundPaths.forEach {
                assets.add(AssetReference(Sound::class, it))
            }
        }
        nodes.forEach { assets.addAll(it.value.sceneRef!!.getAssets()) }
        return assets
    }

}

class SceneNode(
        val sceneRef: SceneSpecRef? = null,
        val instanceParams: InstanceParams = InstanceParams.new()
)

interface SceneSpecRef {
    fun resolve(): SceneSpec
    fun getAssets(): Array<AssetReference>
}

fun sceneRef(path: String): SceneSpecRef {
    return SceneSpecRefPath(path)
}

class SceneSpecRefPath(val path: String) : SceneSpecRef {

    override fun getAssets(): Array<AssetReference> {
        return Array()
    }

    override fun resolve(): SceneSpec {
        return JJ.B.scenes.getScene(path)
    }
}