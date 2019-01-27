package com.binarymonks.jj.core.layers

import com.badlogic.gdx.InputProcessor


class InputLayer(var inputProcessor: InputProcessor) : LayerAbs() {

    init {
        inputMultiplexer.addProcessor(inputProcessor)
    }

    override fun update() {
        //Do nothing
    }

    override fun resize(width: Int, height: Int) {
        //Do nothing
    }
}