package com.binarymonks.jj.core

import com.badlogic.gdx.ApplicationListener
import com.binarymonks.jj.core.specs.SceneSpec

/**
 * A JJGame
 *
 * You must extend this and only ever create one of them. It is the entry point to Jenjin and it initializes
 * a bunch of global state - which must only be done once.
 *
 * @property jjConfig Configuration for your game.
 */
abstract class JJGame(val jjConfig: JJConfig = JJConfig()) : ApplicationListener {

    var step: Boolean = false
    var stepReleased = false

    override fun create() {
        JJ.initialise(this.jjConfig, this)
        gameOn()
    }

    override fun pause() {
    }

    override fun resize(width: Int, height: Int) {
        JJ.B.layers.resize(width, height)
    }

    override fun render() {
        if (checkStep()) {
            JJ.B.clock.update()
            JJ.B.assets.update()
            JJ.B.tasks.preloopTasks.update()
            JJ.B.sceneWorld.update()
            JJ.B.tasks.prePhysicsTasks.update()
            JJ.B.physicsWorld.update()
            JJ.B.tasks.postPhysicsTasks.update()
            JJ.B.layers.update()
        }
    }

    private fun checkStep(): Boolean {
        if (step) {
            if (stepReleased) {
                stepReleased = false
                return true
            }
            return false
        } else {
            return true
        }
    }

    override fun resume() {
    }

    override fun dispose() {
    }

    /**
     * This is your entry hook.
     *
     * You can instantiate assets and build your [SceneSpec]s here or whatever.
     * In the end though, you need to probably do 1 of 3 things:
     *
     *  - Load a [SceneSpec] or
     *  - Present a Splash Screen or
     *  - Present a Menu
     *
     */
    protected abstract fun gameOn()

}


