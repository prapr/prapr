package com.binarymonks.jj.demo.demos

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.physics.box2d.BodyDef
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.JJConfig
import com.binarymonks.jj.core.JJGame
import com.binarymonks.jj.core.components.input.Draggable
import com.binarymonks.jj.core.specs.Rectangle
import com.binarymonks.jj.core.specs.SceneSpec
import com.binarymonks.jj.core.specs.SceneSpecRef
import com.binarymonks.jj.core.specs.params
import com.binarymonks.jj.core.specs.render.RenderGraphType

/**
 * Be sure to enable touch in the JJConfig to use [com.binarymonks.jj.core.components.input.TouchHandler]s
 *
 * And also dim the Ambient light to be able to see your lights.
 */
class D06_lights_and_touch : JJGame(JJConfig {
    b2d.debugRender = false
    b2d.gravity = com.badlogic.gdx.math.Vector2()

    gameView.worldBoxWidth = 20f
    gameView.cameraPosX = 0f
    gameView.cameraPosY = 0f
}) {

    override fun gameOn() {

        JJ.scenes.addSceneSpec("box", box())
        JJ.assets.loadNow("textures/circuit_background.png", Texture::class)

        JJ.render.setAmbientLight(0f, 0f, 0f, 0.4f)

        JJ.scenes.instantiate(SceneSpec {
            container = true
            node {
                render { imageTexture("textures/circuit_background.png") { width = 20f; height = 40f; layer = 0 } }
            }
            nodeRef(params { x = -5f; y = 5f }, "box" )
            nodeRef(params { x = 5f; y = -5f }, "box" )

            /**
             * A touch draggable light
             */
            node {
                physics {
                    bodyType = BodyDef.BodyType.KinematicBody
                    fixture { shape = Rectangle(1.5f, 1.5f) }
                    pointLight {
                        reach = 20f
                        color.set(Color.LIME)
                    }
                }
                render {
                    rectangleRender(1.5f, 1.5f) {
                        renderGraphType = RenderGraphType.LIGHT
                        color.set(Color.LIME)
                    }
                }
                component(Draggable())
            }
        })
    }

    private fun box(): SceneSpecRef {
        return SceneSpec {
            layer = 1
            physics {
                bodyType = BodyDef.BodyType.DynamicBody
                linearDamping = 1f
                fixture { shape = Rectangle(2.5f, 2.5f) }
            }
            render { rectangleRender(2.5f, 2.5f) { color.set(Color.BLUE) } }
            component(Draggable())
        }
    }
}

