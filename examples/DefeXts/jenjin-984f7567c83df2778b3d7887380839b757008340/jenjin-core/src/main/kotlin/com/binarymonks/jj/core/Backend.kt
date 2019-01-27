package com.binarymonks.jj.core

import com.binarymonks.jj.core.assets.Assets
import com.binarymonks.jj.core.async.Tasks
import com.binarymonks.jj.core.audio.Audio
import com.binarymonks.jj.core.input.mapping.InputMapper
import com.binarymonks.jj.core.layers.GameRenderingLayer
import com.binarymonks.jj.core.layers.LayerStack
import com.binarymonks.jj.core.physics.PhysicsWorld
import com.binarymonks.jj.core.pools.Pools
import com.binarymonks.jj.core.render.RenderWorld
import com.binarymonks.jj.core.scenes.Scenes
import com.binarymonks.jj.core.scenes.SceneWorld
import com.binarymonks.jj.core.time.ClockControls

/**
 * The Backend of the [JJ] api. Gives access to full interfaces.
 */
class Backend {
    lateinit var config: JJConfig
    lateinit var clock: ClockControls
    lateinit var scenes: Scenes
    lateinit var layers: LayerStack
    lateinit var renderWorld: RenderWorld
    lateinit var physicsWorld: PhysicsWorld
    lateinit var sceneWorld: SceneWorld
    lateinit var pools: Pools
    lateinit var assets: Assets
    lateinit var audio: Audio
    lateinit var tasks: Tasks
    lateinit var input: InputMapper
    lateinit var defaultGameRenderingLayer: GameRenderingLayer
    lateinit var game: JJGame

    private var idCounter = 0

    fun nextID(): Int {
        return idCounter++
    }

}