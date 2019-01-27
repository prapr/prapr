package com.binarymonks.jj.core.components

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.reflect.KClass


class ComponentMasterTest {

    lateinit var componentMaster: ComponentMaster
    val componentA = ComponentA()
    val componentASubtype = ComponentASubType()
    val componentB = ComponentB()

    @Before
    fun setUp() {
        componentMaster = ComponentMaster()
    }

    @Test
    fun addAndGetMultipleComponentsOfSameType() {
        componentMaster.addComponent(componentA)
        componentMaster.addComponent(componentASubtype)
        componentMaster.addComponent(componentB)

        val actualComponents = componentMaster.getComponent(ComponentA::class)

        Assert.assertTrue(actualComponents.contains(componentA))
        Assert.assertTrue(actualComponents.contains(componentASubtype))
        Assert.assertTrue(actualComponents.size == 2)
    }

}


open class ComponentA : Component() {


    override fun type(): KClass<Component> {
        @Suppress("UNCHECKED_CAST")
        return ComponentA::class as KClass<Component>
    }
}

class ComponentASubType : ComponentA()

class ComponentB : Component()

