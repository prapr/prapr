package com.binarymonks.jj.core.components

import com.binarymonks.jj.core.mockoutGDXinJJ
import com.binarymonks.jj.core.properties.PropOverride
import com.binarymonks.jj.core.testScene
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ComponentTest {

    @Before
    fun setUp(){
        mockoutGDXinJJ()
    }

    @Test
    fun setScene_injectsSceneIntoPropOverrides(){

        val testScene = testScene()

        val myComponent = MyComponent()

        myComponent.scene=testScene

        Assert.assertSame(testScene,myComponent.myPropOverrideVal.hasProps)
        Assert.assertSame(testScene,myComponent.myPropOverrideVar.hasProps)
    }
}


class MyComponent:Component(){

    val myPropOverrideVal = PropOverride("originalVal")
    var myPropOverrideVar = PropOverride("originalVar")
}