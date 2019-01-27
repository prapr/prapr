package com.binarymonks.jj.core.physics.collisions

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.binarymonks.jj.core.audio.SoundMode
import com.binarymonks.jj.core.physics.CollisionHandler
import com.binarymonks.jj.core.scenes.Scene

/**
 * Triggers a sounds on collision
 */
class SoundCollision(
        var soundName: String? = null,
        var mode: SoundMode = SoundMode.NORMAL
) : CollisionHandler() {

    override fun collision(me: Scene, myFixture: Fixture, other: Scene, otherFixture: Fixture, contact: Contact): Boolean {
        me.soundEffects.triggerSound(checkNotNull(soundName), mode)
        return false
    }
}