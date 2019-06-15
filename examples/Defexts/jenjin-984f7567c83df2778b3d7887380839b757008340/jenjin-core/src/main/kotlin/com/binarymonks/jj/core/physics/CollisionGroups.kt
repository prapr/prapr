package com.binarymonks.jj.core.physics

import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.Array
import kotlin.experimental.or

/**
 * This is needed to check Collision groups of your game objects. This will determine what collides with what.
 */
class CollisionGroups {

    private var groups: ObjectMap<String, CollisionGroup> = ObjectMap()

    /**
     * Add a collision group
     */
    fun addGroup(group: CollisionGroup) {
        groups.put(group.name, group)
    }

    fun getCollisionData(name: String): CollisionData {
        if (groups.containsKey(name)) {
            return groups.get(name).collisionData
        }
        throw Exception("No Collision group for $name")
    }

    /**
     * A helper for building your collision groups.
     * You can create a maximum of 15 distinct groups.
     */
    fun buildGroups(init: GroupBuilder.() -> Unit): GroupBuilder {
        val groupBuilder = GroupBuilder()
        groupBuilder.init()
        groupBuilder.build().forEach {
            addGroup(it)
        }
        return groupBuilder
    }

}

class Group(var name: String) {
    var colliderGroupNames: Array<String> = Array()

    fun collidesWith(vararg groupNames: String) {
        groupNames.forEach {
            colliderGroupNames.add(it)
        }
    }
}


class GroupBuilder {

    private var groups: Array<Group> = Array()

    fun group(name: String): Group {
        if (groups.size == 15) throw Exception("Sorry, you can only create 15 collision groups")
        val group = Group(name)
        groups.add(group)
        return group
    }

    fun build(): Array<CollisionGroup> {
        val builtGroups: Array<CollisionGroup> = Array()
        val categoryLookup: ObjectMap<String, Short> = ObjectMap()
        for (i in 0..groups.size - 1) {
            val cat:Short = Math.pow(2.toDouble(), i.toDouble()).toShort()
            categoryLookup.put(groups.get(i).name, cat)
        }
        for (i in 0..groups.size - 1) {
            val group = groups.get(i)
            val category: Short = categoryLookup[group.name]
            var mask: Short = 0
            for (colliderName in group.colliderGroupNames) {
                mask = mask or categoryLookup[colliderName]
            }
            builtGroups.add(CollisionGroup(group.name, CollisionData(category, mask)))
        }
        return builtGroups
    }

}
