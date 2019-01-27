package com.binarymonks.jj.core

import com.badlogic.gdx.physics.box2d.BodyDef
import com.binarymonks.jj.core.api.*
import com.binarymonks.jj.core.assets.Assets
import com.binarymonks.jj.core.async.Tasks
import com.binarymonks.jj.core.audio.Audio
import com.binarymonks.jj.core.debug.Controls
import com.binarymonks.jj.core.events.EventBus
import com.binarymonks.jj.core.input.mapping.InputMapper
import com.binarymonks.jj.core.layers.GameRenderingLayer
import com.binarymonks.jj.core.layers.InputLayer
import com.binarymonks.jj.core.layers.LayerStack
import com.binarymonks.jj.core.physics.PhysicsWorld
import com.binarymonks.jj.core.pools.Pools
import com.binarymonks.jj.core.render.RenderWorld
import com.binarymonks.jj.core.scenes.SceneWorld
import com.binarymonks.jj.core.scenes.Scenes
import com.binarymonks.jj.core.specs.SceneSpec
import com.binarymonks.jj.core.specs.params
import com.binarymonks.jj.core.time.ClockControls

/**
 * The front end global api.
 *
 * Provides access to the commonly used interfaces and operations for interacting
 * with the engine. For complete interfaces have a look at [JJ.B]
 *
 */
object JJ {
    private var initialised = false
    lateinit var scenes: ScenesAPI
    lateinit var assets: AssetsAPI
    lateinit var layers: LayersAPI
    lateinit var pools: PoolsAPI
    lateinit var clock: ClockAPI
    lateinit var physics: PhysicsAPI
    lateinit var render: RenderAPI
    lateinit var tasks: TasksAPI
    lateinit var input: InputAPI
    lateinit var events: EventsAPI

    lateinit var B: Backend

    internal fun initialise(config: JJConfig, game: JJGame) {
        if (initialised) {
            throw Exception("Already initialised")
        }
        initialised = true
        B = Backend()
        B.game = game
        B.config = config
        B.clock = ClockControls()
        B.scenes = Scenes()
        B.layers = LayerStack(config.gameView.clearColor)
        B.physicsWorld = PhysicsWorld()
        B.renderWorld = RenderWorld()
        B.sceneWorld = SceneWorld()
        B.pools = Pools()
        B.assets = Assets()
        B.audio = Audio()
        B.defaultGameRenderingLayer = GameRenderingLayer(config.gameView)
        B.input = InputMapper()
        B.defaultGameRenderingLayer.inputMultiplexer.addProcessor(B.input)
        B.layers.push(B.defaultGameRenderingLayer)
        B.tasks = Tasks()
        if(config.debugStep){
            B.clock.setTimeFunction(ClockControls.TimeFunction.FIXED_TIME)
            B.layers.push(InputLayer(Controls()))
        }




        scenes = B.scenes
        layers = B.layers
        pools = B.pools
        clock = B.clock
        physics = B.physicsWorld
        assets = B.assets
        render = B.renderWorld
        tasks = B.tasks
        input = B.input
        events = EventBus()

        val rootScene = JJ.B.scenes.masterFactory.createScene(
                SceneSpec { physics { bodyType = BodyDef.BodyType.StaticBody } },
                params { },
                null
        )
        B.sceneWorld.rootScene = rootScene
        rootScene.onAddToWorld()
    }
}