package com.binarymonks.jj.demo.demos

import com.badlogic.gdx.physics.box2d.BodyDef
import com.binarymonks.jj.core.JJ
import com.binarymonks.jj.core.JJConfig
import com.binarymonks.jj.core.JJGame
import com.binarymonks.jj.core.pools.vec2
import com.binarymonks.jj.core.scenes.Scene
import com.binarymonks.jj.core.specs.Circle
import com.binarymonks.jj.core.specs.Polygon
import com.binarymonks.jj.core.specs.SceneSpec
import com.binarymonks.jj.core.specs.params


/**
 * The simplest of the simple
 *
 * Create some scenes and instantiate them.
 *
 * Box2D provides the physics, so being familiar with it is obviously very useful. There are loads of online resources.
 */
class D00_Basic : JJGame(JJConfig {
    b2d.debugRender = true
    gameView.worldBoxWidth = 30f
    gameView.cameraPosX = 0f
    gameView.cameraPosY = 10f
}) {

    public override fun gameOn() {

        //You can register material profiles so that you can quickly set the properties of your fixture
        JJ.physics.materials.registerMaterial("rubber", restitution = 0.8f, friction = 0.2f, density = 0.35f)
        JJ.physics.materials.registerMaterial("plastic", restitution = 0.1f, friction = 0.1f, density = 0.3f)
        JJ.physics.materials.registerMaterial("glass", restitution = 0.0f, friction = 0.05f, density = 0.65f)

        JJ.scenes.addSceneSpec("square", square())
        JJ.scenes.addSceneSpec("circle", circle())
        JJ.scenes.addSceneSpec("terrain", floor())

        JJ.scenes.instantiate(
                SceneSpec {
                    nodeRef(params {
                        x = -8f; y = 8f
                        prop("material", "rubber")
                    }, "circle" )
                    nodeRef(params {
                        x = -2f; y = 8f; scaleX = 2f; scaleY = 2f
                        prop("material", "plastic")
                    }
                    , "circle" )
                    nodeRef(params {
                        x = 2f; y = 8f
                        prop("material", "glass")
                    }, "circle" )
                    nodeRef(params {
                        x = +8f; y = 8f
                        prop("material", "rubber")
                    }, "circle" )
                    nodeRef(params {
                        x = -6f; y = 5f; rotationD = 45f
                        prop("material", "rubber")
                    }, "square" )
                    nodeRef(params {
                        x = +6f; y = 5f; rotationD = 45f
                        prop("material", "plastic")
                    }, "square" )
                    node(params { x = 0f; y = 6f }) {
                        physics {
                            bodyType = BodyDef.BodyType.StaticBody
                            fixture {
                                shape = Polygon(
                                        vec2(0f, 4f),
                                        vec2(-2f, 0f),
                                        vec2(2f, 0f)
                                )
                            }
                        }
                    }
                    nodeRef(params {
                        x = 0f; y = 0f; scaleX = 20f
                        prop("material", "glass")
                    },"terrain")
                }
        ).then(this::sceneCreated)
    }

    fun sceneCreated(scene: Scene) {
        println("Created the scene $scene")
    }

    private fun square(): SceneSpec {
        return SceneSpec {
            physics {
                bodyType = BodyDef.BodyType.DynamicBody
                fixture {
                    material.setOverride("material")
                }
            }
        }
    }

    private fun circle(): SceneSpec {
        return SceneSpec {
            physics {
                bodyType = BodyDef.BodyType.DynamicBody
                fixture {
                    shape = Circle()
                    restitution = 0.7f
                    material.setOverride("material")
                }
            }
        }
    }

    private fun floor(): SceneSpec {
        return SceneSpec {
            physics {
                bodyType = BodyDef.BodyType.StaticBody
                fixture {
                    material.set("glass")
                }
            }
        }
    }
}
