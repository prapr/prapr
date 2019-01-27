package com.binarymonks.jj.core

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2

class JJConfig() {
    constructor(build: JJConfig.() -> Unit) : this() {
        this.build()
    }

    var gameView = GameViewConfig()
    var b2d = B2DConfig()
    var spine = SpineConfig()
    var debugStep = false
}

class GameViewConfig {
    var clearColor = Color.BLACK
    var worldBoxWidth = 50f
    var cameraPosX: Float = 0f
    var cameraPosY: Float = 0f
    var postProcessingEnabled = false
}

class B2DConfig {
    var debugRender = false
    var gravity = Vector2(0f, -9f)
}

class SpineConfig {
    var render = true
}