package com.binarymonks.jj.demo.demos

import com.badlogic.gdx.physics.box2d.BodyDef
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.JJConfig
import com.binarymonks.jj.core.JJGame
import com.binarymonks.jj.core.physics.collisions.SoundCollision
import com.binarymonks.jj.core.specs.Circle
import com.binarymonks.jj.core.specs.SceneSpec
import com.binarymonks.jj.core.specs.params


class D_18_physics_debug : JJGame(JJConfig {
    b2d.debugRender = true
    debugStep = true
    val width = 30f
    gameView.worldBoxWidth = width
    gameView.cameraPosX = 0f
    gameView.cameraPosY = 0f
}) {

    public override fun gameOn() {

        JJ.scenes.addSceneSpec("ball", ball())
        JJ.scenes.addSceneSpec("terrain", floor())

        JJ.scenes.loadAssetsNow()

        //Set up our collision groups
        JJ.physics.collisionGroups.buildGroups {
            group("layer1").collidesWith("layer1")
            group("layer2").collidesWith("layer2")
        }

        val initialSceneSpec = SceneSpec {
            //Set up some balls and a terrain on 'layer1' collision group
            nodeRef(params { x = -8f; y = 8f; prop("collisionGroup", "layer1") }, "ball")
            nodeRef(params { x = -2f; y = 9f; scaleX = 2f; scaleY = 2f;prop("collisionGroup", "layer1") }, "ball")
            nodeRef(params { x = 0f; y = 0f; scaleX = 20f;prop("collisionGroup", "layer1") }, "terrain")
            //Set up some balls and a terrain on 'layer2' collision group
            nodeRef(params { x = 2f; y = 10f;prop("collisionGroup", "layer2") }, "ball")
            nodeRef(params { x = +8f; y = 11f; scaleX = 2f; scaleY = 2f;prop("collisionGroup", "layer2") }, "ball")
            nodeRef(params { x = 0f; y = -10f; scaleX = 20f;prop("collisionGroup", "layer2") }, "terrain")
        }

        JJ.scenes.instantiate(initialSceneSpec)
    }

    private fun ball(): SceneSpec {
        return SceneSpec {
            sounds.sound("bounce", "sounds/pong.mp3", volume = 0.6f)
            physics {
                fixture {
                    bodyType = BodyDef.BodyType.DynamicBody
                    shape = Circle()
                    restitution = 0.7f
                    // We bind the collision group to a property key
                    collisionGroupProperty("collisionGroup")
                    // We can add collisions to the fixture (called first)
                    collisions.begin(SoundCollision(soundName = "bounce"))
                }
                //And we can add collisions to the root (called after fixture collision handlers)
                collisions.begin(SoundCollision(soundName = "bounce"))
            }
        }
    }

    private fun floor(): SceneSpec {
        return SceneSpec {
            physics {
                bodyType = BodyDef.BodyType.StaticBody
                fixture {
                    // We bind the collision group to a property key
                    collisionGroupProperty("collisionGroup")
                }
            }
        }
    }
}
