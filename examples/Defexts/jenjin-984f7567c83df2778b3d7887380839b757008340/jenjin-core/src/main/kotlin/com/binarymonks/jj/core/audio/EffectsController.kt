package com.binarymonks.jj.core.audio

import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.JJ

class EffectsController {

    private var singletonMap = ObjectMap<String, SingletonWindow>()

    var volume = 0.2f
    var isMute = false

    fun addSingletonSound(singletonTimeWindow: Float, vararg soundpaths: String) {
        val window = SingletonWindow(singletonTimeWindow)
        for (soundpath in soundpaths) {
            singletonMap.put(soundpath, window)
        }
    }

    fun canTriggerSingleton(soundpath: String): Boolean {
        if (!singletonMap.containsKey(soundpath)) {
            return true
        }
        return singletonMap.get(soundpath).elapsed()
    }


    private inner class SingletonWindow(window: Float) {
        var window: Double = 0.toDouble()
        var lastTrigger = 0.0

        init {
            this.window = window.toDouble()
        }

        fun elapsed(): Boolean {
            val currentT = JJ.B.clock.time
            val elapsed = currentT - lastTrigger > window
            if (elapsed) {
                lastTrigger = currentT
            }
            return elapsed
        }


    }


}
