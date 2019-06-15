package com.binarymonks.jj.core.layers


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.api.LayersAPI
import com.binarymonks.jj.core.pools.Poolable
import com.binarymonks.jj.core.pools.new
import com.binarymonks.jj.core.pools.recycle


/**
 * A stack of [Layers] to render in order.
 *
 * T
 */
class LayerStack(var clearColor: Color = Color(0f, 0f, 0f, 1f)) : LayersAPI {


    private val inputMultiplexer = InputMultiplexer()
    private val layers = Array<Layer>()
    private val registeredLayers: ObjectMap<String, Layer> = ObjectMap()
    private val inComingChanges: Array<StackChange> = Array()

    init {
        Gdx.input.inputProcessor = inputMultiplexer
    }

    fun update() {
        for (change in inComingChanges) {
            when (change) {
                is Pop -> actuallyPop()
                is Push -> actuallyPush(change.layer)
            }
            recycle(change)
        }
        inComingChanges.clear()
        Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        for (layer in layers) {
            layer.update()
        }
    }

    private fun actuallyPush(layer: Layer?) {
        @Suppress("NAME_SHADOWING")
        val layer = checkNotNull(layer)
        layer.resize(Gdx.graphics.width, Gdx.graphics.height)
        layers.insert(layers.size, layer)
        layer.stack = this
        inputMultiplexer.addProcessor(0, layer.inputMultiplexer)
    }

    private fun actuallyPop() {
        layers.removeIndex(layers.size - 1)
        inputMultiplexer.removeProcessor(0)
    }

    override fun push(layer: Layer) {
        val push = new(Push::class)
        push.layer = layer
        inComingChanges.add(push)
    }

    override fun push(layerName: String) {
        val layer = checkNotNull(registeredLayers.get(layerName))
        push(layer)
    }

    override fun pop() {
        inComingChanges.add(new(Pop::class))
    }

    override fun registerLayer(layerName: String, layer: Layer) {
        registeredLayers.put(layerName, layer)
    }

    override fun <T : Layer> getLayer(layerName: String): T {
        @Suppress("UNCHECKED_CAST")
        return registeredLayers.get(layerName) as T
    }


    fun resize(width: Int, height: Int) {
        layers.forEach { it.resize(width, height) }
    }
}

interface StackChange

class Push : StackChange, Poolable {

    var layer: Layer? = null

    override fun reset() {
        layer = null
    }

}

class Pop : StackChange, Poolable {
    override fun reset() {
    }
}
