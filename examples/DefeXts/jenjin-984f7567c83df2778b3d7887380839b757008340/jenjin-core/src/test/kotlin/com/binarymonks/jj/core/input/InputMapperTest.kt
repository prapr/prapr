package com.binarymonks.jj.core.input

import com.badlogic.gdx.Input
import com.binarymonks.jj.core.input.mapping.Actions
import com.binarymonks.jj.core.input.mapping.InputMapper
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class InputMapperTest {

    lateinit var testObj: InputMapper
    val keyCode = Input.Keys.A

    @Before
    fun setUp() {
        testObj = InputMapper()
    }

    @Test
    fun mappedEventToHandler() {

        var wasPressed = false

        val press: () -> Boolean = {
            wasPressed = true
            false
        }

        testObj.map(keyCode, Actions.Key.RELEASED, press)

        testObj.keyDown(keyCode)
        Assert.assertFalse(wasPressed)

        testObj.keyUp(keyCode)
        Assert.assertTrue(wasPressed)

    }

    @Test
    fun mappedEventToKeyHandler() {
        var wasPressed = false

        val press: (Actions.Key) -> Boolean = {
            wasPressed = true
            false
        }

        testObj.map(keyCode, press)

        testObj.keyDown(keyCode)
        Assert.assertTrue(wasPressed)

        wasPressed = false

        testObj.keyUp(keyCode)
        Assert.assertTrue(wasPressed)
    }

    @Test
    fun mappedEventToAction_BindHandlerToAction() {
        val action = "Jump"
        var wasPressed = false

        val press: () -> Boolean = {
            wasPressed = true
            false
        }

        testObj.mapToAction(keyCode, Actions.Key.RELEASED, action)
        testObj.bindToAction(press, action)

        testObj.keyDown(keyCode)
        Assert.assertFalse(wasPressed)

        testObj.keyUp(keyCode)
        Assert.assertTrue(wasPressed)
    }


}