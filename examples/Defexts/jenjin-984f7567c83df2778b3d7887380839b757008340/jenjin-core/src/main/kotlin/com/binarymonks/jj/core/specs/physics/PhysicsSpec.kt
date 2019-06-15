package com.binarymonks.jj.core.specs.physics

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.Array
import com.binarymonks.jj.core.physics.CollisionHandler
import com.binarymonks.jj.core.physics.CollisionHandlers


class PhysicsSpec {
    var bodyType = BodyDef.BodyType.StaticBody
    var linearDamping = 0f
    var angularDamping = 0f
    var gravityScale = 1f
    var bullet = false
    var fixedRotation: Boolean = false
    var allowSleep = true
    val collisions = CollisionHandlers()
    var lights: Array<LightSpec> = Array()

    internal var fixtures: Array<FixtureSpec> = Array()

    fun addFixture(fixtureSpec: FixtureSpec) {
        fixtures.add(fixtureSpec)
    }

    fun fixture(init: FixtureSpec.() -> Unit) {
        var fixtureSpec = FixtureSpec()
        fixtureSpec.init()
        this.addFixture(fixtureSpec)
    }

    fun pointLight(init: PointLightSpec.() -> Unit) {
        val pl = PointLightSpec()
        pl.init()
        this.lights.add(pl)
    }
}