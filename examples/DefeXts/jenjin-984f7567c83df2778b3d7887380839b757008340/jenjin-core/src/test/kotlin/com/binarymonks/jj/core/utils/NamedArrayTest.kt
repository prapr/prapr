package com.binarymonks.jj.core.utils

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.lang.Exception


class NamedArrayTest {

    val name = "item1"
    val item = ArbitraryScene("me1")
    var testObj: NamedArray<ArbitraryScene> = NamedArray()

    @Before
    fun setUp() {
        testObj = NamedArray()
    }

    @Test
    fun addGetNamedItem() {
        testObj.add(name, item)

        Assert.assertEquals(testObj.get(name), item)
        Assert.assertTrue(testObj.contains(item))
    }

    @Test
    fun addRemoveNamedItem() {
        testObj.add(name, item)
        Assert.assertTrue(testObj.removeName(name))
        Assert.assertFalse(testObj.contains(item))
    }

    @Test
    fun addRemoveValueItem() {
        testObj.add(name, item)
        testObj.removeValue(item, true)

        Assert.assertNull(testObj.get(name))
    }

    @Test
    fun addPop() {
        testObj.add(name, item)
        testObj.pop()
        Assert.assertNull(testObj.get(name))
    }

    @Test
    fun addRemoveIndex() {
        testObj.add(name, item)
        testObj.removeIndex(0)
        Assert.assertNull(testObj.get(name))
    }


}

data class ArbitraryScene(val value: String)