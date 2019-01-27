package com.binarymonks.jj.core.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.binarymonks.jj.core.layers.Layer

class UIBuilder private constructor() {

    lateinit var layer: UILayer

    constructor(viewPort: Viewport = ExtendViewport(1000f, 1000f), builder: UIBuilder.() -> Unit) : this() {
        layer = UILayer(viewPort)
        builder()
    }

    fun <T : Actor> actor(name: String, actor: T, build: (T.() -> Unit)? = null): ActorListenerAppender {
        if (build != null) {
            actor.build()
        }
        layer.addActor(name, actor)
        return ActorListenerAppender(layer, actor)
    }

    fun <T : Actor> actor(actor: T, build: (T.() -> Unit)? = null): ActorListenerAppender {
        if (build != null) {
            actor.build()
        }
        layer.addActor(actor)
        return ActorListenerAppender(layer, actor)
    }

    fun build(): Layer {
        return layer
    }

}

class ActorListenerAppender(private val uiLayer: UILayer, private val actor: Actor) {

    fun <T : EventListener> withListener(listener: T): ActorListenerAppender {
        if (listener is LayerAwareListener) {
            listener.myActor = actor
            listener.uiLayer = uiLayer
        }
        actor.addListener(listener)
        return this
    }

}