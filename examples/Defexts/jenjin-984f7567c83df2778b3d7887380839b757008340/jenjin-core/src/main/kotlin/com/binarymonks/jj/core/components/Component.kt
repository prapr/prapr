package com.binarymonks.jj.core.components

import com.binarymonks.jj.core.Copyable
import com.binarymonks.jj.core.copy
import com.binarymonks.jj.core.properties.PROP_OVERRIDE_TYPE
import com.binarymonks.jj.core.properties.PropOverride
import com.binarymonks.jj.core.scenes.Scene
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties


abstract class Component : Copyable<Component>, ComponentLifecycle {

    private var inWorld = false

    open var scene: Scene? = null
        set(value) {
            field = value
            // Set all the property override fields to use the parent scene as property source.
            this::class.declaredMemberProperties.forEach {
                if (it.returnType.isSubtypeOf(PROP_OVERRIDE_TYPE)) {
                    val b = it.name
                    val pd = this.javaClass.kotlin.memberProperties.first { it.name == b }.get(this) as PropOverride<*>
                    pd.hasProps = value
                }
            }
        }

    fun me(): Scene {
        return checkNotNull(scene)
    }

    /**
     * This will be used to create a new instances of your component.
     *
     * Public fields of the component will be copied to the new instance.
     *
     * If it is not a primitive the reference will be shared by the clone unless it is [com.binarymonks.jj.core.Copyable], then clone will be called.
     *
     * If this is not sufficient then override this method.
     */
    override fun clone(): Component {
        return copy(this)
    }

    /**
     * Components are stored and retrieved by their type. It makes no sense to have more than
     * one component of the same type operating on a given [Scene]
     *
     * But you may have several implementations of a type. This lets you specify the key type (or top level interface) that
     * will be used to store and retrieve your [Component] if you need to.
     */
    @Suppress("UNCHECKED_CAST")
    open fun type(): KClass<Component> {
        return this::class as KClass<Component>
    }

    /**
     * This will be called on every game loop. Override this for ongoing tasks
     */
    override fun update() {

    }


    /**
     * You can also build short lived components which can be applied to a [Scene] and then identify themselves as
     * done when they have completed their task. They will then be removed from the [Scene]
     */
    open fun isDone(): Boolean {
        return false
    }

    internal fun onAddToWorldWrapper() {
        inWorld = true
        onAddToWorld()
    }


    /**
     * Called when the Component's parent Scene is added to the world,
     * or when the Component is added to a Scene that is already in the world.
     * The whole scene graph for that loop of the game cycle will be complete.
     */
    override fun onAddToWorld() {

    }

    internal fun onRemoveFromWorldWrapper() {
        inWorld = false
        onRemoveFromWorld()
    }

    /**
     * Called when the Component is removed from the scene,
     * or when the Scene is removed from the world.
     * This will also be called before being added to a pool (If the Scene is pooled)
     */
    override fun onRemoveFromWorld() {

    }

}