package com.binarymonks.jj.core.audio

import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.Array
import com.binarymonks.jj.core.JJ

class SoundEffects : Disposable {
    internal var sounds: ObjectMap<String, SoundPlayer> = ObjectMap()
    internal var parameters: ObjectMap<String, SoundParams> = ObjectMap()
    internal var currentSoundId: String? = null
    internal var currentSound: SoundPlayer? = null
    internal var currentSoundPlayID: Long = 0
    internal var currentSoundStart: Double = 0.toDouble()
    var isStopOnTrigger = false
    private var currentSoundfxID: String? = null

    override fun dispose() {
        if (currentSound != null) {
            currentSound!!.stop(currentSoundPlayID)
        }
    }

    private fun stopCurrentSound() {
        if (currentSound != null) {
            if (isStopOnTrigger) {
                currentSound!!.stop(currentSoundPlayID)
            }
        }
    }

    fun stopSound() {
        if (currentSound != null) {
            currentSound!!.stop()
        }
    }

    fun addSoundEffect(soundfx: SoundParams) {
        sounds.put(soundfx.id, getSoundPlayer(soundfx))
        parameters.put(soundfx.id, soundfx)
    }

    fun addSoundEffects(soundfx: Array<SoundParams>) {
        for (fxParams in soundfx) {
            addSoundEffect(fxParams)
        }
    }

    fun triggerSound(soundfxID: String, mode: SoundMode, volume: Float =1f, pitch: Float = 1f) {
        val elapsed = JJ.B.clock.time - currentSoundStart
        if (currentSound == null ||
                currentSoundfxID != soundfxID || elapsed > REPEAT_ELAPSED_TIME) {
            currentSoundId = soundfxID
            trigger(soundfxID, mode, volume, pitch)
        }
    }

    private fun trigger(soundfxID: String, mode: SoundMode, volume: Float, pitch: Float) {
        if (sounds.containsKey(soundfxID)) {
            val proposedSound = sounds[soundfxID] ?: throw Exception("Proposed sounds is null")
            proposedSound.selectRandom()
            if (proposedSound.canTriggerSingleton()) {
                stopCurrentSound()
                currentSoundfxID = soundfxID
                currentSound = proposedSound
                currentSound!!.triggering()
                currentSoundStart = JJ.clock.time
                val actualVolume = if (JJ.B.audio.effects.isMute)
                    0.0f
                else
                    currentSound!!.parameters.volume * JJ.B.audio.effects.volume * volume
                currentSoundPlayID = currentSound!!.play(actualVolume)
                if (mode == SoundMode.LOOP) {
                    currentSound!!.setLooping(currentSoundPlayID, true)
                }
                currentSound!!.setPitch(currentSoundPlayID,
                        JJ.B.clock.getRealToGameRatio()*pitch)
            }
        }
    }

    companion object {

        private val REPEAT_ELAPSED_TIME = 0.1
    }

}
