package com.binarymonks.jj.core.physics.collisions

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.binarymonks.jj.core.physics.CollisionHandler
import com.binarymonks.jj.core.scenes.Scene


class DestroyCollision : CollisionHandler() {

    var destroyOther = true
    var destroyMe = true

    override fun collision(me: Scene, myFixture: Fixture, other: Scene, otherFixture: Fixture, contact: Contact): Boolean {
        if (destroyOther) other.destroy()
        if (destroyMe) me.destroy()
        return false
    }
}