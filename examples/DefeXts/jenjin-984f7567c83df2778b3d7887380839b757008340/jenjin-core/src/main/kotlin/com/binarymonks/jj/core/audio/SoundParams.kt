package com.binarymonks.jj.core.audio

import java.util.ArrayList

class SoundParams(var id: String) {

    var soundPaths: MutableList<String> = ArrayList()
    var volume = 1.0f
    var isBig = false

    fun variant(path: String): SoundParams {
        soundPaths.add(path)
        return this
    }

    fun setVolume(volume: Float): SoundParams {
        this.volume = volume
        return this
    }

    fun setBig(big: Boolean): SoundParams {
        isBig = big
        return this
    }

    fun copy(): SoundParams {
        val copy = SoundParams(id)
        copy.soundPaths = soundPaths
        copy.volume = volume
        copy.isBig = isBig
        return copy
    }
}
