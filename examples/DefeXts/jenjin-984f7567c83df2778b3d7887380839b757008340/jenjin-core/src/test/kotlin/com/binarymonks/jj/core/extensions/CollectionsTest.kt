package com.binarymonks.jj.core.extensions

import com.badlogic.gdx.utils.Array
import com.binarymonks.jj.core.input.mapping.InputMapper
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class CollectionsTest {

    @Test
    fun testArray_random() {
        val array: Array<Int> = Array()
        array.addVar(1, 2)

        var oneFound = false
        var twoFound = false
        for (i in 0..20) {
            when (array.random()) {
                1 -> oneFound = true
                2 -> twoFound = true
                else -> Unit
            }
        }
        Assert.assertTrue(oneFound)
        Assert.assertTrue(twoFound)
    }

}