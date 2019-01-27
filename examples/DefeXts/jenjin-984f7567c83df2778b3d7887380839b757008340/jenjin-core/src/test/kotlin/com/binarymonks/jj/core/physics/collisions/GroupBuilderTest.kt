package com.binarymonks.jj.core.physics.collisions

import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.physics.CollisionData
import com.binarymonks.jj.core.physics.GroupBuilder
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.experimental.and


class GroupBuilderTest {

    lateinit var groupBuilder: GroupBuilder
    val collideWithNothing = "CollideWithNothing"
    val ACollidesWithBAndC = "ACollidesWithBAndC"
    val BCollidesWithA = "BCollidesWithA"
    val CCollidesWithAAndSelf = "CCollidesWithAAndSelf"
    val groups: ObjectMap<String, CollisionData> = ObjectMap()


    @Before
    fun setup() {
        groupBuilder = GroupBuilder()
        groupBuilder.group(collideWithNothing)
        groupBuilder.group(ACollidesWithBAndC).collidesWith(BCollidesWithA, CCollidesWithAAndSelf)
        groupBuilder.group(BCollidesWithA).collidesWith(ACollidesWithBAndC)
        groupBuilder.group(CCollidesWithAAndSelf).collidesWith(ACollidesWithBAndC, CCollidesWithAAndSelf)
        groups.clear()
        groupBuilder.build().forEach { groups.put(it.name, it.collisionData) }
    }

    @Test
    fun groupsThatShouldNotCollideWithAnythingDoNot() {
        assertCollision(collideWithNothing, collideWithNothing, false)
        assertCollision(collideWithNothing, ACollidesWithBAndC, false)
        assertCollision(collideWithNothing, BCollidesWithA, false)
        assertCollision(collideWithNothing, CCollidesWithAAndSelf, false)
    }

    @Test
    fun groupsThatCollideWithSomethingsDo(){
        assertCollision(ACollidesWithBAndC, ACollidesWithBAndC, false)
        assertCollision(ACollidesWithBAndC, BCollidesWithA, true)
        assertCollision(ACollidesWithBAndC, CCollidesWithAAndSelf, true)
    }

    @Test
    fun groupsThatCollideWithSomethingAndThemselvesDo(){
        assertCollision(CCollidesWithAAndSelf, CCollidesWithAAndSelf, true)
        assertCollision(CCollidesWithAAndSelf, ACollidesWithBAndC, true)
        assertCollision(CCollidesWithAAndSelf, BCollidesWithA, false)
    }

    private fun assertCollision(group1: String, group2: String, collides: Boolean) {
        Assert.assertEquals(collides, actualCollision(group1, group2))
    }

    private fun actualCollision(group1: String, group2: String): Boolean {
        val zero: Short = 0
        val col1 = groups[group1]
        val col2 = groups[group2]
        return (col1.mask and col2.category != zero && col2.mask and col1.category != zero)
    }


}