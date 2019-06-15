package com.binarymonks.jj.core.specs

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.copy
import com.binarymonks.jj.core.pools.PoolManager
import com.binarymonks.jj.core.pools.mat3
import com.binarymonks.jj.core.pools.new
import com.binarymonks.jj.core.properties.HasProps
import com.binarymonks.jj.core.scenes.Scene

/**
 * A [InstanceParams] builder.
 */
fun params(init: InstanceParams.() -> Unit): InstanceParams {
    val instanceParams = InstanceParams.new()
    instanceParams.init()
    return instanceParams
}

class InstanceParams : HasProps {

    companion object Factory {
        fun new(): InstanceParams {
            return new(InstanceParams::class)
        }

        fun from(rootScene: Scene): InstanceParams {
            val params = new(InstanceParams::class)
            val position = rootScene.physicsRoot.position()
            params.x = position.x
            params.y = position.y
            params.rotationD = rootScene.physicsRoot.rotationR() * MathUtils.radDeg
            return params
        }
    }

    var x: Float = 0f
    var y: Float = 0f
    var scaleX: Float = 1f
    var scaleY: Float = 1f
    var rotationD: Float = 0f
    var properties: ObjectMap<String, Any> = ObjectMap()
    /**
     * This must be a name that is unique to its me SceneSpec child nodes if set
     */
    var name: String? = null
    /**
     * This must be a name that is globally unique if set. It is used for retrieving
     */
    var uniqueInstanceName: String? = null
    private var transformMatrix: Matrix3 = mat3()

    fun setPosition(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun setPosition(vector: Vector2) {
        this.x = vector.x
        this.y = vector.y
    }

    fun prop(name: String, value: Any? = null) {
        properties.put(name, value)
    }

    fun getTransformMatrix(): Matrix3 {
        transformMatrix.idt()
        transformMatrix.translate(x, y)
        transformMatrix.scale(scaleX, scaleY)
        transformMatrix.rotate(rotationD)
        return transformMatrix
    }

    override fun hasProp(key: String): Boolean {
        return properties.containsKey(key)
    }

    override fun getProp(key: String): Any? {
        return properties[key]
    }

    fun clone(): InstanceParams {
        return copy(this)
    }

    override fun toString(): String {
        return "InstanceParams(x=$x, y=$y, scaleX=$scaleX, scaleY=$scaleY, rotationD=$rotationD, properties=$properties, name=$name, uniqueInstanceName=$uniqueInstanceName)"
    }


}

class ParamsPoolManager : PoolManager<InstanceParams> {

    override fun reset(pooled: InstanceParams) {
        pooled.x = 0f
        pooled.y = 0f
        pooled.scaleX = 1f
        pooled.scaleY = 1f
        pooled.rotationD = 0f
        pooled.properties.clear()

    }

    override fun create_new(): InstanceParams {
        return InstanceParams()
    }

    override fun dispose(pooled: InstanceParams) {

    }
}