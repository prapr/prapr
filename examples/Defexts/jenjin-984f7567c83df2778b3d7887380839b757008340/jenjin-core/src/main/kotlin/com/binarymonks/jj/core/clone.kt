package com.binarymonks.jj.core

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.ObjectSet
import com.binarymonks.jj.core.extensions.copy
import com.binarymonks.jj.core.properties.PROP_OVERRIDE_TYPE
import com.binarymonks.jj.core.properties.PropOverride
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

/**
 * Something that can make copies of itself. T should be a type or supertype of the implementer for this to make
 * sense of course.
 */
interface Copyable<T> {

    /**
     * The expectation for this function is that only fields that are visible and modifiable will be copied.
     * Use [copy] to achieve deep clones of [Copyable] implementations.
     */
    fun clone(): T
}


/**
 * Makes copies of things.
 *
 * Will try to copy any visible and modifiable properties.
 * Will call [Copyable.clone] on properties that implement it.
 * Will check members of the following collections for [Copyable]:
 *  - [com.badlogic.gdx.utils.ObjectMap]
 *  - [com.badlogic.gdx.utils.Array]
 *  - [com.badlogic.gdx.utils.ObjectSet]
 *
 */
fun <T : Any> copy(original: T): T {
    val myClass = original::class
    var copy = construct(myClass)
    myClass.memberProperties.forEach {
        try {
            val name = it.name
            if (it.visibility == KVisibility.PUBLIC && isPubliclyMutable(it)) {
                when {
                    original.javaClass.kotlin.memberProperties.first { it.name == name }.get(original) == null -> copyProperty(original, copy, name)
                    it.returnType.jvmErasure.isSubclassOf(Copyable::class) -> {
                        val sourceProperty = original.javaClass.kotlin.memberProperties.first { it.name == name }.get(original) as Copyable<*>
                        val destinationProperty = getMutable(copy, name)
                        destinationProperty.setter.call(copy, sourceProperty.clone())
                    }
                    it.returnType.jvmErasure.isSubclassOf(Array::class) -> {
                        val sourceProperty = original.javaClass.kotlin.memberProperties.first { it.name == name }.get(original) as Array<*>
                        val destinationProperty = getMutable(copy, name)
                        destinationProperty.setter.call(copy, sourceProperty.copy())
                    }
                    it.returnType.jvmErasure.isSubclassOf(ObjectMap::class) -> {
                        val sourceProperty = original.javaClass.kotlin.memberProperties.first { it.name == name }.get(original) as ObjectMap<*, *>
                        val destinationProperty = getMutable(copy, name)
                        destinationProperty.setter.call(copy, sourceProperty.copy())
                    }
                    it.returnType.jvmErasure.isSubclassOf(ObjectSet::class) -> {
                        val sourceProperty = original.javaClass.kotlin.memberProperties.first { it.name == name }.get(original) as ObjectSet<*>
                        val destinationProperty = getMutable(copy, name)
                        destinationProperty.setter.call(copy, sourceProperty.copy())
                    }
                    else -> copyProperty(original, copy, name)
                }
            }
            if (it.visibility == KVisibility.PUBLIC){
                @Suppress("UNCHECKED_CAST") // Doing ugly type erasure/overwrite hack to allow copying
                if(it.returnType.isSubtypeOf(PROP_OVERRIDE_TYPE)){
                    val sourceProperty = original.javaClass.kotlin.memberProperties.first { it.name == name }.get(original) as PropOverride<String>
                    val destinationProperty = copy.javaClass.kotlin.memberProperties.first { it.name == name }.get(copy) as PropOverride<String>
                    destinationProperty.copyFrom(sourceProperty)
                }
            }
        } catch (e: Exception) {
            throw Exception("Could not copy something", e)
        }
    }
    return copy
}

fun getMutable(instance: Any, name: String): KMutableProperty<*> {
    val property = instance.javaClass.kotlin.memberProperties.first { it.name == name }
    if (property is KMutableProperty<*>) {
        return property
    }
    throw Exception("Seems that property is not actually mutable")
}

fun isPubliclyMutable(property: KProperty1<*, *>): Boolean {
    if (property is KMutableProperty<*>) {
        if (property.setter.visibility == KVisibility.PUBLIC) {
            return true
        }
        return false
    } else {
        return false
    }
}

fun <T : Any> construct(kClass: KClass<T>): T {
    kClass.constructors.forEach {
        if (it.parameters.isEmpty()) {
            return it.call()
        } else {
            var allParamsHaveDefaults = true
            it.parameters.forEach {
                if (!it.isOptional) {
                    allParamsHaveDefaults = false
                }
            }
            if (allParamsHaveDefaults) {
                return it.callBy(emptyMap())
            }
        }
    }
    throw Exception("No empty or fully optional constructor")
}

fun copyProperty(source: Any, destination: Any, propertyName: String) {
    val sourceProperty = source.javaClass.kotlin.memberProperties.first { it.name == propertyName }.get(source)
    val destinationProperty = getMutable(destination, propertyName)
    destinationProperty.setter.call(destination, sourceProperty)
}
