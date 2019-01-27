package com.binarymonks.jj.core.physics.collisions

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.physics.CollisionHandler
import com.binarymonks.jj.core.properties.PropOverride
import com.binarymonks.jj.core.scenes.Scene


class EmitEventCollision : CollisionHandler() {

    var messege: PropOverride<String> = PropOverride("")

    override fun collision(me: Scene, myFixture: Fixture, other: Scene, otherFixture: Fixture, contact: Contact): Boolean {
        JJ.events.send(messege.get())
        return false
    }
}