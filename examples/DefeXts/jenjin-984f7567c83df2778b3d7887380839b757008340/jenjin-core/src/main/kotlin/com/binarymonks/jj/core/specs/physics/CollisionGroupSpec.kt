package com.binarymonks.jj.core.specs.physics

import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.physics.CollisionData

interface CollisionGroupSpec {
    fun toCollisionData(properties: ObjectMap<String, Any>): CollisionData
}

class CollisionGroupSpecExplicit(val category: Short = Short.MAX_VALUE, val mask: Short = Short.MAX_VALUE) : CollisionGroupSpec {
    override fun toCollisionData(properties: ObjectMap<String, Any>): CollisionData = CollisionData(category, mask)
}

class CollisionGroupSpecName(val groupName: String) : CollisionGroupSpec {
    override fun toCollisionData(properties: ObjectMap<String, Any>): CollisionData {
        return JJ.B.physicsWorld.collisionGroups.getCollisionData(groupName)
    }
}

class CollisionGroupSpecProperty(val propertyName: String) : CollisionGroupSpec {
    override fun toCollisionData(properties: ObjectMap<String, Any>): CollisionData {
        if(properties.containsKey(propertyName)){
            var groupName: String  =  properties.get(propertyName) as String
            return JJ.B.physicsWorld.collisionGroups.getCollisionData(groupName)
        }
        throw Exception("No property set by name of $propertyName")
    }

}
