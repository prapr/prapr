package com.binarymonks.jj.core.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.DragListener


interface LayerAwareListener : EventListener {
    var uiLayer: UILayer?
    var myActor: Actor?
}

open class JJClickListener(
        override var uiLayer: UILayer? = null,
        override var myActor: Actor? = null
) : ClickListener(), LayerAwareListener

open class JJActorGestureListener(
        override var uiLayer: UILayer? = null,
        override var myActor: Actor? = null
) : ActorGestureListener(), LayerAwareListener

abstract class JJChangeListener(
        override var uiLayer: UILayer? = null,
        override var myActor: Actor? = null
) : LayerAwareListener, ChangeListener()

class JJDragListener(
        override var uiLayer: UILayer? = null,
        override var myActor: Actor? = null
) : DragListener(), LayerAwareListener