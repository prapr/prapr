package com.binarymonks.jj.demo.demos

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.physics.box2d.BodyDef
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.JJConfig
import com.binarymonks.jj.core.JJGame
import com.binarymonks.jj.core.specs.SceneSpec
import com.binarymonks.jj.core.specs.params


class D02_rendering_layers : JJGame(JJConfig {
    b2d.debugRender = true

    gameView.worldBoxWidth = 50f
    gameView.cameraPosX = 0f
    gameView.cameraPosY = 0f
}) {

    public override fun gameOn() {

        JJ.scenes.addSceneSpec("1", layer1())
        JJ.scenes.addSceneSpec("0", layer0())
        JJ.scenes.loadAssetsNow()

        JJ.scenes.instantiate("0")
        JJ.scenes.instantiate(params { y = 10f }, "1")
    }

    private fun layer0(): SceneSpec {
        return SceneSpec {
            physics {
                bodyType = BodyDef.BodyType.StaticBody
            }
            render {
                imageTexture("textures/layers/0_1.png") {
                    color.set(Color.ORANGE)
                    priority = 1
                    offsetX = 2.5f
                    offsetY = 2.5f
                    width = 30f
                    height = 30f
                }
                imageTexture("textures/layers/0_0.png") {
                    color.set(Color.CORAL)
                    priority = 0
                    width = 35f
                    height = 35f
                }
            }
        }
    }

    /**
     * Set the scenes layer
     */
    private fun layer1(): SceneSpec {
        return SceneSpec {
            layer = 1
            physics {
                bodyType = BodyDef.BodyType.StaticBody
            }
            render {
                imageTexture("textures/layers/1_1.png") {
                    color.set(Color.YELLOW)
                    priority = 1
                    offsetX = 5f
                    offsetY = 5f
                    width = 15f
                    height = 15f
                }
                imageTexture("textures/layers/1_0.png") {
                    color.set(Color.GREEN)
                    priority = 0
                    width = 25f
                    height = 25f
                }
            }
        }
    }
}


