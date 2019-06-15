package com.binarymonks.jj.core.properties

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PropOverrideTest {

    val default: String = "default"
    val explicit: String = "explicit"
    val propertyValue: String = "propValue"
    val propertyKey: String = "propKey"
    val hasProps = StubHasProperties()

    @Before
    fun setUp() {
        hasProps.props[propertyKey] = propertyValue
    }

    @Test
    fun get_ValueNotSet_PropertyNotSet() {
        val propOverride = PropOverride<String>(default)
        propOverride.hasProps = hasProps

        Assert.assertEquals(default, propOverride.get())
    }

    @Test
    fun get_ValueSet_PropertyNotSet() {
        val propOverride = PropOverride(default)
        propOverride.hasProps = hasProps
        propOverride.set(explicit)

        Assert.assertEquals(explicit, propOverride.get())
    }

    @Test
    fun get_ValueNotSet_PropertySet_hasProp() {
        val propOverride = PropOverride(default)
        propOverride.hasProps = hasProps
        propOverride.setOverride(propertyKey)

        Assert.assertEquals(propertyValue, propOverride.get())
    }

    @Test
    fun get_ValueNotSet_PropertySet_doesNotHaveProp() {
        val propOverride = PropOverride(default)
        propOverride.hasProps = hasProps
        propOverride.setOverride("someOtherPropertyKey")

        Assert.assertEquals(default, propOverride.get())
    }

    @Test
    fun get_ValueSetThenPropertySet_hasProp() {
        val propOverride = PropOverride(default)
        propOverride.hasProps = hasProps
        propOverride.set(explicit)
        propOverride.setOverride(propertyKey)

        Assert.assertEquals(propertyValue, propOverride.get())
    }

    @Test
    fun get_ValueSetThenPropertySet_doesNotHaveProp() {
        val propOverride = PropOverride(default)
        propOverride.hasProps = hasProps
        propOverride.set(explicit)
        propOverride.setOverride("someOtherPropertyKey")

        Assert.assertEquals(default, propOverride.get())
    }

    @Test
    fun get_PropertySetThenValueSet_hasProp() {
        val propOverride = PropOverride(default)
        propOverride.hasProps = hasProps
        propOverride.setOverride(propertyKey)
        propOverride.set(explicit)

        Assert.assertEquals(explicit, propOverride.get())
    }

    @Test
    fun get_PropertySetThenValueSet_doesNotHaveProp() {
        val propOverride = PropOverride(default)
        propOverride.hasProps = hasProps
        propOverride.setOverride("someOtherPropertyKey")
        propOverride.set(explicit)

        Assert.assertEquals(explicit, propOverride.get())
    }


}