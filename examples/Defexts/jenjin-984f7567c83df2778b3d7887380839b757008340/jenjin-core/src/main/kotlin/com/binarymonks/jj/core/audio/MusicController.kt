package com.binarymonks.jj.core.audio

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music

class MusicController : Music.OnCompletionListener {
    var volume = 0.2f
        set(volume) {
            field = volume
            if (music != null) {
                music!!.volume = calculateVolume()
            }
        }
    var isMute = false
        set(mute) {
            field = mute
            if (music != null) {
                music!!.volume = calculateVolume()
            }
        }
    internal var musicPath: String? = null
    internal var music: Music? = null
    var isMusicFinished = true
        internal set

    fun setMusic(musicPath: String) {
        disposeCurrentMusic()
        this.musicPath = musicPath
        music = Gdx.audio.newMusic(Gdx.files.internal(musicPath))
        music!!.volume = calculateVolume()
        music!!.setOnCompletionListener(this)
    }

    private fun calculateVolume(): Float {
        return if (isMute) 0.0f else this.volume
    }

    fun play() {
        isMusicFinished = false
        music!!.play()
    }

    fun playLoop() {
        music!!.isLooping = true
        play()
    }

    val isMusicPlaying: Boolean
        get() = music!!.isPlaying

    fun stop() {
        music!!.stop()
    }

    fun disposeCurrentMusic() {
        if (music != null) {
            music!!.stop()
            music!!.dispose()
        }
    }

    override fun onCompletion(music: Music) {
        isMusicFinished = true
    }
}
