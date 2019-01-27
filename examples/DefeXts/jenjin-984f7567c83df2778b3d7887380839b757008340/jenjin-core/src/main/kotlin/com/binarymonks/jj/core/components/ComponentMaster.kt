package com.binarymonks.jj.core.components

import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.Array
import kotlin.reflect.KClass

class ComponentMaster {

    private var trackedComponents = ObjectMap<KClass<*>, Array<Component>>(2)
    private var addTrackedComponent = ObjectMap<KClass<*>, Array<Component>>(2)
    private var removeTrackedComponents = ObjectMap<KClass<*>, Array<Component>>(2)

    fun update() {
        clean()
        for (entry in trackedComponents.entries()) {
            for (component in entry.value) {
                if (component.isDone()) {
                    if (!removeTrackedComponents.containsKey(entry.key))
                        removeTrackedComponents.put(entry.key, Array())
                    removeTrackedComponents.get(entry.key).add(component)
                } else {
                    component.update()
                }
            }
        }
    }

    fun clean() {
        for (entry in removeTrackedComponents.entries()) {
            for (component in entry.value) {
                component.onRemoveFromWorldWrapper()
                trackedComponents.get(entry.key).removeValue(component, true)
            }
        }
        removeTrackedComponents.clear()
        for (entry in addTrackedComponent.entries()) {
            if (trackedComponents.containsKey(entry.key)) {
                trackedComponents.get(entry.key).addAll(entry.value)
            } else {
                trackedComponents.put(entry.key, entry.value)
            }
        }
        addTrackedComponent.clear()
    }

    fun addComponent(component: Component) {
        if (!component.type().java.isAssignableFrom(component.javaClass)) {
            throw Exception("Your component ${component::class.simpleName} is not an instance of its type")
        }
        if (!addTrackedComponent.containsKey(component.type())) {
            addTrackedComponent.put(component.type(), Array(1))
        }
        addTrackedComponent.get(component.type()).add(component)
    }

    fun <T : Component> getComponent(type: KClass<T>): Array<T> {
        val result: Array<T> = Array()
        if (trackedComponents.containsKey(type)) {
            @Suppress("UNCHECKED_CAST")
            result.addAll(trackedComponents.get(type) as Array<T>)
        }
        if (addTrackedComponent.containsKey(type)) {
            @Suppress("UNCHECKED_CAST")
            result.addAll(addTrackedComponent.get(type) as Array<T>)
        }
        return result
    }

    fun onAddToWorld() {
        clean()
        trackedComponents.forEach { it.value.forEach { it.onAddToWorldWrapper() } }
    }

    fun destroy() {
        clean()
        trackedComponents.forEach { it.value.forEach { it.onRemoveFromWorldWrapper() } }
    }

}
