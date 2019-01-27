package com.binarymonks.jj.core.layers


import com.badlogic.gdx.InputMultiplexer

interface Layer {

    var inputMultiplexer: InputMultiplexer
    var stack: LayerStack?

    fun update()

    fun resize(width: Int, height: Int)
}

abstract class LayerAbs(
        override var inputMultiplexer: InputMultiplexer = InputMultiplexer(),
        override var stack: LayerStack? = null
) : Layer

