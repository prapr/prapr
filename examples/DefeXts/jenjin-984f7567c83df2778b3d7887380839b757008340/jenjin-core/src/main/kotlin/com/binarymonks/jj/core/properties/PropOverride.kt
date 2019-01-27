package com.binarymonks.jj.core.properties

import com.binarymonks.jj.core.Copyable
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType

val PROP_OVERRIDE_TYPE = PropOverride::class.createType(listOf(KTypeProjection(null, null)))

/**
 * Used to indicate fields that can be set by value or by looking up a property of the runtime scene.
 *
 * Initialised with a default. This default can be null if the type T is nullable.
 *
 * An explicit value can be set using [PropOverride.set]. This will nullify the property key set by [PropOverride.setOverride].
 *
 * A property override key can be set using [PropOverride.setOverride]. This will nullify the value set by [PropOverride.set].
 *
 * Use [PropOverride.get] for run time use of the resultant actual value.
 */
class PropOverride<T>(var default: T) : Copyable<PropOverride<T>> {

    private var value: T? = null

    private var propOverrideKey: String? = null

    internal var hasProps: HasProps? = null

    /**
     * Set to an explicit value - do not delegate to a property.
     */
    fun set(value: T) {
        this.value = value
        this.propOverrideKey = null
    }

    /**
     * Set the key of the property to refer to.
     *
     * Will fall back to default if no property for the key is found.
     */
    fun setOverride(propertyKey: String) {
        this.propOverrideKey = propertyKey
        this.value = null
    }

    /**
     * Get the computed value.
     *
     * This will be whichever was called last between [PropOverride.set]/[PropOverride.setOverride], if they are not null.
     * Falling back to the default if they are null or not set.
     */
    fun get(): T {
        if (propOverrideKey != null && hasProps != null && hasProps!!.hasProp(propOverrideKey!!)) {
            @Suppress("UNCHECKED_CAST")
            return hasProps!!.getProp(propOverrideKey!!) as T
        }
        if (value != null) {
            return value!!
        }
        return default
    }

    /**
     * Get the actual value with a specific set of properties
     */
    fun get(hasProps: HasProps): T {
        this.hasProps = hasProps
        return get()
    }

    override fun clone(): PropOverride<T> {
        val clone = PropOverride(default)
        clone.propOverrideKey = propOverrideKey
        clone.value = value
        return clone
    }

    fun copyFrom(original: PropOverride<T>) {
        this.propOverrideKey = original.propOverrideKey
        this.value = original.value
        this.default = original.default
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PropOverride<*>

        if (default != other.default) return false
        if (value != other.value) return false
        if (propOverrideKey != other.propOverrideKey) return false

        return true
    }

    override fun hashCode(): Int {
        var result = default?.hashCode() ?: 0
        result = 31 * result + (value?.hashCode() ?: 0)
        result = 31 * result + (propOverrideKey?.hashCode() ?: 0)
        return result
    }


}


