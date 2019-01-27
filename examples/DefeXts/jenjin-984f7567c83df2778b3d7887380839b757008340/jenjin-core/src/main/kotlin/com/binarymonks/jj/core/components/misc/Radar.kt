package com.binarymonks.jj.core.components.misc

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.utils.ObjectSet
import com.binarymonks.jj.core.components.Component
import com.binarymonks.jj.core.physics.CollisionHandler
import com.binarymonks.jj.core.scenes.Scene


class EnterRadar(val radar: Radar) : CollisionHandler() {

    override fun collision(me: Scene, myFixture: Fixture, other: Scene, otherFixture: Fixture, contact: Contact): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class ExitRadar(val radar: Radar) : CollisionHandler() {
    override fun collision(me: Scene, myFixture: Fixture, other: Scene, otherFixture: Fixture, contact: Contact): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}


class Radar : Component() {

    internal val trackedScenes = ObjectSet<Scene>()

    override fun onAddToWorld() {
        me().physicsRoot.collisionResolver.addInitialBegin(EnterRadar(this))
        me().physicsRoot.collisionResolver.addEnd(ExitRadar(this))
    }
}