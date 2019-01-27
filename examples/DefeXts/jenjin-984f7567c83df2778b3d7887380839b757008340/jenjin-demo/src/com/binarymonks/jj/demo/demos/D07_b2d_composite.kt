package com.binarymonks.jj.demo.demos

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.Array
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.JJConfig
import com.binarymonks.jj.core.JJGame
import com.binarymonks.jj.core.extensions.addVar
import com.binarymonks.jj.core.pools.vec2
import com.binarymonks.jj.core.specs.*


class D07_b2d_composite : JJGame(JJConfig {
    gameView.worldBoxWidth = 20f
    gameView.cameraPosX = 0f
    gameView.cameraPosY = 5f
}) {


    override fun gameOn() {

        JJ.scenes.addSceneSpec("swingHammer", swingHammer())
        JJ.scenes.addSceneSpec("spinner", spinner())
        JJ.scenes.addSceneSpec("slider", slider())
        JJ.scenes.addSceneSpec("terrain", floor())

        JJ.scenes.instantiate(SceneSpec {
            nodeRef(params { x = 8f; y = 7f; rotationD = 180f;scaleX = 0.5f; scaleY = 0.5f }, "swingHammer")
            nodeRef(params { x = 8f; y = 11f; rotationD = 180f;scaleX = 0.5f; scaleY = 0.5f }, "swingHammer")
            nodeRef(params { x = 8f; y = 4f; scaleX = 0.5f; scaleY = 0.5f }, "spinner")
            nodeRef("terrain")
            nodeRef(params { x = -1f; y = 15f; rotationD = -15f }, "slider")
            node(params { x = 7.5f; y = 2.5f }) {
                physics {
                    bodyType = BodyDef.BodyType.DynamicBody
                    fixture { shape = Circle(0.5f) }
                }
                render { circleRender(0.5f) { color.set(Color.PURPLE) } }
            }
        })

    }

    private fun slider(): SceneSpecRef {
        val sliderLength = 8f
        return SceneSpec {
            node(params { name = "anchor" }) {
                physics { bodyType = BodyDef.BodyType.StaticBody }
                render {
                    val chain: Array<Vector2> = Array<Vector2>().addVar(vec2(), vec2(sliderLength, 0f))
                    lineChainRender(chain) {
                        color.set(Color.BLUE)
                    }
                }
            }
            node(params { name = "ball" }) {
                physics {
                    bodyType = BodyDef.BodyType.DynamicBody
                    fixture { shape = Circle(0.5f) }
                }
                render { circleRender(0.5f) { color.set(Color.BROWN) } }
            }
            prismaticJoint("anchor", "ball") {
                collideConnected = false
                enableLimit = true
                upperTranslation = sliderLength
            }
        }
    }

    private fun floor(): SceneSpec {
        val floor = Chain(
                vec2(-10f, 2f),
                vec2(-8f, 1f),
                vec2(-6f, 0f),
                vec2(-4f, 0f),
                vec2(-2f, 0f),
                vec2(0f, 1f),
                vec2(2f, 1f),
                vec2(4f, 1f),
                vec2(6f, 2f),
                vec2(8f, 2f),
                vec2(10f, 2f)
        )
        return SceneSpec {
            physics {
                bodyType = BodyDef.BodyType.StaticBody
                fixture {
                    shape = floor
                }
            }
            render {
                lineChainRender(floor.vertices) {
                    color.set(Color.GREEN)
                }
            }
        }
    }

    private fun spinner(): SceneSpec {
        //Some forced weld joints, but they work
        return SceneSpec {
            node(params { name = "anchor" }) {
                physics { bodyType = BodyDef.BodyType.StaticBody }
                render { circleRender(0.25f) { color.set(Color.GREEN) } }
            }
            node(params { name = "arm" }) {
                physics {
                    bodyType = BodyDef.BodyType.DynamicBody
                    fixture { shape = Rectangle(0.5f, 4f) }
                }
                render { rectangleRender(0.5f, 4f) { color.set(Color.BROWN) } }
            }
            node(params { name = "topBall"; y = 2f }) {
                physics {
                    bodyType = BodyDef.BodyType.DynamicBody
                    fixture { shape = Circle(1f) }
                }
                render {
                    circleRender(1f) { color.set(Color.GRAY) }
                }
            }
            node(params { name = "bottomBall"; y = -2f }) {
                physics {
                    bodyType = BodyDef.BodyType.DynamicBody
                    fixture { shape = Circle(1f) }
                }
                render {
                    circleRender(1f) { color.set(Color.GRAY) }
                }
            }
            revJoint("anchor", "arm", vec2()) {}
            weldJoint("arm", "topBall", vec2(0f, 2f)) {}
            weldJoint("arm", "bottomBall", vec2(0f, -2f)) {}
        }
    }

    private fun swingHammer(): SceneSpec {
        return SceneSpec {
            node(params { name = "hammerAnchor" }) {
                physics { bodyType = BodyDef.BodyType.StaticBody }
            }
            node(params { name = "hammer" }) {
                physics {
                    bodyType = BodyDef.BodyType.DynamicBody
                    fixture { shape = Rectangle(0.5f, 3f); offsetY = -1.5f }
                    fixture { shape = Rectangle(2f, 2f); offsetY = -4f }
                    fixture { shape = Circle(1f); offsetY = -4f; offsetX = -1f }
                    fixture { shape = Circle(1f); offsetY = -4f; offsetX = 1f }
                }
                render {
                    rectangleRender(0.5f, 3f) { color.set(Color.BROWN); offsetY = -1.5f }
                    rectangleRender(2f, 2f) { color.set(Color.GRAY); offsetY = -4f }
                    circleRender(1f) { color.set(Color.GRAY); offsetY = -4f; offsetX = -1f }
                    circleRender(1f) { color.set(Color.GRAY); offsetY = -4f; offsetX = 1f }
                    circleRender(0.25f) { color.set(Color.GREEN); }
                }
            }
            revJoint("hammer", "hammerAnchor", vec2()) {}
        }
    }
}

