package com.binarymonks.jj.core.specs

import com.binarymonks.jj.core.audio.SoundParams
import com.badlogic.gdx.utils.Array


class SoundSpec {

    internal var params: Array<SoundParams> = Array()

    fun sound(name: String, vararg paths: String, volume: Float = 1f) {
        val soundParams = SoundParams(name)
        soundParams.volume=volume
        paths.forEach { soundParams.soundPaths.add(it) }
        params.add(soundParams)
    }

    /**
     * For sounds that are longer than 7 seconds (I think)
     */
    fun bigSound(name: String, vararg paths: String, volume: Float = 1f) {
        val soundParams = SoundParams(name)
        soundParams.volume=volume
        soundParams.isBig = true
        paths.forEach { soundParams.soundPaths.add(it) }
        params.add(soundParams)
    }
}