package com.binarymonks.jj.core.scenes

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.async.Bond
import com.binarymonks.jj.core.audio.SoundEffects
import com.binarymonks.jj.core.components.Component
import com.binarymonks.jj.core.components.ComponentMaster
import com.binarymonks.jj.core.events.EventBus
import com.binarymonks.jj.core.events.ParamSubscriber
import com.binarymonks.jj.core.events.Subscriber
import com.binarymonks.jj.core.physics.PhysicsRoot
import com.binarymonks.jj.core.pools.Poolable
import com.binarymonks.jj.core.pools.new
import com.binarymonks.jj.core.pools.recycle
import com.binarymonks.jj.core.properties.HasProps
import com.binarymonks.jj.core.render.RenderRoot
import com.binarymonks.jj.core.specs.InstanceParams
import kotlin.reflect.KClass

/**
 * Scene Lifecycle                   |   Component lifecycle
 *
 *                                       * addToScene              - called once for component - unless Component is itself pooled.
 *
 *  * addToScene                                                   - called multiple times if scene is pooled
 *
 *  * addToWorld                         * addToWorld              - called multiple times if scene is pooled
 *
 *  * onRemoveFromWorld                  * onRemoveFromWorld       - called multiple times if scene is pooled
 *
 *                                       * onScenePooled           - called multiple times if scene is pooled
 *
 */
open class Scene(
        var name: String?,
        var specName: String?,
        var uniqueName: String?,
        val specID: Int,
        val scale: Vector2,
        val physicsRoot: PhysicsRoot,
        val renderRoot: RenderRoot,
        val soundEffects: SoundEffects,
        val properties: ObjectMap<String, Any>,
        val pooled: Boolean = false
) : HasProps {

    init {
        physicsRoot.parent = this
        renderRoot.parent = this
    }

    var id = JJ.B.nextID()

    var parent: Scene? = null
    internal var sceneLayers = ObjectMap<Int, Array<Scene>>(5)
    internal var sceneToLayerIndex = ObjectMap<Int, Int>()
    internal var nameChildren = ObjectMap<String, Scene>()
    internal var queuedForAddScenes = Array<AddScene>(100)
    internal var removals = Array<Scene>()
    private var componentMaster = ComponentMaster()
    internal var inUpdate = false
    private val eventBus = EventBus()
    var inWorld = false
    var isDestroyed = false

    fun addComponent(component: Component) {
        component.scene = this
        if (inWorld) {
            component.onAddToWorldWrapper()
        }
        componentMaster.addComponent(component)
    }

    fun <T : Component> getComponent(type: KClass<T>): Array<T> {
        return componentMaster.getComponent(type)
    }

    fun <T : Component> hasComponent(type: KClass<T>): Boolean {
        return getComponent(type).size > 0
    }


    override fun hasProp(key: String): Boolean {
        return properties.containsKey(key)
    }

    override fun getProp(key: String): Any? {
        return properties.get(key)
    }

    fun getChild(name: String): Scene? {
        return nameChildren.get(name)
    }

    fun parent(): Scene {
        return checkNotNull(parent)
    }

    fun getNode(path: ScenePath): Scene {
        return path.from(this)
    }


    fun onAddToWorld() {
        inWorld = true
        sceneLayers.forEach {
            it.value.forEach { it.onAddToWorld() }
        }
        physicsRoot.onAddToWorld()
        componentMaster.onAddToWorld()
    }

    fun apply(function: (Scene) -> Unit) {
        function(this)
        sceneLayers.forEach {
            it.value.forEach { it.apply(function) }
        }
    }


    fun add(scene: Scene, layer: Int = 0) {
        if (inUpdate) {
            queuedForAddScenes.add(new(AddScene::class).set(scene, layer))
        } else {
            reallyAdd(scene, layer)
        }
    }

    private fun reallyAdd(scene: Scene, layer: Int) {
        if (!sceneLayers.containsKey(layer)) {
            sceneLayers.put(layer, Array())
        }
        sceneLayers[layer].add(scene)
        sceneToLayerIndex.put(scene.id, layer)
        if (scene.name != null) {
            nameChildren.put(scene.name, scene)
        }
        scene.parent = this
        if (inWorld) {
            scene.onAddToWorld()
        }
    }

    open fun update() {
        clean()
        inUpdate = true
        componentMaster.update()
        sceneLayers.forEach {
            it.value.forEach { it.update() }
        }
        inUpdate = false
    }


    fun clean() {
        for (sceneEntry in queuedForAddScenes) {
            reallyAdd(sceneEntry.scene!!, sceneEntry.layer!!)
            recycle(sceneEntry)
        }
        for (removal in removals) {
            reallyRemove(removal)
        }
        queuedForAddScenes.clear()
    }

    fun remove(scene: Scene) {
        if (inUpdate) {
            removals.add(scene)
        } else {
            reallyRemove(scene)
        }
    }

    fun destroy() {
        if (!isDestroyed) {
            isDestroyed = true
            sceneLayers.forEach {
                it.value.forEach { it.isDestroyed = true }
            }
            parent?.remove(this)
        }
    }

    internal fun executeDestruction() {
        inWorld = false
        sceneLayers.forEach {
            it.value.forEach { it.executeDestruction() }
        }
        sceneLayers.forEach {
            it.value.clear()
        }
        renderRoot.destroy(pooled)
        physicsRoot.destroy(pooled)
        componentMaster.destroy()
        eventBus.clear()
        if (pooled) {
            JJ.B.scenes.masterFactory.scenePool.put(scale.x, scale.y, specID, this)
        }
    }

    internal fun resetFromPool(x: Float, y: Float, rotationD: Float) {
        physicsRoot.reset(x, y, rotationD)
        isDestroyed = false
        inUpdate = false
    }

    private fun reallyRemove(removal: Scene) {
        val layer = sceneToLayerIndex.get(removal.id)
        sceneLayers.remove(removal.id)
        if (removal.name != null) {
            nameChildren.remove(removal.name)
        }
        sceneLayers.get(layer).removeValue(removal, true)
        removal.executeDestruction()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Scene) return false

        if (name != other.name) return false
        if (uniqueName != other.uniqueName) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

    override fun toString(): String {
        return "Scene(name=$name, uniqueName=$uniqueName, id=$id)"
    }

    fun append(params: InstanceParams, scenePath: String): Bond<Scene> {
        return JJ.B.scenes.instantiate(params, scenePath, this)
    }

    fun register(channel: String, function: Subscriber): Int {
        return eventBus.register(channel, function)
    }

    fun register(channel: String, eventHandler: ParamSubscriber): Int {
        return eventBus.register(channel, eventHandler)
    }

    fun broadcast(channel: String, eventParams: ObjectMap<String, Any> = EventBus.NULLPARAMS, propagate: Boolean = false) {
        eventBus.send(channel, eventParams)
        if (propagate) {
            sceneLayers.forEach { it.value.forEach { it.broadcast(channel, eventParams, propagate) } }
        }
    }

    fun deregister(channel: String, registerID: Int) {
        eventBus.deregister(channel, registerID)
    }
}

class AddScene : Poolable {
    var scene: Scene? = null
    var layer: Int? = null

    fun set(scene: Scene, layer: Int): AddScene {
        this.scene = scene
        this.layer = layer
        return this
    }

    override fun reset() {
        scene = null
        layer = null
    }

}