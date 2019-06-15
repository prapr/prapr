package com.binarymonks.jj.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.binarymonks.jj.core.assets.Assets
import com.binarymonks.jj.core.audio.Audio
import com.binarymonks.jj.core.audio.SoundEffects
import com.binarymonks.jj.core.layers.LayerStack
import com.binarymonks.jj.core.physics.PhysicsRoot
import com.binarymonks.jj.core.physics.PhysicsWorld
import com.binarymonks.jj.core.pools.Pools
import com.binarymonks.jj.core.render.RenderRoot
import com.binarymonks.jj.core.render.RenderWorld
import com.binarymonks.jj.core.scenes.Scene
import com.binarymonks.jj.core.scenes.SceneWorld
import com.binarymonks.jj.core.scenes.Scenes
import com.binarymonks.jj.core.time.ClockControls
import org.mockito.Mockito


fun mockoutGDXinJJ() {
    Gdx.input = Mockito.mock(Input::class.java)
    val config = JJConfig()
    JJ.B = Backend()
    JJ.B.config = config
    JJ.B.clock = ClockControls()
    JJ.B.scenes = Scenes()
    JJ.B.layers = LayerStack()
    JJ.B.physicsWorld = Mockito.mock(PhysicsWorld::class.java)
    JJ.B.renderWorld = Mockito.mock(RenderWorld::class.java)
    JJ.B.sceneWorld = SceneWorld()
    JJ.B.pools = Pools()
    JJ.B.assets = Assets()
    JJ.B.audio = Audio()

    JJ.scenes = JJ.B.scenes
    JJ.layers = JJ.B.layers
    JJ.pools = JJ.B.pools
    JJ.clock = JJ.B.clock
    JJ.physics = JJ.B.physicsWorld
    JJ.assets = JJ.B.assets
    JJ.render = JJ.B.renderWorld
}

fun testScene(pooled: Boolean = true, name: String? = null): Scene {
    val mockBody: Body = Mockito.mock(Body::class.java)
    Mockito.`when`(mockBody.getFixtureList()).thenReturn(Array())
    Mockito.`when`(mockBody.getJointList()).thenReturn(Array())

    return Scene(
            name,
            null,
            null,
            1,
            Vector2(),
            PhysicsRoot(mockBody),
            RenderRoot(1),
            SoundEffects(),
            ObjectMap(),
            pooled
    )
}
