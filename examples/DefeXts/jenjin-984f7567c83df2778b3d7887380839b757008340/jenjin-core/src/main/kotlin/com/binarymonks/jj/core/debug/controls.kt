package com.binarymonks.jj.core.debug

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.input.InputProcessorAbstract


class Controls : InputProcessorAbstract() {


    override fun keyDown(keycode: Int): Boolean {
        return when (keycode) {
            Input.Keys.F8 -> {
                JJ.B.game.stepReleased = true
                true
            }
            Input.Keys.F9 -> {
                JJ.B.game.step = !JJ.B.game.step
                true
            }
            else -> false
        }
    }
}