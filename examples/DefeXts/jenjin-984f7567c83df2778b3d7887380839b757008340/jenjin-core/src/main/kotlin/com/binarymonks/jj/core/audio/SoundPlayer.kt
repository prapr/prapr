package com.binarymonks.jj.core.audio

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.MathUtils
import com.binarymonks.jj.core.JJ

fun getSoundPlayer(params: SoundParams): SoundPlayer {
    if (!params.isBig) {
        return ShortSound(params)
    }
    return LongSound(params)
}

abstract class SoundPlayer constructor(internal var parameters: SoundParams) : Sound {
    internal var currentSoundPath: String? = null

    fun selectRandom() {
        currentSoundPath = selectRandom(parameters.soundPaths)
    }

    private fun selectRandom(soundPaths: List<String>): String {
        val roll = MathUtils.random(0, soundPaths.size - 1)
        return soundPaths[roll]
    }

    abstract fun triggering()


    fun canTriggerSingleton(): Boolean {
        return JJ.B.audio.effects.canTriggerSingleton(myCurrentSoundPath())
    }

    fun myCurrentSoundPath(): String {
        if (currentSoundPath != null) {
            return currentSoundPath!!
        }
        throw Exception("No soundpath to fetch")
    }

}

private class ShortSound constructor(parameters: SoundParams) : SoundPlayer(parameters) {

    internal var sound: Sound? = null

    override fun triggering() {
        sound = JJ.B.assets.getAsset(myCurrentSoundPath(), Sound::class)
    }

    override fun play(): Long {
        return checkNotNull(sound).play()
    }

    override fun play(volume: Float): Long {
        return checkNotNull(sound).play(volume)
    }

    override fun play(volume: Float, pitch: Float, pan: Float): Long {
        return checkNotNull(sound).play(volume, pitch, pan)
    }

    override fun loop(): Long {
        return checkNotNull(sound).loop()
    }

    override fun loop(volume: Float): Long {
        return checkNotNull(sound).loop(volume)
    }

    override fun loop(volume: Float, pitch: Float, pan: Float): Long {
        return loop(volume, pitch, pan)
    }

    override fun stop() {
        checkNotNull(sound).stop()
    }

    override fun pause() {
        checkNotNull(sound).pause()
    }

    override fun resume() {
        checkNotNull(sound).resume()
    }

    override fun dispose() {
        checkNotNull(sound).dispose()
    }

    override fun stop(soundId: Long) {
        checkNotNull(sound).stop(soundId)
    }

    override fun pause(soundId: Long) {
        checkNotNull(sound).pause(soundId)
    }

    override fun resume(soundId: Long) {
        checkNotNull(sound).resume(soundId)
    }

    override fun setLooping(soundId: Long, looping: Boolean) {
        checkNotNull(sound).setLooping(soundId, looping)
    }

    override fun setPitch(soundId: Long, pitch: Float) {
        checkNotNull(sound).setPitch(soundId, pitch)
    }

    override fun setVolume(soundId: Long, volume: Float) {
        checkNotNull(sound).setVolume(soundId, volume)
    }

    override fun setPan(soundId: Long, pan: Float, volume: Float) {
        checkNotNull(sound).setPan(soundId, pan, volume)
    }


}

private class LongSound(parameters: SoundParams) : SoundPlayer(parameters) {

    internal var sound: Music? = null

    override fun triggering() {
        sound = Gdx.audio.newMusic(Gdx.files.internal(currentSoundPath))
    }


    override fun play(): Long {
        checkNotNull(sound).play()
        return 1
    }

    override fun play(volume: Float): Long {
        checkNotNull(sound).volume = volume
        checkNotNull(sound).play()
        return 1
    }

    override fun play(volume: Float, pitch: Float, pan: Float): Long {
        checkNotNull(sound).setPan(pan, volume)
        checkNotNull(sound).play()
        return 1
    }

    override fun loop(): Long {
        checkNotNull(sound).isLooping = true
        checkNotNull(sound).play()
        return 1
    }

    override fun loop(volume: Float): Long {
        checkNotNull(sound).isLooping = true
        checkNotNull(sound).volume = volume
        checkNotNull(sound).play()
        return 1
    }

    override fun loop(volume: Float, pitch: Float, pan: Float): Long {
        checkNotNull(sound).isLooping = true
        checkNotNull(sound).setPan(pan, volume)
        checkNotNull(sound).play()
        return 1
    }

    override fun stop() {
        checkNotNull(sound).stop()
    }

    override fun pause() {
        checkNotNull(sound).pause()
    }

    override fun resume() {
        checkNotNull(sound).play()
    }

    override fun dispose() {
        checkNotNull(sound).dispose()
    }

    override fun stop(soundId: Long) {
        checkNotNull(sound).stop()
    }

    override fun pause(soundId: Long) {
        checkNotNull(sound).pause()
    }

    override fun resume(soundId: Long) {
        checkNotNull(sound).play()
    }

    override fun setLooping(soundId: Long, looping: Boolean) {
        checkNotNull(sound).isLooping = looping
    }

    override fun setPitch(soundId: Long, pitch: Float) {}

    override fun setVolume(soundId: Long, volume: Float) {
        checkNotNull(sound).volume = volume
    }

    override fun setPan(soundId: Long, pan: Float, volume: Float) {
        checkNotNull(sound).setPan(pan, volume)
    }

}
