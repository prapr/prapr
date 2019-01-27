package com.binarymonks.jj.core.components.misc

import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.components.Component
import com.binarymonks.jj.core.copy
import com.binarymonks.jj.core.pools.newObjectMap
import com.binarymonks.jj.core.pools.recycle
import com.binarymonks.jj.core.properties.PropOverride
import com.binarymonks.jj.core.specs.InstanceParams
import com.binarymonks.jj.core.specs.SceneSpecRef
import com.binarymonks.jj.core.specs.SceneSpecRefPath


class Emitter(
        var sceneSpecRef: SceneSpecRef? = null,
        var offsetX: Float = 0f,
        var offsetY: Float = 0f,
        var scaleX: Float = 1f,
        var scaleY: Float = 1f,
        var rotationD: Float = 0f,
        var delayMinSeconds: Float = 1f,
        var delayMaxSeconds: Float = 1f,
        var repeat: Int = 0
) : Component() {

    /**
     * The properties to emit the instance with.
     */
    val emitProps = PropOverride<ObjectMap<String, Any>>(newObjectMap())

    fun setSpec(path: String) {
        sceneSpecRef = SceneSpecRefPath(path)
    }

    private var scheduledID = -1

    override fun onAddToWorld() {
        scheduledID = JJ.clock.schedule(this::emit, delayMinSeconds, delayMaxSeconds, repeat, "Emitter")
    }

    override fun onRemoveFromWorld() {
        JJ.clock.cancel(scheduledID)
    }

    fun emit() {
        val params = InstanceParams.new()
        val myPosition = me().physicsRoot.position()
        params.x = myPosition.x + offsetX
        params.y = myPosition.y + offsetY
        params.scaleX = scaleX
        params.scaleY = scaleY
        params.rotationD = rotationD
        params.properties =emitProps.get()

        JJ.scenes.instantiate(params, checkNotNull(sceneSpecRef).resolve())

        recycle(params)
    }

    override fun update() {

    }
}