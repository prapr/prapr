package com.binarymonks.jj.demo.demos

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.physics.box2d.BodyDef
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.JJConfig
import com.binarymonks.jj.core.JJGame
import com.binarymonks.jj.core.components.misc.Emitter
import com.binarymonks.jj.core.pools.vec2
import com.binarymonks.jj.core.specs.*


val spawnDelay = 0.01f
val destroyDelay = 1f
val poolEverything = false

class D08_pooling_and_destroying : JJGame(JJConfig {
    b2d.debugRender = true
    b2d.gravity = com.badlogic.gdx.math.Vector2()

    gameView.worldBoxWidth = 20f
    gameView.cameraPosX = 0f
    gameView.cameraPosY = 0f
}) {
    override fun gameOn() {

        JJ.scenes.addSceneSpec("nestedCompositeScene", nestedCompositeScene())
        JJ.scenes.addSceneSpec("circle", circle())


        JJ.scenes.instantiate(SceneSpec {
            //generator
            node(params { y = -3f;x = 3f }) {
                component(Emitter()) { sceneSpecRef = sceneRef("nestedCompositeScene"); delayMinSeconds = spawnDelay; delayMaxSeconds = spawnDelay }
                render {
                    circleRender(0.5f) { color.set(Color.YELLOW) }
                }
            }
            //scaled generator
            node(params { y = 3f;x = -3f }) {
                component(Emitter()) { sceneSpecRef = sceneRef("nestedCompositeScene"); delayMinSeconds = spawnDelay; delayMaxSeconds = spawnDelay; scaleX = 0.5f; scaleY = 0.5f }
                render {
                    circleRender(0.5f) { color.set(Color.PURPLE) }
                }
            }
        })

    }

    private fun circle(): SceneSpec {
        return SceneSpec {
            pooled = poolEverything
            physics {
                bodyType = BodyDef.BodyType.DynamicBody
                fixture { shape = Circle(1f) }
            }
            render {
                circleRender(1f) { color.set(Color.GREEN) }
            }
        }
    }

    private fun nestedCompositeScene(): SceneSpec {
        return SceneSpec {
            pooled = poolEverything
            component(com.binarymonks.jj.core.components.misc.SelfDestruct()) {
                delaySeconds = destroyDelay
            }
            node(params { name = "rectangle" }) {
                pooled = poolEverything
                physics {
                    bodyType = BodyDef.BodyType.DynamicBody
                    fixture { shape = Rectangle(1f, 1f) }
                }
                render {
                    rectangleRender(1f, 1f) { color.set(Color.BLUE) }
                }
            }
            node(params { name = "rectangle2"; y = 4f; rotationD = 45f }) {
                pooled = poolEverything
                physics {
                    bodyType = BodyDef.BodyType.DynamicBody
                    fixture { shape = Rectangle(1f, 1f) }
                }
                render {
                    rectangleRender(1f, 1f) { color.set(Color.BLUE) }
                }
            }
            nodeRef(params { name = "circle"; y = 2f }, "circle")
            weldJoint("rectangle", "circle", vec2()) {}
            weldJoint("rectangle", "rectangle2", vec2()) {}
        }
    }
}