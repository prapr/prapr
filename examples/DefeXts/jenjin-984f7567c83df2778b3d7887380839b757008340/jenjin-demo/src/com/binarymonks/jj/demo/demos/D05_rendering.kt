package com.binarymonks.jj.demo.demos

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.physics.box2d.BodyDef
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.JJConfig
import com.binarymonks.jj.core.JJGame
import com.binarymonks.jj.core.pools.vec2
import com.binarymonks.jj.core.specs.Chain
import com.binarymonks.jj.core.specs.Circle
import com.binarymonks.jj.core.specs.Polygon
import com.binarymonks.jj.core.specs.SceneSpec


class D05_rendering : JJGame(JJConfig {
    b2d.debugRender = true

    gameView.worldBoxWidth = 30f
    gameView.cameraPosX = 0f
    gameView.cameraPosY = 0f
}) {
    override fun gameOn() {
        JJ.scenes.addSceneSpec("shapes", shapes())

        val initialScene = SceneSpec {
            nodeRef ( "shapes" )
        }

        JJ.scenes.instantiate(initialScene)
    }

    private fun line(): SceneSpec {
        return SceneSpec {
            render {
                polygonRender(
                        vec2(0f, 0f),
                        vec2(0f, 2f),
                        vec2(0f, 2f),
                        vec2(0f, 0f)
                ) {
                    color.set(Color.GREEN)
                }
            }
        }
    }

    //We can draw shapes - with the same dimensions as our fixtures
    private fun shapes(): SceneSpec {

        val poly = Polygon(
                vec2(-2f, 0f),
                vec2(-1.5f, 1.5f),
                vec2(0f, 2f),
                vec2(1.5f, 1.5f),
                vec2(2f, 0f),
                vec2(1.5f, -1.5f),
                vec2(0f, -2f),
                vec2(-1.5f, -1.5f)
        )
        val zigzag = Chain(
                vec2(-2f, 0f),
                vec2(-1f, 1f),
                vec2(0f, 0f),
                vec2(1f, 1f),
                vec2(2f, 0f)
        )

        return SceneSpec {

            // Polygon
            node {
                physics {
                    bodyType = BodyDef.BodyType.StaticBody
                    fixture {
                        offsetX = 4f
                        shape = poly
                    }
                }
                render {
                    polygonRender(poly.vertices) {
                        offsetX = 4f
                        color.set(Color.PINK)
                    }
                }
            }
            // Circle
            node {
                physics {
                    bodyType = BodyDef.BodyType.StaticBody
                    fixture {
                        offsetX = -4f
                        shape = Circle(2f)
                    }
                }
                render {
                    circleRender(2f) {
                        color.set(Color.YELLOW)
                        offsetX = -4f
                    }
                }
            }
            //LineChain
            node {
                physics {
                    bodyType = BodyDef.BodyType.StaticBody
                    fixture {
                        shape = zigzag
                        offsetY = -4f
                        rotationD = 90f
                    }
                }
                render {
                    lineChainRender(zigzag.vertices) {
                        color.set(Color.BLUE)
                        offsetY = -4f
                        rotationD = 90f
                    }
                }
            }
        }
    }
}