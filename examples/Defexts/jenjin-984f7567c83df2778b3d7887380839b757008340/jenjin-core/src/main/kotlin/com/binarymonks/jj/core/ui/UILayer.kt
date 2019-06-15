package com.binarymonks.jj.core.ui

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.viewport.Viewport
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.layers.Layer
import com.binarymonks.jj.core.layers.LayerStack


class UILayer(
        override var inputMultiplexer: InputMultiplexer = InputMultiplexer(),
        override var stack: LayerStack? = null
) : Stage(), Layer {

    init {
        this.inputMultiplexer.addProcessor(this)
    }


    constructor(
            viewport: Viewport
    ) : this(inputMultiplexer = InputMultiplexer(), stack = null) {
        this.viewport = viewport
    }

    val namedActors: ObjectMap<String, Actor> = ObjectMap()

    fun addActor(name: String, actor: Actor) {
        super.addActor(actor)
        namedActors.put(name, actor)
    }

    fun getActor(name: String): Actor {
        return namedActors.get(name)
    }

    override fun update() {
        act(JJ.clock.deltaFloat)
        draw()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }
}